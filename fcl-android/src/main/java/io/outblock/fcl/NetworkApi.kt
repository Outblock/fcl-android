package io.outblock.fcl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.outblock.fcl.config.Config
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.models.response.ResponseStatus
import io.outblock.fcl.models.response.Service
import io.outblock.fcl.utils.FCLError
import io.outblock.fcl.utils.FCLException
import io.outblock.fcl.utils.repeatWhen
import io.outblock.fcl.utils.runBlockDelay
import io.outblock.fcl.webview.FCLWebViewLifecycle
import io.outblock.fcl.webview.WebViewActivity
import io.outblock.fcl.webview.WebViewLifecycleObserver
import io.outblock.fcl.webview.openAuthenticationWebView
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
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

    @POST
    suspend fun executePost(@Url url: String, @QueryMap params: Map<String, String>? = mapOf(), @Body data: Any? = null): PollingResponse

    @GET
    suspend fun executeGet(@Url url: String, @QueryMap params: Map<String, String>? = mapOf()): PollingResponse
}

internal fun retrofitAuthnApi(url: String? = null): RetrofitAuthnApi {
    val client = okHttpClient()

    return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .baseUrl(url ?: "https://google.com")
        .client(client).build().create(RetrofitAuthnApi::class.java)
}


private object PollServiceState {
    private var canContinue = false

    init {
        FCLWebViewLifecycle.addWebViewLifecycleObserver(object : WebViewLifecycleObserver {
            override fun onWebViewClose(url: String?) {
                canContinue = false
            }

            override fun onWebViewOpen(url: String?) {
            }
        })
    }

    fun poll() {
        canContinue = true
    }

    fun stopPoll() {
        canContinue = false
    }

    fun isPollEnable() = canContinue
}


suspend fun execHttpPost(url: String, params: Map<String, String>? = mapOf(), data: Any? = null): PollingResponse {
    val response = retrofitAuthnApi().executePost(url, params, data)

    when (response.status) {
        ResponseStatus.APPROVED -> WebViewActivity.close()
        ResponseStatus.DECLINED -> {
            WebViewActivity.close()
            throw FCLException(FCLError.declined)
        }
        ResponseStatus.PENDING -> return tryPollService(response)
    }

    return response
}

private suspend fun tryPollService(
    response: PollingResponse,
): PollingResponse {
    PollServiceState.poll()
    val local = response.local() ?: throw FCLException(FCLError.generic)
    val updates = (response.updates ?: response.authorizationUpdates) ?: throw FCLException(FCLError.generic)

    try {
        local.openAuthenticationWebView()
    } catch (e: Exception) {
        throw FCLException(FCLError.generic, exception = e)
    }


    var pollResponse: PollingResponse? = null
    repeatWhen(predicate = { (pollResponse?.isPending() ?: true) }) {
        runBlockDelay(1000) {
            pollResponse = poll(updates)
        }
    }

    return pollResponse ?: response
}

private suspend fun poll(service: Service): PollingResponse {
    if (!PollServiceState.isPollEnable()) {
        throw FCLException(FCLError.declined)
    }

    val url = service.endpoint ?: throw FCLException(FCLError.invaildURL)

    val response = retrofitAuthnApi().executeGet(url, service.params)

    when (response.status) {
        ResponseStatus.APPROVED -> WebViewActivity.close()
        ResponseStatus.DECLINED -> {
            WebViewActivity.close()
            throw FCLException(FCLError.declined)
        }
        else -> return response
    }
    return response
}

private fun okHttpClient(): OkHttpClient {
    val client = OkHttpClient.Builder().apply {
        addInterceptor(AuthzBodyInterceptor())
        addInterceptor(HttpLoggingInterceptor())

        callTimeout(20, TimeUnit.SECONDS)
        connectTimeout(20, TimeUnit.SECONDS)
        readTimeout(20, TimeUnit.SECONDS)
        writeTimeout(20, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
    }.build()
    return client
}

private class AuthzBodyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        FCL.config.get("location")?.let {
            if (request.url.queryParameter("l6n").isNullOrBlank()) {
                val url = request.url.newBuilder().addQueryParameter("l6n", it).build()
                request = request.newBuilder().url(url).build()
            }
        }

        request = request.newBuilder().apply {
            FCL.config.get(Config.KEY.Location)?.let { addHeader("referer", it) }
            addHeader("application/json", "Content-Type")
            addHeader("application/json", "Accept")
        }.build()

//        if (request.method == "POST") {
//            request.body.string()?.addAuthzBody()?.let { body ->
//                request = request.newBuilder().post(body.toRequestBody()).build()
//            }
//        }

        return chain.proceed(request)
    }
}

private fun String.addAuthzBody(): String? {
    return try {
        val signable = Gson().fromJson<MutableMap<String, Any>>(this, object : TypeToken<MutableMap<String, Any>>() {}.type)
        signable["app"] = FCL.config.configLens("^app\\.detail\\.")
        signable["service"] = FCL.config.configLens("^service\\.")

        signable["client"] = mapOf(
            "fclVersion" to FCL.version,
            "fclLibrary" to "https://github.com/Outblock/fcl-android",
            "hostname" to null,
        )

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
