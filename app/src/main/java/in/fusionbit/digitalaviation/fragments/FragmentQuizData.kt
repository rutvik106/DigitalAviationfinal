package `in`.fusionbit.digitalaviation.fragments

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.adapters.QuizDataAdapter
import `in`.fusionbit.digitalaviation.databinding.FragmentQuizDataBinding
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.extras.QUIZ_ID
import `in`.fusionbit.digitalaviation.extras.SUBJECT_ID
import `in`.fusionbit.digitalaviation.model.QuizDataModel
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response


class FragmentQuizData : Fragment(), QuizDataAdapter.QuizDataCallback {


    private val TAG = FragmentQuizData::class.java.simpleName
    private lateinit var fragmentQuizDataBinding: FragmentQuizDataBinding
    private lateinit var progressDialog: ProgressDialog
    private var subjectId = ""
    private lateinit var quizDataList: ArrayList<QuizDataModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentQuizDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_quiz_data, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Quiz"

        subjectId = arguments!!.getString(SUBJECT_ID)

        progressDialog = ProgressDialog(activity!!)
        getQuizData(subjectId)

        return fragmentQuizDataBinding.root
    }

    private fun getQuizData(subjectId: String) {
        progressDialog.show()

        API().getQuizData(subjectId, object : ApiCallback<ResponseBody>() {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                super.onFailure(call, t)
                progressDialog.cancel()
                Toast.makeText(activity, "Something wrong!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                super.onResponse(call, response)
                try {
                    val responseString = response.body()?.string()
                    Log.e(TAG, responseString)
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

                        fragmentQuizDataBinding.rvQuizData.apply {
                            fragmentQuizDataBinding.rvQuizData.hasFixedSize()
                            fragmentQuizDataBinding.rvQuizData.layoutManager =
                                GridLayoutManager(activity, 2)
                            fragmentQuizDataBinding.rvQuizData.addItemDecoration(
                                GridSpacingItemDecoration(
                                    2, dpToPx(10),
                                    true
                                )
                            )
                            fragmentQuizDataBinding.rvQuizData.itemAnimator = DefaultItemAnimator()
                            fragmentQuizDataBinding.rvQuizData.adapter =
                                QuizDataAdapter(quizDataList, this@FragmentQuizData)
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
                    ex.printStackTrace()
                    progressDialog.cancel()
                    Toast.makeText(activity, "Something wrong!", Toast.LENGTH_SHORT).show()
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

    override fun quizData(id: String, title: String) {
        val bundle = Bundle()
        bundle.putString(QUIZ_ID, id)
        val fragment = QuizFragment()
        fragment.arguments = bundle
        fragmentManager!!.beginTransaction().replace(R.id.main_frame, fragment)
            .addToBackStack(null).commit()
    }
}