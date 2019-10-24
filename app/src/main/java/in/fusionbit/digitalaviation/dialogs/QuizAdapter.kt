package `in`.fusionbit.digitalaviation.dialogs

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.adapters.QuizOptionAdapter
import `in`.fusionbit.digitalaviation.model.QuizOptions
import `in`.fusionbit.digitalaviation.model.QuizQuestion
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuizAdapter internal constructor(
    context: Context,
    val quizList: ArrayList<QuizQuestion>
) : RecyclerView.Adapter<QuizAdapter.VHQuiz>() {

    private var context: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHQuiz {
        return VHQuiz(
            LayoutInflater.from(context).inflate(
                R.layout.single_quiz_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return quizList.size
    }

    override fun onBindViewHolder(holder: VHQuiz, position: Int) {
        holder.tvQuestion.text = "Question. ${position + 1}  " + quizList[position].questionTitle

        val list: List<QuizOptions> =
            quizList[position].optionList.filter { quizOptions -> quizOptions.questionId == quizList[position].id }

        holder.rvOption.hasFixedSize()
        holder.rvOption.layoutManager = LinearLayoutManager(context)
        holder.rvOption.adapter = QuizOptionAdapter(context, list)
    }

    class VHQuiz(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestion = itemView.findViewById<AppCompatTextView>(R.id.tv_question)
        val rvOption = itemView.findViewById<RecyclerView>(R.id.rv_option)
    }
}