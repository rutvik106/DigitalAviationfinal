package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.databinding.ActivityLoginBinding
import `in`.fusionbit.digitalaviation.extras.MOBILE_NO
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.extras.TOKEN
import `in`.fusionbit.digitalaviation.extras.getPref
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.net.HttpURLConnection

class LoginActivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.simpleName
    private lateinit var activityLoginBinding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding =
            DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)

        progressDialog = ProgressDialog(this@LoginActivity)

        activityLoginBinding.btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        activityLoginBinding.btnLogin.setOnClickListener {
            if (activityLoginBinding.etMobile.text.toString().trim().length < 10) {
                Toast.makeText(this@LoginActivity, "10 digit required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                callRegistration()
            }
        }
    }

    private fun callRegistration() {
        progressDialog.show()
        API().register(activityLoginBinding.etMobile.text.toString().trim(),
            object : ApiCallback<ResponseBody>() {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    super.onFailure(call, t)
                    progressDialog.cancel()
                    Toast.makeText(this@LoginActivity, "Something wrong!", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    super.onResponse(call, response)

                    try {

                        if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                            val error = response.errorBody()?.string()
                            Log.e("ERROR -> ", error)
                            val jsonObject = JSONObject(error)
                            val responseObject = jsonObject.getJSONObject("response")
                            val errorObject = responseObject.getJSONObject("error")
                            if (errorObject.getString("error_code") == "1") {
                                Toast.makeText(
                                    this@LoginActivity,
                                    errorObject.getString("error_msg"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            val responseString = response.body()?.string()
                            val jsonObject = JSONObject(responseString)
                            val responseObject = jsonObject.getJSONObject("response")
                            val errorObject = responseObject.getJSONObject("error")
                            if (errorObject.getString("error_code") == "0") {
                                Toast.makeText(
                                    this@LoginActivity, errorObject.getString("msg"),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                finish()
                                val intent =
                                    Intent(this@LoginActivity, OTPVerifyActivity::class.java)
                                intent.putExtra(
                                    MOBILE_NO,
                                    activityLoginBinding.etMobile.text.toString().trim()
                                )
                                startActivity(intent)
                            } else if (errorObject.getString("error_code") == "1") {
                                Toast.makeText(
                                    this@LoginActivity,
                                    errorObject.getString("error_msg"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        Toast.makeText(
                            this@LoginActivity,
                            "Something wrong!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    progressDialog.cancel()
                }
            })
    }

}
