package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.databinding.ActivityPaymentBinding
import `in`.fusionbit.digitalaviation.extras.*
import android.content.DialogInterface
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.databinding.DataBindingUtil

class PaymentActivity : AppCompatActivity() {

    private lateinit var activityPaymentBinding: ActivityPaymentBinding
    private var courseId = ""
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPaymentBinding =
            DataBindingUtil.setContentView(this@PaymentActivity, R.layout.activity_payment)

        courseId = intent.getStringExtra(COURSE_ID)

        progressDialog = ProgressDialog(this@PaymentActivity)

        var mainLink = ""

        if (getPref(this@PaymentActivity, TOKEN) == "") {
            mainLink = "https://www.digitalaviation.in/subscriptions.php?id=" + courseId
        } else {
            mainLink = PAYMENT_LINK + "course_id=" + courseId + "&user_id=" + getPref(
                this@PaymentActivity,
                USER_ID
            ) + "&token=" + getPref(this@PaymentActivity, TOKEN)
        }



        Log.e("LINK -> ", mainLink)
        activityPaymentBinding.webPayment.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        activityPaymentBinding.webPayment.settings.javaScriptEnabled = true
        activityPaymentBinding.webPayment.loadUrl(mainLink)

        progressDialog.setOnCancelListener(DialogInterface.OnCancelListener {
            this@PaymentActivity.finish()
        })

        progressDialog.show()

        activityPaymentBinding.webPayment.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.e("PAGE", url)
                if (url == "https://www.digitalaviation.in/dashboard.php#") {
                    Toast.makeText(this@PaymentActivity, "Already subscribed", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.dismiss()
                    finish()
                } else if (url == "https://www.digitalaviation.in/course.php#") {
                    progressDialog.dismiss()
                    Toast.makeText(this@PaymentActivity, "Already subscribed", Toast.LENGTH_SHORT)
                        .show()
                } else if (url == "https://www.digitalaviation.in/status.php") {
                    progressDialog.dismiss()
                    finish()
                    Toast.makeText(this@PaymentActivity, "Payment Completed", Toast.LENGTH_SHORT)
                        .show()
                }
                return false
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressDialog.dismiss()
            }
        }

    }

}
