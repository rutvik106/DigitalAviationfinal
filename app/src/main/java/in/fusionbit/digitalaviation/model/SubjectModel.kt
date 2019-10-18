package `in`.fusionbit.digitalaviation.model

data class SubjectModel(
    val id: String, val name: String, val courseId: String, val description: String,
    val shortDescription: String, val link: String, val image: String, val duration: String,
    val ext1: String, val ext2: String, val createdDate: String, val updatedDate: String
)