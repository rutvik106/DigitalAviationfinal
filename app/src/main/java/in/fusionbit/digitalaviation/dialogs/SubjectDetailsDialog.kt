package `in`.fusionbit.digitalaviation.dialogs

import `in`.fusionbit.digitalaviation.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar

class SubjectDetailsDialog : DialogFragment() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvName: AppCompatTextView
    private lateinit var tvDescription: AppCompatTextView
    private lateinit var tvDuration: AppCompatTextView
    private lateinit var tvPrice: AppCompatTextView
    private lateinit var tvCreatedDate: AppCompatTextView
    private lateinit var ivImage: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_subject_details, container, false)

        toolbar = view.findViewById(R.id.dialog_toolbar)
        tvName = view.findViewById(R.id.tv_name)
        tvDescription = view.findViewById(R.id.tv_description)
        tvDuration = view.findViewById(R.id.tv_duration)
        tvCreatedDate = view.findViewById(R.id.tv_date)
        ivImage = view.findViewById(R.id.iv_image)

        tvName.text = subjectName
        tvDescription.text = subjectDescription
        tvDuration.text = "Duration : $subjectDuration"
        tvCreatedDate.text = subjectCreatedDate

        Glide
            .with(activity!!)
            .load(subjectImage)
            .centerCrop()
            .placeholder(R.drawable.logo)
            .into(ivImage)

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
    }


    companion object {
        var subjectName = ""
        var subjectDescription = ""
        var subjectImage = ""
        var subjectDuration = ""
        var subjectCreatedDate = ""

        fun display(
            fragmentManager: FragmentManager,
            name: String,
            description: String,
            image: String,
            duration: String,
            createdDate: String
        ): SubjectDetailsDialog {
            val dialog = SubjectDetailsDialog()
            dialog.show(fragmentManager, "")
            subjectName = name
            subjectDescription = description
            subjectImage = image
            subjectDuration = duration
            subjectCreatedDate = createdDate
            return dialog
        }
    }
}