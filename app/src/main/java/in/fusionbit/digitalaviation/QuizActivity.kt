package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.databinding.ActivityQuizBinding
import `in`.fusionbit.digitalaviation.dialogs.QuizAdapter
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.extras.QUIZ_ID
import `in`.fusionbit.digitalaviation.model.QuizOptions
import `in`.fusionbit.digitalaviation.model.QuizQuestion
import `in`.fusionbit.digitalaviation.model.RoatationModel
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class QuizActivity : AppCompatActivity() {

    private val TAG = QuizActivity::class.java.simpleName

    private lateinit var activityQuizBinding: ActivityQuizBinding
    private var quizId = ""
    private var title = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var quizQuestionList: ArrayList<QuizQuestion>
    private lateinit var quizOptionList: ArrayList<QuizOptions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityQuizBinding =
            DataBindingUtil.setContentView(this@QuizActivity, R.layout.activity_quiz)

        title = intent.getStringExtra("title")

        setSupportActionBar(activityQuizBinding.toolbar)
        supportActionBar?.title = title + " 's QUIZ"
        activityQuizBinding.toolbar.setNavigationOnClickListener { finish() }

        progressDialog = ProgressDialog(this@QuizActivity)

        quizId = intent.getStringExtra(QUIZ_ID)

        callGetQuiz(quizId)
    }

    private fun callGetQuiz(quizId: String?) {
        progressDialog.show()
        API().getQuiz(quizId!!, object : ApiCallback<ResponseBody>() {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                super.onFailure(call, t)
                progressDialog.cancel()
                Toast.makeText(this@QuizActivity, "Something wrong!", Toast.LENGTH_SHORT).show()
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

                        activityQuizBinding.rvQuizData.apply {
                            activityQuizBinding.rvQuizData.hasFixedSize()
                            activityQuizBinding.rvQuizData.layoutManager =
                                LinearLayoutManager(this@QuizActivity)
                            activityQuizBinding.rvQuizData.adapter =
                                QuizAdapter(this@QuizActivity, quizQuestionList)
                        }
                        progressDialog.cancel()

                    } else {
                        progressDialog.cancel()
                        Toast.makeText(
                            this@QuizActivity,
                            errorObject.getString("error_msg"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    progressDialog.cancel()
                    Toast.makeText(this@QuizActivity, "Something wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

    }
}
