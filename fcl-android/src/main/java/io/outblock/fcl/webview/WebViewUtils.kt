package io.outblock.fcl.webview

import android.net.Uri
import io.outblock.fcl.Fcl
import io.outblock.fcl.config.Config
import io.outblock.fcl.lifecycle.LifecycleObserver
import io.outblock.fcl.models.response.Service
import io.outblock.fcl.utils.FclError
import io.outblock.fcl.utils.FclException


fun Service.openAuthenticationWebView() {
    val url = endpoint ?: throw FclException(FclError.invaildURL)
    val context = LifecycleObserver.context() ?: throw FclException(FclError.invaildContext)

    val uri = Uri.parse(url).buildUpon().apply {
        params?.forEach { appendQueryParameter(it.key, it.value) }

        val location = Fcl.config.get(Config.KEY.Location)
        if (!location.isNullOrEmpty() && Fcl.isMainnet()) {
            appendQueryParameter("l6n", location)
        }
    }.build()
    WebViewActivity.launchUrl(context, uri.toString())
}

fun Service.endpointUri(): Uri {
    val url = endpoint ?: throw FclException(FclError.invaildURL)

    return Uri.parse(url).buildUpon().apply {
        params?.forEach { appendQueryParameter(it.key, it.value) }
        appendQueryParameter("l6n", Fcl.config.get(Config.KEY.Location))
    }.build()
}

fun Uri.openInWebView() {
    val context = LifecycleObserver.context() ?: throw FclException(FclError.invaildContext)
    WebViewActivity.launchUrl(context, this.toString())
}

internal object FCLWebViewLifecycle {
    private val observers = mutableListOf<WebViewLifecycleObserver>()

    fun addWebViewLifecycleObserver(observer: WebViewLifecycleObserver) {
        observers.add(observer)
    }

    fun removeWebViewLifecycleObserver(observer: WebViewLifecycleObserver) {
        observers.remove(observer)
    }

    fun onWebViewOpen(url: String?) {
        observers.forEach { it.onWebViewOpen(url) }
    }

    fun onWebViewClose(url: String?) {
        observers.forEach { it.onWebViewClose(url) }
    }
}

internal interface WebViewLifecycleObserver {
    fun onWebViewClose(url: String?)

    fun onWebViewOpen(url: String?)
}