package `in`.fusionbit.digitalaviation.network

import okhttp3.ResponseBody

class API {
    fun getCourse(apiCallback: ApiCallback<ResponseBody>) {
        val courseCallback = ApiClient().requestService().getCourseList()
        courseCallback.enqueue(apiCallback)
    }

    fun getSubject(id: String, apiCallback: ApiCallback<ResponseBody>) {
        val subjectCallback = ApiClient().requestService().getSubject(id)
        subjectCallback.enqueue(apiCallback)
    }

    fun getLesson(courseId: String, subjectId: String, apiCallback: ApiCallback<ResponseBody>) {
        val lessonCallback = ApiClient().requestService().getLesson(courseId, subjectId)
        lessonCallback.enqueue(apiCallback)
    }

    fun getContent(
        courseId: String,
        subjectId: String,
        lessonId: String,
        apiCallback: ApiCallback<ResponseBody>
    ) {
        val contentCallback = ApiClient().requestService().getContent(courseId, subjectId, lessonId)
        contentCallback.enqueue(apiCallback)
    }

    fun getQuizData(subjectId: String, callback: ApiCallback<ResponseBody>) {
        val quizCallback = ApiClient().requestService().getAllQuiz(subjectId)
        quizCallback.enqueue(callback)
    }

    fun getQuiz(quizId: String, callback: ApiCallback<ResponseBody>) {
        val quizCallback = ApiClient().requestService().getQuizData(quizId)
        quizCallback.enqueue(callback)
    }

    fun register(mobileNo: String, callback: ApiCallback<ResponseBody>) {
        val otpCallback = ApiClient().requestService().callOtp(mobileNo)
        otpCallback.enqueue(callback)
    }

    fun verify(mobile: String, otp: String, callback: ApiCallback<ResponseBody>) {
        val verifyCallback = ApiClient().requestService().verifyOtp(mobile, otp)
        verifyCallback.enqueue(callback)
    }

    fun login(number: String, token: String, callback: ApiCallback<ResponseBody>) {
        val dataCallback = ApiClient().requestService().login(number, token)
        dataCallback.enqueue(callback)
    }
}