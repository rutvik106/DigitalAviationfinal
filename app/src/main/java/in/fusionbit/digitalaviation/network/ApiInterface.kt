package `in`.fusionbit.digitalaviation.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("course/get")
    fun getCourseList(): Call<ResponseBody>

    @GET("subject/get/{id}")
    fun getSubject(@Path("id") id: String): Call<ResponseBody>

    @GET("lesson/get/{courseId}/{subjectId}")
    fun getLesson(
        @Path("courseId") courseId: String,
        @Path("subjectId") subjectIdentityHashMap: String
    ): Call<ResponseBody>

    @GET("content/get/{courseId}/{subjectId}/{lessonId}")
    fun getContent(
        @Path("courseId") courseId: String,
        @Path("subjectId") subjectId: String,
        @Path("lessonId") lessonId: String
    ): Call<ResponseBody>


    @GET("quiz/get/{subjectId}")
    fun getAllQuiz(@Path("subjectId") subjectId: String): Call<ResponseBody>

    @GET("question/get/{quizId}")
    fun getQuizData(@Path("quizId") quizId: String): Call<ResponseBody>

    @GET("app/requestOtp/{mobileNo}")
    fun callOtp(@Path("mobileNo") mobileNo: String): Call<ResponseBody>

    @GET("App/verifyOtp/{mobile}/{otp}")
    fun verifyOtp(@Path("mobile") mobileNo: String, @Path("otp") otp: String): Call<ResponseBody>

    @GET("App/login/{number}/{token}")
    fun login(@Path("number") number: String, @Path("token") token: String): Call<ResponseBody>

    @GET("course/get/{contactNumber}")
    fun getCourseListAfterLogin(@Path("contactNumber") number: String): Call<ResponseBody>
}