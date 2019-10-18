package `in`.fusionbit.digitalaviation.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

open class ApiCallback<T> : Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        if (!call.isCanceled) {
            if (!call.isCanceled) {
            }
        }
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            if (response.code() != HttpURLConnection.HTTP_OK) {
            } else if (response.code() == HttpURLConnection.HTTP_OK) {
            }
        }
    }
}