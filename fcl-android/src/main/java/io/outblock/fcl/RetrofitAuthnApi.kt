package io.outblock.fcl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.outblock.fcl.models.response.PollingResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

internal interface RetrofitAuthnApi {
    @GET
    suspend fun getAuthentication(@Url url: String): PollingResponse

    @POST("authn")
    suspend fun requestAuthentication(): PollingResponse
}

internal interface RetrofitAuthzApi {

    @POST
    suspend fun executePost(@Url url: String, @QueryMap params: Map<String, String>? = mapOf(), @Body data: String? = null): PollingResponse
}

internal fun retrofitAuthnApi(url: String): RetrofitAuthnApi {
    val client = okHttpClient()

    return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create())).baseUrl(url)
        .client(client).build().create(RetrofitAuthnApi::class.java)
}

internal fun retrofitAuthzApi(): RetrofitAuthzApi {
    val client = okHttpClient()

    return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create())).baseUrl("https://google.com")
        .client(client).build().create(RetrofitAuthzApi::class.java)
}

private fun okHttpClient(): OkHttpClient {
    val client = OkHttpClient.Builder().apply {
        addInterceptor(AuthzBodyInterceptor())
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

private class AuthzBodyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.method == "POST") {
            request.body.string()?.addAuthzBody()?.let { body ->
                request = request.newBuilder().post(body.toRequestBody()).build()
            }
        }

        return chain.proceed(request)
    }
}

private fun String.addAuthzBody(): String? {
    return try {
        val signable = Gson().fromJson<MutableMap<String, Any>>(this, object : TypeToken<MutableMap<String, Any>>() {}.type)
        signable["app"] = FCL.config.configLens("^app\\.detail\\.")
        signable["service"] = FCL.config.configLens("^service\\.")

        Gson().toJson(signable)
    } catch (e: Exception) {
        null
    }
}

private fun RequestBody?.string(): String? {
    this ?: return null
    val buffer = Buffer()
    writeTo(buffer)

    val charset: Charset = contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
    return buffer.readString(charset)
}
