package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.adapters.CourseAdapter
import `in`.fusionbit.digitalaviation.databinding.ActivityQuizCourseBinding
import `in`.fusionbit.digitalaviation.dialogs.CourseDetailsDialog
import `in`.fusionbit.digitalaviation.extras.COURSE_ID
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.model.CourseModel
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class QuizCourseActivity : AppCompatActivity(), CourseAdapter.CourseClick {
    private val TAG = QuizCourseActivity::class.java.simpleName
    private lateinit var binding: ActivityQuizCourseBinding

    private lateinit var progressDialog: ProgressDialog
    private lateinit var courseList: ArrayList<CourseModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this@QuizCourseActivity, R.layout.activity_quiz_course)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        progressDialog = ProgressDialog(this@QuizCourseActivity)

        callGetCourseList()
    }

    private fun callGetCourseList() {
        progressDialog.show()
        API().getCourse(object : ApiCallback<ResponseBody>() {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                super.onFailure(call, t)
                progressDialog.cancel()
                Toast.makeText(this@QuizCourseActivity, "Something wrong!", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                super.onResponse(call, response)
                try {
                    val responseString = response.body()?.string()
                    Log.e(TAG, responseString)
                    val jsonObject = JSONObject(responseString)
                    val responseObject = jsonObject.getJSONObject("response")
                    val errorObject = responseObject.getJSONObject("error")
                    if (errorObject.getString("error_code") == "0") {
                        courseList = ArrayList()
                        val dataObject = responseObject.getJSONObject("data")
                        val courseArray = dataObject.getJSONArray("courses")
                        for (i in 0 until courseArray.length()) {
                            val obj = courseArray.getJSONObject(i)
                            val courseModel = CourseModel(
                                obj.getString("id"),
                                obj.getString("name"),
                                obj.getString("description"),
                                obj.getString("short_description"),
                                obj.getString("image"),
                                obj.getString("pricing"),
                                obj.getString("duration"),
                                obj.getString("created_at")
                            )
                            courseList.add(courseModel)
                        }

                        binding.rvQuizCourses.apply {
                            binding.rvQuizCourses.hasFixedSize()
                            binding.rvQuizCourses.layoutManager =
                                LinearLayoutManager(this@QuizCourseActivity)
                            binding.rvQuizCourses.adapter =
                                CourseAdapter(this@QuizCourseActivity,courseList, this@QuizCourseActivity)
                        }
                        progressDialog.cancel()

                    } else {
                        progressDialog.cancel()
                        Toast.makeText(
                            this@QuizCourseActivity,
                            errorObject.getString("error_msg"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (ex: Exception) {
                    progressDialog.cancel()
                    ex.printStackTrace()
                    Toast.makeText(this@QuizCourseActivity, "Something wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    override fun onCourseClick(id: String) {
        val intent = Intent(this@QuizCourseActivity, QuizSubjectActivity::class.java)
        intent.putExtra(COURSE_ID, id)
        startActivity(intent)
    }

    override fun onSingleItemData(
        name: String,
        description: String,
        image: String,
        price: String,
        duration: String,
        createdDate: String
    ) {
        val courseDialog = CourseDetailsDialog.display(
            supportFragmentManager,
            name, description, image, price, duration, createdDate
        )
    }
}
