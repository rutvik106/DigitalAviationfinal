package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.adapters.SubscriptionAdapter
import `in`.fusionbit.digitalaviation.databinding.ActivitySubscriptionBinding
import `in`.fusionbit.digitalaviation.extras.*
import `in`.fusionbit.digitalaviation.model.SubscriptionsModel
import `in`.fusionbit.digitalaviation.network.API
import `in`.fusionbit.digitalaviation.network.ApiCallback
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class SubscriptionActivity : AppCompatActivity() {

    private val TAG = SubscriptionActivity::class.java.simpleName
    private lateinit var activitySubscriptionBinding: ActivitySubscriptionBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var subscriptionList: ArrayList<SubscriptionsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySubscriptionBinding = DataBindingUtil.setContentView(
            this@SubscriptionActivity,
            R.layout.activity_subscription
        )

        progressDialog = ProgressDialog(this@SubscriptionActivity)

        setSupportActionBar(activitySubscriptionBinding.toolbar)
        activitySubscriptionBinding.toolbar.setNavigationOnClickListener { finish() }

        activitySubscriptionBinding.userName.text = getPref(this@SubscriptionActivity!!, USER_NAME)
        activitySubscriptionBinding.userEmail.text =
            getPref(this@SubscriptionActivity!!, USER_EMAIL)

        getSubscriptions()

    }

    private fun getSubscriptions() {
        progressDialog.show()
        API().login(getPref(this@SubscriptionActivity, USER_MOBILE), getPref(
            this@SubscriptionActivity,
            TOKEN
        ), object : ApiCallback<ResponseBody>() {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                super.onFailure(call, t)
                progressDialog.cancel()
                Toast.makeText(this@SubscriptionActivity, "Server error", Toast.LENGTH_SHORT).show()
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
                        val subscriptionsArray = dataObject.getJSONArray("subscriptions")

                        for (i in 0 until subscriptionsArray.length()) {
                            val obj = subscriptionsArray.getJSONObject(i)
                            val model = SubscriptionsModel(
                                obj.getString("id"),
                                obj.getString("order_no"),
                                obj.getString("subscription_date"),
                                obj.getString("expiration_date"),
                                obj.getString("name"),
                                obj.getString("pricing"),
                                obj.getString("expired")
                            )
                            subscriptionList.add(model)
                        }

                        activitySubscriptionBinding.rvList.apply {
                            activitySubscriptionBinding.rvList.hasFixedSize()
                            activitySubscriptionBinding.rvList.layoutManager =
                                LinearLayoutManager(this@SubscriptionActivity)
                            activitySubscriptionBinding.rvList.adapter =
                                SubscriptionAdapter(subscriptionList)
                        }

                        progressDialog.cancel()
                    } else {
                        progressDialog.cancel()
                    }

                } catch (ex: Exception) {
                    progressDialog.cancel()
                    Toast.makeText(
                        this@SubscriptionActivity,
                        "Something wrong!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}
