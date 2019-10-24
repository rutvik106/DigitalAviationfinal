package `in`.fusionbit.digitalaviation.extras

import `in`.fusionbit.digitalaviation.model.SubscriptionsModel
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


const val BASE_URL = "http://api.digitalaviation.in/digital-aviation/index.php/"
const val PAYMENT_LINK = "https://www.digitalaviation.in/app_subscriptions.php?"

const val COURSE_ID = "course_id"
const val SUBJECT_ID = "subject_id"
const val QUIZ_ID = "quiz_id"
const val MOBILE_NO = "mobile_no"
const val PREF = "digital_avi"
const val TOKEN = "token"
const val USER_ID = "user_id"
const val USER_EMAIL = "user_email"
const val USER_NAME = "user_name"
const val USER_MOBILE = "mobile"
const val SUBSCRIPTION = "subscription_date"
const val IS_SUBSCRIBED = "is_subscribed"

fun savePref(context: Context, key: String, value: String) {
    val sharedPref = context.getSharedPreferences(
        PREF, Context.MODE_PRIVATE
    )
    with(sharedPref.edit()) {
        putString(key, value)
        commit()
    }
}

fun getPref(context: Context, key: String): String {
    val sharedPref = context.getSharedPreferences(
        PREF, Context.MODE_PRIVATE
    )
    return sharedPref.getString(key, "")
}

fun getArrayListPref(context: Context, key: String): ArrayList<SubscriptionsModel> {
    val sharedPref = context.getSharedPreferences(
        PREF, Context.MODE_PRIVATE
    )
    val gson = Gson()
    val json = sharedPref.getString(key, "")
    val type = object : TypeToken<ArrayList<SubscriptionsModel>>() {

    }.type
    return gson.fromJson(json, type)
}
