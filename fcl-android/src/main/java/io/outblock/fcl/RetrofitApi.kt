package io.outblock.fcl

import com.google.gson.GsonBuilder
import io.outblock.fcl.response.PollingResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

internal interface RetrofitApi {
    @GET
    suspend fun getAuthentication(@Url url: String): PollingResponse

    @POST("authn")
    suspend fun requestAuthentication(): PollingResponse
}

internal fun retrofitApi(url: String): RetrofitApi {
    val client = OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor())

        callTimeout(10, TimeUnit.SECONDS)
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
    }.build()

    return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create())).baseUrl(url)
        .client(client).build().create(RetrofitApi::class.java)
}
