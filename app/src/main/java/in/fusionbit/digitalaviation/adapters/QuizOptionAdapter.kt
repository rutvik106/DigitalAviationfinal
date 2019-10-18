package `in`.fusionbit.digitalaviation.adapters

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.model.QuizOptions
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton


class QuizOptionAdapter(
    val context: Context,
    private val list: List<QuizOptions>
) :
    RecyclerView.Adapter<QuizOptionAdapter.VHOption>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHOption {
        return VHOption(
            LayoutInflater.from(parent.context).inflate(
                R.layout.single_option_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VHOption, position: Int) {
        holder.rbTitle.text = list[position].optionTitle

        holder.rbTitle.isChecked = selectedPosition == position

        holder.rbTitle.setOnClickListener {

            if (list[position].isOptionRight == "1") {
                holder.rbTitle.setTextColor(context.resources.getColor(R.color.colorGreen))
                if (list[position].reason == "" || list[position].reason == null) {
                    holder.tvReason.visibility = View.GONE
                } else {
                    holder.tvReason.visibility = View.VISIBLE
                    holder.tvReason.text = list[position].reason
                }
            } else {
                holder.rbTitle.setTextColor(context.resources.getColor(R.color.colorRed))
            }

            this.selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    class VHOption(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rbTitle = itemView.findViewById<MaterialRadioButton>(R.id.rb_title)
        val tvReason = itemView.findViewById<AppCompatTextView>(R.id.tv_reason)
    }

    interface QuizOptionCallback {
        fun optionData(isOptionRight: String, reason: String, position: Int)
    }
}