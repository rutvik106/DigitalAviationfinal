package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.adapters.QuizDataAdapter
import `in`.fusionbit.digitalaviation.databinding.ActivityQuizSelectionBinding
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.extras.QUIZ_ID
import `in`.fusionbit.digitalaviation.extras.SUBJECT_ID
import `in`.fusionbit.digitalaviation.model.QuizDataModel
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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class QuizSelectionActivity : AppCompatActivity(), QuizDataAdapter.QuizDataCallback {

    private val TAG = QuizSelectionActivity::class.java

    private lateinit var binding: ActivityQuizSelectionBinding

    private lateinit var progressDialog: ProgressDialog
    private var subjectId = ""
    private lateinit var quizDataList: ArrayList<QuizDataModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_selection)

        progressDialog = ProgressDialog(this@QuizSelectionActivity)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        subjectId = intent.getStringExtra(SUBJECT_ID)
        getQuizData(subjectId)
    }

    private fun getQuizData(subjectId: String) {
        progressDialog.show()

        API().getQuizData(subjectId, object : ApiCallback<ResponseBody>() {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                super.onFailure(call, t)
                progressDialog.cancel()
                Toast.makeText(this@QuizSelectionActivity, "Something wrong!", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                super.onResponse(call, response)
                try {
                    val responseString = response.body()?.string()
                    Log.e("", responseString)
                    val jsonObject = JSONObject(responseString)
                    val responseObject = jsonObject.getJSONObject("response")
                    val errorObject = responseObject.getJSONObject("error")

                    if (errorObject.getString("error_code") == "0") {

                        val dataObject = responseObject.getJSONObject("data")
                        val quizArray = dataObject.getJSONArray("quiz")
                        quizDataList = ArrayList()

                        for (i in 0 until quizArray.length()) {
                            val questionObj = quizArray.getJSONObject(i)

                            val data = QuizDataModel(
                                questionObj.getString("id"),
                                questionObj.getString("name")
                            )

                            quizDataList.add(data)
                        }

                        binding.rvQuizSelection.apply {
                            binding.rvQuizSelection.hasFixedSize()
                            binding.rvQuizSelection.layoutManager =
                                GridLayoutManager(this@QuizSelectionActivity, 2)
                            binding.rvQuizSelection.addItemDecoration(
                                GridSpacingItemDecoration(
                                    2, dpToPx(10),
                                    true
                                )
                            )
                            binding.rvQuizSelection.itemAnimator = DefaultItemAnimator()
                            binding.rvQuizSelection.adapter =
                                QuizDataAdapter(quizDataList, this@QuizSelectionActivity)
                        }
                        progressDialog.cancel()

                    } else {
                        progressDialog.cancel()
                        Toast.makeText(
                            this@QuizSelectionActivity,
                            errorObject.getString("error_msg"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    progressDialog.cancel()
                    Toast.makeText(
                        this@QuizSelectionActivity,
                        "Something wrong!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
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

    override fun quizData(id: String) {
        val intent = Intent(this@QuizSelectionActivity, QuizActivity::class.java)
        intent.putExtra(QUIZ_ID, id)
        startActivity(intent)
    }
}
