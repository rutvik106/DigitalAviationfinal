package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.adapters.QuizSubjectAdapter
import `in`.fusionbit.digitalaviation.adapters.SubjectAdapter
import `in`.fusionbit.digitalaviation.databinding.ActivityQuizSubjectBinding
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

class QuizSubjectActivity : AppCompatActivity(), QuizSubjectAdapter.SubjectClick {

    private val TAG = QuizSubjectActivity::class.java.simpleName

    private lateinit var binding: ActivityQuizSubjectBinding
    private lateinit var progressDialog: ProgressDialog
    private var courseId: String = ""
    private var isSubscribed = ""
    private lateinit var subjectList: ArrayList<SubjectModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_subject)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        progressDialog = ProgressDialog(this)

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
                Toast.makeText(this@QuizSubjectActivity, "Something wrong!", Toast.LENGTH_SHORT)
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

                        binding.rvQuizSubject.apply {
                            binding.rvQuizSubject.hasFixedSize()
                            binding.rvQuizSubject.layoutManager =
                                GridLayoutManager(this@QuizSubjectActivity, 2)
                            binding.rvQuizSubject.addItemDecoration(
                                GridSpacingItemDecoration(2, dpToPx(10), true)
                            )
                            binding.rvQuizSubject.adapter =
                                QuizSubjectAdapter(
                                    this@QuizSubjectActivity,
                                    subjectList,
                                    this@QuizSubjectActivity
                                )
                        }
                        progressDialog.cancel()

                    } else {
                        progressDialog.cancel()
                        Toast.makeText(
                            this@QuizSubjectActivity,
                            errorObject.getString("error_msg"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (ex: Exception) {
                    progressDialog.cancel()
                    ex.printStackTrace()
                    Toast.makeText(this@QuizSubjectActivity, "Something wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    override fun onSubjectClick(courseId: String, subjectId: String, name: String) {
        val intent = Intent(this, QuizSelectionActivity::class.java)
        intent.putExtra(SUBJECT_ID, subjectId)
        intent.putExtra("subject_name", name)
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
