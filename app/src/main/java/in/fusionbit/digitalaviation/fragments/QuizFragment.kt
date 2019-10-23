package `in`.fusionbit.digitalaviation.fragments

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.adapters.QuizOptionAdapter
import `in`.fusionbit.digitalaviation.databinding.FragmentQuizBinding
import `in`.fusionbit.digitalaviation.dialogs.QuizAdapter
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.extras.QUIZ_ID
import `in`.fusionbit.digitalaviation.model.QuizOptions
import `in`.fusionbit.digitalaviation.model.QuizQuestion
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
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

class QuizFragment : Fragment() {

    private val TAG = QuizFragment::class.java.simpleName
    private lateinit var fragmentQuizBinding: FragmentQuizBinding
    private var quizId = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var quizQuestionList: ArrayList<QuizQuestion>
    private lateinit var quizOptionList: ArrayList<QuizOptions>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentQuizBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_quiz, container, false)

        progressDialog = ProgressDialog(activity!!)

        quizId = arguments!!.getString(QUIZ_ID)

        callGetQuiz(quizId)

        return fragmentQuizBinding.root
    }

    private fun callGetQuiz(quizId: String?) {
        progressDialog.show()
        API().getQuiz(quizId!!, object : ApiCallback<ResponseBody>() {
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
                        quizQuestionList = ArrayList()
                        quizOptionList = ArrayList()
                        val dataObject = responseObject.getJSONObject("data")
                        val quizArray = dataObject.getJSONArray("questions")

                        for (i in 0 until quizArray.length()) {

                            val questionObj = quizArray.getJSONObject(i)


                            val optionArray = questionObj.getJSONArray("options")

                            for (j in 0 until optionArray.length()) {
                                val optionObj = optionArray.getJSONObject(j)

                                val quizOptions = QuizOptions(
                                    optionObj.getString("question_id"),
                                    optionObj.getString("option"),
                                    optionObj.getString("is_right_option"),
                                    optionObj.getString("reason")
                                )
                                quizOptionList.add(quizOptions)
                            }

                            val quizQuestion = QuizQuestion(
                                questionObj.getString("id"),
                                questionObj.getString("question"),
                                quizOptionList
                            )

                            quizQuestionList.add(quizQuestion)
                        }

                        fragmentQuizBinding.rvQuiz.apply {
                            fragmentQuizBinding.rvQuiz.hasFixedSize()
                            fragmentQuizBinding.rvQuiz.layoutManager = LinearLayoutManager(
                                activity!!,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            fragmentQuizBinding.rvQuiz.adapter =
                                QuizAdapter(activity!!, quizQuestionList)
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

}