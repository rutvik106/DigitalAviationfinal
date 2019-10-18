package `in`.fusionbit.digitalaviation.model

data class LessonModel(
    val id: String,
    val name: String,
    val description: String,
    val courseId: String,
    val subjectId: String,
    val shortDescription: String,
    val link: String,
    val image: String,
    val duration: String,
    val ext1: String,
    val ext2: String,
    val createdDate: String,
    val updatedDate: String
)