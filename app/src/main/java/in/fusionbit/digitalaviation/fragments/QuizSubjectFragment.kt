package `in`.fusionbit.digitalaviation.fragments

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.adapters.SubjectAdapter
import `in`.fusionbit.digitalaviation.databinding.FragmentSubjectBinding
import `in`.fusionbit.digitalaviation.dialogs.SubjectDetailsDialog
import `in`.fusionbit.digitalaviation.extras.COURSE_ID
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.extras.SUBJECT_ID
import `in`.fusionbit.digitalaviation.model.SubjectModel
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

class QuizSubjectFragment : Fragment(), SubjectAdapter.SubjectClick {

    private val TAG = SubjectFragment::class.java.simpleName
    private lateinit var fragmentSubjectBinding: FragmentSubjectBinding
    private var courseId: String = ""
    private lateinit var subjectList: ArrayList<SubjectModel>
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSubjectBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_subject, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Subjects"

        courseId = arguments?.getString(COURSE_ID)!!

        progressDialog = ProgressDialog(activity!!)

        CoroutineScope(Dispatchers.Main).launch {
            callGetSubjectList()
        }

        return fragmentSubjectBinding.root
    }

    private suspend fun callGetSubjectList() {
        progressDialog.show()
        withContext(Dispatchers.Main) {
            API().getSubject(courseId, object : ApiCallback<ResponseBody>() {
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
                            subjectList = ArrayList()
                            val dataObject = responseObject.getJSONObject("data")
                            val subjectArray = dataObject.getJSONArray("subjects")
                            for (i in 0 until subjectArray.length()) {
                                val obj = subjectArray.getJSONObject(i)
                                val subjectModel = SubjectModel(
                                    obj.getString("id"),
                                    obj.getString("name"),
                                    obj.getString("course_id"),
                                    obj.getString("description"),
                                    obj.getString("short_description"),
                                    obj.getString("link"),
                                    obj.getString("image"),
                                    obj.getString("duration"),
                                    obj.getString("ext_1"),
                                    obj.getString("ext_2"),
                                    obj.getString("created_at"),
                                    obj.getString("updated_at")
                                )
                                subjectList.add(subjectModel)
                            }

                            fragmentSubjectBinding.rvSubject.apply {
                                fragmentSubjectBinding.rvSubject.hasFixedSize()
                                fragmentSubjectBinding.rvSubject.layoutManager =
                                    LinearLayoutManager(activity!!)
                                fragmentSubjectBinding.rvSubject.adapter =
                                    SubjectAdapter(
                                        activity!!,
                                        subjectList,
                                        this@QuizSubjectFragment
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

    override fun onSubjectClick(courseId: String, subjectId: String, name: String) {
        val bundle = Bundle()
        bundle.putString(SUBJECT_ID, subjectId)
        val fragment = FragmentQuizData()
        fragment.arguments = bundle
        fragmentManager!!.beginTransaction().replace(R.id.main_frame, fragment)
            .addToBackStack(null).commit()
    }

    override fun onSingleItemData(
        name: String,
        description: String,
        image: String,
        duration: String,
        createdDate: String
    ) {
        val subjectDialog = SubjectDetailsDialog.display(
            fragmentManager!!,
            name, description, image, duration, createdDate
        )
    }
}