package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.databinding.ActivityContentBinding
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import im.delight.android.webview.AdvancedWebView
import android.app.Activity
import android.content.DialogInterface


class ContentActivity : AppCompatActivity() {

    private lateinit var activityContentBinding: ActivityContentBinding

    private var name = ""
    private var url = ""
    private var id = ""

    private lateinit var progressDialog: ProgressDialog


    var mbOrientationLandscape = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityContentBinding =
            DataBindingUtil.setContentView(this@ContentActivity, R.layout.activity_content)

        name = intent.getStringExtra("name")
        url = intent.getStringExtra("url")
        id = intent.getStringExtra("id")


        setSupportActionBar(activityContentBinding.toolbar)
        supportActionBar?.title = name

        activityContentBinding.toolbar.setNavigationOnClickListener {
            finish()
        }
        progressDialog = ProgressDialog(this@ContentActivity)

        progressDialog.setOnCancelListener(DialogInterface.OnCancelListener {
            this@ContentActivity.finish()
        })

        progressDialog.show()

        val handler = Handler().postDelayed(Runnable {
            progressDialog.dismiss()
        }, 5000)




        activityContentBinding.btnOrientation.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                activityContentBinding.web.setDesktopMode(true)
                activityContentBinding.web.getSettings()
                    .setUserAgentString("Mozilla/5.0 (Linux; diordnA 7.1.1; suxeN 6 Build/N6F26U; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 eliboM Safari/537.36")
//                mbOrientationLandscape = false
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            } else {
                activityContentBinding.web.setDesktopMode(false)
                activityContentBinding.web.getSettings()
                    .setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.132 Safari/537.36")
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                mbOrientationLandscape = true
            }
        }

//        activityContentBinding.tvContent.webViewClient = object : WebViewClient() {
////
////            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
////                super.onPageStarted(view, url, favicon)
////            }
////
////            override fun onPageFinished(view: WebView?, url: String?) {
////                super.onPageFinished(view, url)
////            }
////
////        }

//        activityContentBinding.tvContent.webChromeClient = object : WebChromeClient() {
//
//        }
//
//        activityContentBinding.tvContent.settings.javaScriptEnabled = true
//
////        activityContentBinding.tvContent.getSettings()
////            .setRenderPriority(WebSettings.RenderPriority.HIGH)
////        activityContentBinding.tvContent.getSettings()
////            .setCacheMode(WebSettings.LOAD_NO_CACHE)
//
////        var urlData = "http://digitalaviation.in/master-mobile.php?cid=" + id
//        var urlData = "https://digitalaviation.in/master-mobile.php?cid=99"
////        var urlData = "http://admin.digitalaviation.in/uploads/images/1080723776.gif"
//
//        activityContentBinding.tvContent.loadUrl(urlData)
        activityContentBinding.web.loadUrl("http://digitalaviation.in/master-mobile.php?cid=" + id)
//        activityContentBinding.web.loadUrl("https://www.facebook.com/")

    }

}
