package `in`.fusionbit.digitalaviation.dialogs

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import `in`.fusionbit.digitalaviation.model.RoatationModel
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.appbar.MaterialToolbar
import android.content.pm.ActivityInfo
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.switchmaterial.SwitchMaterial


class ContentDetailsDialog : DialogFragment() {

    private val TAG = ContentDetailsDialog::class.java.simpleName

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvContent: WebView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var btn: SwitchMaterial

    private lateinit var roatationModel: RoatationModel

    var mbOrientationLandscape = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_content_details, container, false)

        progressDialog = ProgressDialog(activity!!)

        toolbar = view.findViewById(R.id.dialog_toolbar)
        tvContent = view.findViewById(R.id.tv_content)
        btn = view.findViewById(R.id.btn_orientation)

        roatationModel = ViewModelProviders.of(this)[RoatationModel::class.java]

        btn.setOnCheckedChangeListener { compoundButton, b ->
            if (roatationModel.rotation) {
                roatationModel.rotation = false
                (activity as AppCompatActivity).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            } else {
                (activity as AppCompatActivity).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                roatationModel.rotation = true
            }
        }



        tvContent.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressDialog.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
//                tvContent.loadUrl(
//                    "javascript:(function() { " +
//                            "var head = document.getElementsByTagName('header')[0];"
//                            + "head.parentNode.removeChild(head);" +
//                            "})()"
//                )
//
//
//
//                tvContent.loadUrl(
//                    "javascript:(function() { " +
//                            "var head = document.getElementsByTagName('footer')[0];"
//                            + "head.parentNode.removeChild(head);" +
//                            "})()"
//                )
                progressDialog.cancel()
            }

        }

        tvContent.settings.javaScriptEnabled = true


        val DESKTOP_USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36"
        val MOBILE_USER_AGENT =
            "Mozilla/5.0 (Linux; U; Android 4.4; en-us; Nexus 4 Build/JOP24G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"

        //Choose Mobile/Desktop client.
        val settings = tvContent.getSettings()

        var urlData = "http://digitalaviation.in/master-mobile.php?cid=" + contentId
        Log.e("URL -> ", urlData)

        tvContent.loadUrl(urlData)

        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.title = lessonName
    }


    companion object {
        var lessonName = ""
        var url = ""
        var contentId = ""

        fun display(
            fragmentManager: FragmentManager,
            name: String,
            mUrl: String,
            cid: String
        ): ContentDetailsDialog {
            val dialog = ContentDetailsDialog()
            dialog.show(fragmentManager, "")
            lessonName = name
            url = mUrl
            contentId = cid
            return dialog
        }
    }
}