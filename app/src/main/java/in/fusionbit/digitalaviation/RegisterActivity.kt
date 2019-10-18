package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.databinding.ActivityRegisterBinding
import `in`.fusionbit.digitalaviation.extras.MOBILE_NO
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
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

class RegisterActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.java.simpleName
    private lateinit var activityRegisterBinding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRegisterBinding =
            DataBindingUtil.setContentView(this@RegisterActivity, R.layout.activity_register)

        progressDialog = ProgressDialog(this@RegisterActivity)

        activityRegisterBinding.btnRegister.setOnClickListener {
            if (activityRegisterBinding.etMobile.text.toString().trim().length < 10) {
                Toast.makeText(this@RegisterActivity, "10 digit required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                callRegistration()
            }
        }
    }

    private fun callRegistration() {
        progressDialog.show()
        API().register(activityRegisterBinding.etMobile.text.toString().trim(),
            object : ApiCallback<ResponseBody>() {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    super.onFailure(call, t)
                    progressDialog.cancel()
                    Toast.makeText(this@RegisterActivity, "Something wrong!", Toast.LENGTH_SHORT)
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
                            progressDialog.cancel()
                            Toast.makeText(
                                this@RegisterActivity, errorObject.getString("msg"),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            finish()
                            val intent =
                                Intent(this@RegisterActivity, OTPVerifyActivity::class.java)
                            intent.putExtra(
                                MOBILE_NO,
                                activityRegisterBinding.etMobile.text.toString().trim()
                            )
                            startActivity(intent)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        progressDialog.cancel()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Something wrong!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            })
    }
}
