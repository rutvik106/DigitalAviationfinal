package `in`.fusionbit.digitalaviation.network

import `in`.fusionbit.digitalaviation.BuildConfig
import `in`.fusionbit.digitalaviation.extras.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.google.gson.GsonBuilder


class ApiClient {
    val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun requestService(): ApiInterface = retrofit.create(ApiInterface::class.java)

    private fun okHttpClient(): OkHttpClient {

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        lateinit var okHttpClient: OkHttpClient

        okHttpClient = if (BuildConfig.DEBUG) {
            OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .readTimeout(10, TimeUnit.MINUTES)
                .connectTimeout(10, TimeUnit.MINUTES)
                .build()
        } else {
            OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.MINUTES)
                .connectTimeout(10, TimeUnit.MINUTES)
                .build()
        }
        return okHttpClient
    }

}