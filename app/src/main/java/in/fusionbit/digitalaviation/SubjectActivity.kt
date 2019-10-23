package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.adapters.SubjectAdapter
import `in`.fusionbit.digitalaviation.databinding.ActivitySubjectBinding
import `in`.fusionbit.digitalaviation.dialogs.SubjectDetailsDialog
import `in`.fusionbit.digitalaviation.extras.COURSE_ID
import `in`.fusionbit.digitalaviation.extras.IS_SUBSCRIBED
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.extras.SUBJECT_ID
import `in`.fusionbit.digitalaviation.model.SubjectModel
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class SubjectActivity : AppCompatActivity(), SubjectAdapter.SubjectClick {

    private val TAG = SubjectActivity::class.java.simpleName

    private lateinit var activitySubjectBinding: ActivitySubjectBinding
    private lateinit var progressDialog: ProgressDialog
    var courseId = ""
    var isSubscribed = ""
    private lateinit var subjectList: ArrayList<SubjectModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySubjectBinding =
            DataBindingUtil.setContentView(this@SubjectActivity, R.layout.activity_subject)

        setSupportActionBar(activitySubjectBinding.toolbar)
        activitySubjectBinding.toolbar.setNavigationOnClickListener { finish() }

        progressDialog = ProgressDialog(this@SubjectActivity)

        courseId = intent.getStringExtra(COURSE_ID)
        isSubscribed = intent.getStringExtra(IS_SUBSCRIBED)

        getsubject(courseId)
    }

    private fun getsubject(courseId: String?) {
        progressDialog.show()
        API().getSubject(courseId!!, object : ApiCallback<ResponseBody>() {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                super.onFailure(call, t)
                progressDialog.cancel()
                Toast.makeText(this@SubjectActivity, "Something wrong!", Toast.LENGTH_SHORT).show()
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

                        activitySubjectBinding.rvSubjects.apply {
                            activitySubjectBinding.rvSubjects.hasFixedSize()
                            activitySubjectBinding.rvSubjects.layoutManager =
                                GridLayoutManager(this@SubjectActivity, 2)
                            activitySubjectBinding.rvSubjects.addItemDecoration(
                                GridSpacingItemDecoration(2, dpToPx(10), true)
                            )
                            activitySubjectBinding.rvSubjects.adapter =
                                SubjectAdapter(
                                    this@SubjectActivity,
                                    subjectList,
                                    this@SubjectActivity
                                )
                        }
                        progressDialog.cancel()

                    } else {
                        progressDialog.cancel()
                        Toast.makeText(
                            this@SubjectActivity,
                            errorObject.getString("error_msg"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (ex: Exception) {
                    progressDialog.cancel()
                    ex.printStackTrace()
                    Toast.makeText(this@SubjectActivity, "Something wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    override fun onSubjectClick(courseId: String, subjectId: String, name: String) {
        val intent = Intent(this@SubjectActivity, LessonActivity::class.java)
        intent.putExtra(COURSE_ID, courseId)
        intent.putExtra(SUBJECT_ID, subjectId)
        intent.putExtra("subject_name", name)
        intent.putExtra(IS_SUBSCRIBED, isSubscribed)
        startActivity(intent)

//        val bundle = Bundle()
//        bundle.putString(COURSE_ID, courseId)
//        bundle.putString(SUBJECT_ID, subjectId)
//        val fragment = LessonFragment()
//        fragment.arguments = bundle
//        fragmentManager!!.beginTransaction().replace(R.id.other_container, fragment)
//            .addToBackStack(null).commit()
    }

    override fun onSingleItemData(
        name: String,
        description: String,
        image: String,
        duration: String,
        createdDate: String
    ) {
        val subjectDialog = SubjectDetailsDialog.display(
            supportFragmentManager!!,
            name, description, image, duration, createdDate
        )
    }


    inner class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left =
                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =
                    (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val r = resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            )
        )
    }
}
