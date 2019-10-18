package `in`.fusionbit.digitalaviation.model

data class QuizQuestion(
    val id: String,
    val questionTitle: String,
    val optionList: ArrayList<QuizOptions>
)

data class QuizOptions(
    val questionId: String,
    val optionTitle: String,
    val isOptionRight: String,
    val reason: String
)