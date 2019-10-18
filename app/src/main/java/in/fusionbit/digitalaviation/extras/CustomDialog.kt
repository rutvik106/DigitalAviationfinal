package `in`.fusionbit.digitalaviation.extras

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View

class CustomDialog(context: Context) : Dialog(context), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onClick(v: View?) {
        dismiss()
    }
}