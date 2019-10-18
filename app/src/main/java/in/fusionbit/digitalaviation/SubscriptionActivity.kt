package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.adapters.SubscriptionAdapter
import `in`.fusionbit.digitalaviation.databinding.ActivitySubscriptionBinding
import `in`.fusionbit.digitalaviation.extras.*
import `in`.fusionbit.digitalaviation.model.SubscriptionsModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var activitySubscriptionBinding: ActivitySubscriptionBinding
    private lateinit var list: ArrayList<SubscriptionsModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySubscriptionBinding = DataBindingUtil.setContentView(
            this@SubscriptionActivity,
            R.layout.activity_subscription
        )


        setSupportActionBar(activitySubscriptionBinding.toolbar)
        activitySubscriptionBinding.toolbar.setNavigationOnClickListener { finish() }

        activitySubscriptionBinding.userName.text = getPref(this@SubscriptionActivity!!, USER_NAME)
        activitySubscriptionBinding.userEmail.text =
            getPref(this@SubscriptionActivity!!, USER_EMAIL)

        list = getArrayListPref(this@SubscriptionActivity, SUBSCRIPTION)

        if (list.size > 0) {
            activitySubscriptionBinding.rvList.apply {
                activitySubscriptionBinding.rvList.hasFixedSize()
                activitySubscriptionBinding.rvList.layoutManager =
                    LinearLayoutManager(this@SubscriptionActivity)
                activitySubscriptionBinding.rvList.adapter = SubscriptionAdapter(list)
            }
        } else {
            Toast.makeText(
                this@SubscriptionActivity,
                "No Subscription available",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
