package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.adapters.LessonAdapter
import `in`.fusionbit.digitalaviation.databinding.ActivityLessionBinding
import `in`.fusionbit.digitalaviation.dialogs.ContentDetailsDialog
import `in`.fusionbit.digitalaviation.dialogs.LessonDetailsDialog
import `in`.fusionbit.digitalaviation.extras.*
import `in`.fusionbit.digitalaviation.model.LessonModel
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
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

class LessonActivity : AppCompatActivity(), LessonAdapter.LessonClick {
    private lateinit var activityLessionBinding: ActivityLessionBinding

    private val TAG = LessonActivity::class.java.simpleName
    private lateinit var progressDialog: ProgressDialog
    private var courseId: String = ""
    private var subjectId: String = ""
    private lateinit var lessonList: ArrayList<LessonModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLessionBinding =
            DataBindingUtil.setContentView(this@LessonActivity, R.layout.activity_lession)

        setSupportActionBar(activityLessionBinding.toolbar)

        courseId = intent.getStringExtra(COURSE_ID)!!
        subjectId = intent.getStringExtra(SUBJECT_ID)!!

        progressDialog = ProgressDialog(this@LessonActivity)
        activityLessionBinding.toolbar.setNavigationOnClickListener { finish() }

        callGetLessonList()
    }

    private fun callGetLessonList() {
        progressDialog.show()
        API().getLesson(courseId, subjectId, object : ApiCallback<ResponseBody>() {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                super.onFailure(call, t)
                progressDialog.cancel()
                Toast.makeText(this@LessonActivity, "Something wrong!", Toast.LENGTH_SHORT)
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
                        lessonList = ArrayList()
                        val dataObject = responseObject.getJSONObject("data")
                        val subjectArray = dataObject.getJSONArray("lessons")
                        for (i in 0 until subjectArray.length()) {
                            val obj = subjectArray.getJSONObject(i)
                            val subjectModel = LessonModel(
                                obj.getString("id"),
                                obj.getString("name"),
                                obj.getString("description"),
                                obj.getString("course_id"),
                                obj.getString("subject_id"),
                                obj.getString("short_description"),
                                obj.getString("link"),
                                obj.getString("image"),
                                obj.getString("duration"),
                                obj.getString("ext_1"),
                                obj.getString("ext_2"),
                                obj.getString("created_at"),
                                obj.getString("updated_at")
                            )
                            lessonList.add(subjectModel)
                        }

                        activityLessionBinding.rvLessons.apply {
                            activityLessionBinding.rvLessons.hasFixedSize()
                            activityLessionBinding.rvLessons.layoutManager =
                                LinearLayoutManager(this@LessonActivity)
                            activityLessionBinding.rvLessons.adapter =
                                LessonAdapter(lessonList, this@LessonActivity)
                        }
                        progressDialog.cancel()

                    } else {
                        progressDialog.cancel()
                        Toast.makeText(
                            this@LessonActivity,
                            errorObject.getString("error_msg"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (ex: Exception) {
                    progressDialog.cancel()
                    ex.printStackTrace()
                    Toast.makeText(this@LessonActivity, "Something wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    override fun onLessonClick(
        name: String,
        url: String,
        id: String
    ) {
        if (getPref(this@LessonActivity, TOKEN) == "") {
            Toast.makeText(this@LessonActivity, "Please Login", Toast.LENGTH_SHORT).show()
        } else {
            val dialog = ContentDetailsDialog.display(
                supportFragmentManager,
                name, url, id
            )
        }
    }

    override fun onSingleItemData(
        name: String,
        description: String,
        image: String,
        duration: String,
        createdDate: String
    ) {
        val subjectDialog = LessonDetailsDialog.display(
            supportFragmentManager,
            name, description, image, duration, createdDate
        )
    }
}
