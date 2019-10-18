package `in`.fusionbit.digitalaviation.extras

import `in`.fusionbit.digitalaviation.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout

class ProgressDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress_dialogue_layout)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val linearLayout = findViewById<LinearLayout>(R.id.root)
        linearLayout.setOnClickListener {

        }
        setCancelable(false)
    }
}