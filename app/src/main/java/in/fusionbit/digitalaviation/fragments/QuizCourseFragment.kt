package `in`.fusionbit.digitalaviation.fragments

import `in`.fusionbit.digitalaviation.QuizSubjectActivity
import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.adapters.CourseAdapter
import `in`.fusionbit.digitalaviation.databinding.FragmentQuizCourseBinding
import `in`.fusionbit.digitalaviation.extras.COURSE_ID
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.model.CourseModel
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class QuizCourseFragment : Fragment(), CourseAdapter.CourseClick {

    private val TAG = QuizCourseFragment::class.java.simpleName

    private lateinit var fragmentQuizCourseBinding: FragmentQuizCourseBinding
    private lateinit var courseList: ArrayList<CourseModel>
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentQuizCourseBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_quiz_course, container, false)

        progressDialog = ProgressDialog(activity!!)

        CoroutineScope(Dispatchers.Main).launch {
            callGetCourseList()
        }

        return fragmentQuizCourseBinding.root
    }

    private suspend fun callGetCourseList() {
        progressDialog.show()
        withContext(Dispatchers.Main) {
            API().getCourse(object : ApiCallback<ResponseBody>() {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    super.onFailure(call, t)
                    progressDialog.cancel()
                    Toast.makeText(activity, "Something wrong!", Toast.LENGTH_SHORT).show()
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

                            fragmentQuizCourseBinding.rvQuizCourse.apply {
                                fragmentQuizCourseBinding.rvQuizCourse.hasFixedSize()
                                fragmentQuizCourseBinding.rvQuizCourse.layoutManager =
                                    LinearLayoutManager(activity!!)
                                fragmentQuizCourseBinding.rvQuizCourse.adapter =
                                    CourseAdapter(
                                        activity!!,
                                        courseList,
                                        this@QuizCourseFragment
                                    )
                            }
                            progressDialog.cancel()

                        } else {
                            progressDialog.cancel()
                            Toast.makeText(
                                activity,
                                errorObject.getString("error_msg"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (ex: Exception) {
                        progressDialog.cancel()
                        ex.printStackTrace()
                        Toast.makeText(activity, "Something wrong!", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    override fun onCourseClick(id: String) {
        val intent = Intent(activity!!, QuizSubjectActivity::class.java)
        intent.putExtra(COURSE_ID, id)
        activity!!.startActivity(intent)
    }

    override fun onSingleItemData(
        name: String,
        description: String,
        image: String,
        price: String,
        duration: String,
        createdDate: String
    ) {

    }
}