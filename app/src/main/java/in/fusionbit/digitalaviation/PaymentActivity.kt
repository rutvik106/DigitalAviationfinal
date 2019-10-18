package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.databinding.ActivityPaymentBinding
import `in`.fusionbit.digitalaviation.extras.COURSE_ID
import `in`.fusionbit.digitalaviation.extras.PAYMENT_LINK
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        val mainLink = PAYMENT_LINK + courseId

        activityPaymentBinding.webPayment.settings.javaScriptEnabled = true
        activityPaymentBinding.webPayment.loadUrl(mainLink)

        activityPaymentBinding.webPayment.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressDialog.show()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.e("PAGE", url)
                if (url == "http://digitalaviation.in/dashboard.php#") {
                    Toast.makeText(this@PaymentActivity, "Already subscribed", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.cancel()
                    finish()
                } else if (url == "http://digitalaviation.in/course.php#") {
                    progressDialog.cancel()
                    Toast.makeText(this@PaymentActivity, "Already subscribed", Toast.LENGTH_SHORT)
                        .show()
                } else if (url == "http://digitalaviation.in/status.php") {
                    progressDialog.cancel()
                    finish()
                    Toast.makeText(this@PaymentActivity, "Payment Completed", Toast.LENGTH_SHORT)
                        .show()
                }
                return false
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressDialog.cancel()
            }
        }

    }
}
