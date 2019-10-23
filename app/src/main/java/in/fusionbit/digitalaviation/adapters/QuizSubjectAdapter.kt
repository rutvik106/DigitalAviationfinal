package `in`.fusionbit.digitalaviation.adapters

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.model.SubjectModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class QuizSubjectAdapter(
    context: Context,
    private val subjectList: ArrayList<SubjectModel>,
    private val subjectClick: SubjectClick
) :
    RecyclerView.Adapter<QuizSubjectAdapter.VHSubject>() {

    private val context: Context = context
    private val onClick: SubjectClick = subjectClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHSubject {
        return VHSubject(
            LayoutInflater.from(parent.context).inflate(
                R.layout.quiz_single_subject_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return subjectList.size
    }

    override fun onBindViewHolder(holder: VHSubject, position: Int) {
        holder.tvCourseName.text = subjectList[position].name
        holder.btnReadMore.setOnClickListener {
            onClick.onSubjectClick(
                subjectList[position].courseId,
                subjectList[position].id,
                subjectList[position].name
            )
        }

        Glide
            .with(context!!)
            .load(subjectList[position].image)
            .placeholder(R.drawable.logo)
            .apply(RequestOptions().override(120, 130))
            .into(holder.ivImage)
    }

    class VHSubject(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourseName = itemView.findViewById<AppCompatTextView>(R.id.tv_course_name)
        val ivImage = itemView.findViewById<AppCompatImageView>(R.id.iv_image)
        val btnReadMore = itemView.findViewById<MaterialButton>(R.id.btn_read_more)
    }

    interface SubjectClick {
        fun onSubjectClick(courseId: String, subjectId: String, subjectName: String)
        fun onSingleItemData(
            name: String,
            description: String,
            image: String,
            duration: String,
            createdDate: String
        )
    }
}