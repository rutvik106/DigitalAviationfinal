package `in`.fusionbit.digitalaviation.model


data class CourseModel(
    val id: String,
    val name: String,
    val description: String,
    val shortDescription: String,
    val image: String,
    val price: String,
    val duration: String,
    val createdDate: String,
    val isSubscribed: String = "0",
    val expired: String = "0"
)