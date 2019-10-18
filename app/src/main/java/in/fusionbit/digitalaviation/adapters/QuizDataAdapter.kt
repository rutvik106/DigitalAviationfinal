package `in`.fusionbit.digitalaviation.adapters

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.model.QuizDataModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class QuizDataAdapter internal constructor(
    private val list: ArrayList<QuizDataModel>,
    private val callback: QuizDataCallback
) :
    RecyclerView.Adapter<QuizDataAdapter.VHQuizData>() {

    private val onClick: QuizDataCallback = callback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHQuizData {
        return VHQuizData(
            LayoutInflater.from(parent.context).inflate(
                R.layout.single_quiz_data,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VHQuizData, position: Int) {
        holder.tvTitle.text = list[position].title
        holder.card.setOnClickListener {
            onClick.quizData(
                list[position].id
            )
        }
    }

    class VHQuizData(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card = itemView.findViewById<MaterialCardView>(R.id.card)
        val tvTitle = itemView.findViewById<AppCompatTextView>(R.id.tv_title)
    }

    interface QuizDataCallback {
        fun quizData(id: String)
    }
}