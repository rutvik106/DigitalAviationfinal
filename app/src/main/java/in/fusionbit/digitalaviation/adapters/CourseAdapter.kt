package `in`.fusionbit.digitalaviation.adapters

import `in`.fusionbit.digitalaviation.PaymentActivity
import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.extras.COURSE_ID
import `in`.fusionbit.digitalaviation.model.CourseModel
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class CourseAdapter(
    context: Context,
    private val courseList: ArrayList<CourseModel>,
    private val courseClick: CourseClick
) :
    RecyclerView.Adapter<CourseAdapter.VHCourse>() {

    private val onClick: CourseClick = courseClick
    private val context: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHCourse {
        return VHCourse(
            LayoutInflater.from(parent.context).inflate(
                R.layout.course_single_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    override fun onBindViewHolder(holder: VHCourse, position: Int) {
        holder.tvCourseName.text = courseList[position].name
        holder.tvDescription.text = courseList[position].shortDescription
        holder.tvDuration.text = "Duration : " + courseList[position].duration
//        holder.tvAmount.text = "Price : " + courseList[position].price

        holder.card.setOnClickListener {
            onClick.onCourseClick(courseList[position].id)
        }

        holder.ivInfo.setOnClickListener {
            onClick.onSingleItemData(
                courseList[position].name,
                courseList[position].description,
                courseList[position].image,
                courseList[position].price,
                courseList[position].duration,
                courseList[position].createdDate
            )
        }

        holder.btnSubscribe.setOnClickListener {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(COURSE_ID, courseList[position].id)
            context.startActivity(intent)
        }

    }

    class VHCourse(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card = itemView.findViewById<MaterialCardView>(R.id.card)
        val tvCourseName = itemView.findViewById<AppCompatTextView>(R.id.tv_course_name)
        val tvDescription = itemView.findViewById<AppCompatTextView>(R.id.tv_description)
        val ivInfo = itemView.findViewById<AppCompatImageView>(R.id.iv_info)
        val tvDuration = itemView.findViewById<AppCompatTextView>(R.id.tv_duration)
        val tvAmount = itemView.findViewById<AppCompatTextView>(R.id.tv_amount)
        val btnSubscribe = itemView.findViewById<MaterialButton>(R.id.btn_subscribe)
    }

    interface CourseClick {
        fun onCourseClick(id: String)
        fun onSingleItemData(
            name: String,
            description: String,
            image: String,
            price: String,
            duration: String,
            createdDate: String
        )
    }
}