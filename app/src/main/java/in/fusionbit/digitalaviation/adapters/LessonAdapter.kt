package `in`.fusionbit.digitalaviation.adapters

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.model.LessonModel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class LessonAdapter(
    private val lessonList: ArrayList<LessonModel>,
    private val lessonClick: LessonClick
) :
    RecyclerView.Adapter<LessonAdapter.VHLesson>() {

    private val onClick: LessonClick = lessonClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHLesson {
        return VHLesson(
            LayoutInflater.from(parent.context).inflate(
                R.layout.single_course_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return lessonList.size
    }

    override fun onBindViewHolder(holder: VHLesson, position: Int) {
        holder.tvCourseName.text = lessonList[position].name
        holder.tvDescription.text = lessonList[position].shortDescription
        holder.tvDuration.text = "Duration : " + lessonList[position].duration

        Log.e("ID -> ", lessonList[position].id)

        holder.card.setOnClickListener {
            onClick.onLessonClick(
                lessonList[position].name,
                lessonList[position].ext1,
                lessonList[position].id
            )
        }

        holder.ivInfo.setOnClickListener {
            onClick.onSingleItemData(
                lessonList[position].name,
                lessonList[position].description,
                lessonList[position].image,
                lessonList[position].duration,
                lessonList[position].createdDate
            )
        }

    }

    class VHLesson(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card = itemView.findViewById<MaterialCardView>(R.id.card)
        val tvCourseName = itemView.findViewById<AppCompatTextView>(R.id.tv_course_name)
        val tvDescription = itemView.findViewById<AppCompatTextView>(R.id.tv_description)
        val ivInfo = itemView.findViewById<AppCompatImageView>(R.id.iv_info)
        val tvDuration = itemView.findViewById<AppCompatTextView>(R.id.tv_duration)
        val tvAmount = itemView.findViewById<AppCompatTextView>(R.id.tv_amount)
    }

    interface LessonClick {
        fun onLessonClick(name: String, url: String, id: String)
        fun onSingleItemData(
            name: String,
            description: String,
            image: String,
            duration: String,
            createdDate: String
        )
    }
}