package `in`.fusionbit.digitalaviation.extras

import `in`.fusionbit.digitalaviation.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView

class ProgressDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress_dialogue_layout)
        val msg = findViewById<AppCompatTextView>(R.id.actv_progressMsg);
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val linearLayout = findViewById<LinearLayout>(R.id.root)
        linearLayout.setOnClickListener {

        }
        //setCancelable(false)

        setCanceledOnTouchOutside(false)

        Handler().postDelayed(Runnable {
            msg.setText("LOADING");
        }, 1000)

       Handler().postDelayed(Runnable {
            msg.setText("FETCHING DATA");
        }, 2000)

        Handler().postDelayed(Runnable {
            msg.setText("JUST A MOMENT");
        }, 3000)

        Handler().postDelayed(Runnable {
            msg.setText("ALMOST THERE");
        }, 4000)

    }

}