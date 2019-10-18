package `in`.fusionbit.digitalaviation.model

data class SubscriptionsModel(
    val id: String,
    val orderNo: String,
    val date: String,
    val expiredDate: String,
    val courseName: String,
    val coursePrice: String,
    val expired: String
)