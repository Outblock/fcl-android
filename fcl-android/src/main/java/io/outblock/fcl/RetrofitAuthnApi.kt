package io.outblock.fcl

import com.google.gson.GsonBuilder
import io.outblock.fcl.models.response.PollingResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

internal interface RetrofitAuthnApi {
    @GET
    suspend fun getAuthentication(@Url url: String): PollingResponse

    @POST("authn")
    suspend fun requestAuthentication(): PollingResponse
}

internal interface RetrofitAuthzApi {

    @POST
    suspend fun executePost(@QueryMap params: Map<String, String>? = mapOf(), @Body data: String? = null): PollingResponse
}

internal fun retrofitAuthnApi(url: String): RetrofitAuthnApi {
    val client = okHttpClient()

    return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create())).baseUrl(url)
        .client(client).build().create(RetrofitAuthnApi::class.java)
}

internal fun retrofitAuthzApi(url: String): RetrofitAuthzApi {
    val client = okHttpClient()

    return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create())).baseUrl(url)
        .client(client).build().create(RetrofitAuthzApi::class.java)
}

private fun okHttpClient(): OkHttpClient {
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
    return client
}
