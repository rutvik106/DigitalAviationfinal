package `in`.fusionbit.digitalaviation.fragments

import `in`.fusionbit.digitalaviation.PaymentActivity
import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.SubjectActivity
import `in`.fusionbit.digitalaviation.adapters.CourseAdapter
import `in`.fusionbit.digitalaviation.databinding.FragmentAboutBinding
import `in`.fusionbit.digitalaviation.dialogs.CourseDetailsDialog
import `in`.fusionbit.digitalaviation.extras.*
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

class AboutFragment : Fragment(), CourseAdapter.CourseClick {

    private lateinit var fragmentAboutBinding: FragmentAboutBinding
    private lateinit var courseList: ArrayList<CourseModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAboutBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Digital Aviation"

        if (getPref(activity!!, TOKEN) == "" || getPref(activity!!, TOKEN) == null) {
            callGetCourseList()
        } else {
            callGetCourseListAfterLogin()
        }


        return fragmentAboutBinding.root
    }

    private fun callGetCourseList() {
        API().getCourse(object : ApiCallback<ResponseBody>() {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                super.onFailure(call, t)
                Toast.makeText(activity, "Something wrong!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                super.onResponse(call, response)
                try {
                    val responseString = response.body()?.string()
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

                        fragmentAboutBinding.rvCourse.apply {
                            fragmentAboutBinding.rvCourse.hasFixedSize()
                            fragmentAboutBinding.rvCourse.layoutManager =
                                LinearLayoutManager(activity!!)
                            fragmentAboutBinding.rvCourse.adapter =
                                CourseAdapter(activity!!, courseList, this@AboutFragment)
                        }

                    } else {
                        Toast.makeText(
                            activity,
                            errorObject.getString("error_msg"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        })
    }

    private fun callGetCourseListAfterLogin() {
        API().getCourseAfterLogin(getPref(activity!!, USER_MOBILE),
            object : ApiCallback<ResponseBody>() {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    super.onFailure(call, t)
                    Toast.makeText(activity, "Something wrong!", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    super.onResponse(call, response)
                    try {
                        val responseString = response.body()?.string()
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
                                    obj.getString("created_at"),
                                    obj.getString("is_subscribed")
                                )
                                courseList.add(courseModel)
                            }

                            fragmentAboutBinding.rvCourse.apply {
                                fragmentAboutBinding.rvCourse.hasFixedSize()
                                fragmentAboutBinding.rvCourse.layoutManager =
                                    LinearLayoutManager(activity!!)
                                fragmentAboutBinding.rvCourse.adapter =
                                    CourseAdapter(activity!!, courseList, this@AboutFragment)
                            }

                        } else {
                            Toast.makeText(
                                activity,
                                errorObject.getString("error_msg"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            })
    }

    override fun onCourseClick(id: String, isSubscribed: String) {
        val intent = Intent(activity, SubjectActivity::class.java)
        intent.putExtra(COURSE_ID, id)
        intent.putExtra(IS_SUBSCRIBED, isSubscribed)
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
        val courseDialog = CourseDetailsDialog.display(
            fragmentManager!!,
            name, description, image, price, duration, createdDate
        )
    }

    override fun onBtnClick(id: String, isSubscribed: String) {
        if (isSubscribed == "1") {
            val intent = Intent(activity, SubjectActivity::class.java)
            intent.putExtra(COURSE_ID, id)
            intent.putExtra(IS_SUBSCRIBED, isSubscribed)
            activity!!.startActivity(intent)
        } else {
            val intent = Intent(activity!!, PaymentActivity::class.java)
            intent.putExtra(COURSE_ID, id)
            startActivity(intent)
        }
    }
}