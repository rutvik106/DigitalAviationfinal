package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.databinding.ActivityOtpverifyBinding
import `in`.fusionbit.digitalaviation.extras.*
import `in`.fusionbit.digitalaviation.model.SubscriptionsModel
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class OTPVerifyActivity : AppCompatActivity() {

    private val TAG = OTPVerifyActivity::class.java.simpleName
    private lateinit var activityOtpverifyBinding: ActivityOtpverifyBinding
    private var mobileNo = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var subscriptionList: ArrayList<SubscriptionsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityOtpverifyBinding =
            DataBindingUtil.setContentView(this@OTPVerifyActivity, R.layout.activity_otpverify)

        progressDialog = ProgressDialog(this@OTPVerifyActivity)

        mobileNo = intent.getStringExtra(MOBILE_NO)

        activityOtpverifyBinding.btnLogin.setOnClickListener {
            if (activityOtpverifyBinding.etOtp.text.toString().trim().length < 4) {
                Toast.makeText(this@OTPVerifyActivity, "4 digit required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                verifyOtp()
            }
        }
    }

    private fun verifyOtp() {
        progressDialog.show()
        API().verify(
            mobileNo,
            activityOtpverifyBinding.etOtp.text.toString().trim(),
            object : ApiCallback<ResponseBody>() {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    super.onFailure(call, t)
                    progressDialog.cancel()
                    Toast.makeText(this@OTPVerifyActivity, "Something wrong!", Toast.LENGTH_SHORT)
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
                                this@OTPVerifyActivity, errorObject.getString("msg"),
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            val dataObject = responseObject.getJSONObject("data")
                            val token = dataObject.getString("token")


                            progressDialog.show()
                            login(token)
                        }
                    } catch (ex: Exception) {
                        progressDialog.cancel()
                        ex.printStackTrace()
                        Toast.makeText(
                            this@OTPVerifyActivity,
                            "Something wrong!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            })
    }

    private fun login(token: String) {
        progressDialog.show()
        API().login(
            mobileNo, token, object : ApiCallback<ResponseBody>() {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    super.onFailure(call, t)
                    progressDialog.cancel()
                    Toast.makeText(this@OTPVerifyActivity, "Something wrong!", Toast.LENGTH_SHORT)
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

                        subscriptionList = ArrayList()

                        if (errorObject.getString("error_code") == "0") {
                            val dataObject = responseObject.getJSONObject("data")
                            val userObject = dataObject.getJSONObject("user")
                            savePref(this@OTPVerifyActivity, USER_ID, userObject.getString("id"))
                            savePref(
                                this@OTPVerifyActivity,
                                USER_NAME,
                                userObject.getString("name")
                            )
                            savePref(
                                this@OTPVerifyActivity,
                                USER_MOBILE,
                                userObject.getString("phone")
                            )
                            savePref(
                                this@OTPVerifyActivity,
                                USER_EMAIL,
                                userObject.getString("email")
                            )

                            savePref(this@OTPVerifyActivity, TOKEN, token)

                            progressDialog.cancel()
                            finish()
                            startActivity(
                                Intent(
                                    this@OTPVerifyActivity,
                                    DashboardActivity::class.java
                                )
                            )

                        } else {
                            progressDialog.cancel()
                        }


                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        progressDialog.cancel()
                        Toast.makeText(
                            this@OTPVerifyActivity,
                            "Something wrong!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            })
    }
}
