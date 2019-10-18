package `in`.fusionbit.digitalaviation.fragments

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.adapters.LessonAdapter
import `in`.fusionbit.digitalaviation.databinding.FragmentLessonBinding
import `in`.fusionbit.digitalaviation.dialogs.ContentDetailsDialog
import `in`.fusionbit.digitalaviation.dialogs.LessonDetailsDialog
import `in`.fusionbit.digitalaviation.extras.COURSE_ID
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.extras.SUBJECT_ID
import `in`.fusionbit.digitalaviation.model.LessonModel
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class LessonFragment : Fragment(), LessonAdapter.LessonClick {

    private val TAG = LessonFragment::class.java.simpleName

    private lateinit var fragmentLessonBinding: FragmentLessonBinding
    private lateinit var progressDialog: ProgressDialog
    private var courseId: String = ""
    private var subjectId: String = ""
    private lateinit var lessonList: ArrayList<LessonModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLessonBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_lesson, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Lesson"

        courseId = arguments?.getString(COURSE_ID)!!
        subjectId = arguments?.getString(SUBJECT_ID)!!

        progressDialog = ProgressDialog(activity!!)

        CoroutineScope(Dispatchers.Main).launch {
            callGetLessonList()
        }

        return fragmentLessonBinding.root
    }

    private suspend fun callGetLessonList() {
        progressDialog.show()
        withContext(Dispatchers.Main) {
            API().getLesson(courseId, subjectId, object : ApiCallback<ResponseBody>() {
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

                            fragmentLessonBinding.rvLesson.apply {
                                fragmentLessonBinding.rvLesson.hasFixedSize()
                                fragmentLessonBinding.rvLesson.layoutManager =
                                    LinearLayoutManager(activity!!)
                                fragmentLessonBinding.rvLesson.adapter =
                                    LessonAdapter(lessonList, this@LessonFragment)
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

    override fun onLessonClick(
        name: String,
        url: String,
        id: String
    ) {
        Log.e("ID -> ", id)
        val dialog = ContentDetailsDialog.display(
            fragmentManager!!,
            name, url, id
        )
    }

    override fun onSingleItemData(
        name: String,
        description: String,
        image: String,
        duration: String,
        createdDate: String
    ) {
        val subjectDialog = LessonDetailsDialog.display(
            fragmentManager!!,
            name, description, image, duration, createdDate
        )
    }
}