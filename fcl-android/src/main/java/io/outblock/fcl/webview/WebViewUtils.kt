package io.outblock.fcl.webview

import android.net.Uri
import io.outblock.fcl.Fcl
import io.outblock.fcl.config.Config
import io.outblock.fcl.lifecycle.LifecycleObserver
import io.outblock.fcl.models.response.Service
import io.outblock.fcl.utils.FCLException
import io.outblock.fcl.utils.FclError


fun Service.openAuthenticationWebView() {
    val url = endpoint ?: throw FCLException(FclError.invaildURL)
    val context = LifecycleObserver.context() ?: throw FCLException(FclError.invaildContext)

    val uri = Uri.parse(url).buildUpon().apply {
        params?.forEach { appendQueryParameter(it.key, it.value) }
        appendQueryParameter("l6n", Fcl.config.get(Config.KEY.Location))
    }.build()
    WebViewActivity.launchUrl(context, uri.toString())
}

fun Service.endpointUri(): Uri {
    val url = endpoint ?: throw FCLException(FclError.invaildURL)

    return Uri.parse(url).buildUpon().apply {
        params?.forEach { appendQueryParameter(it.key, it.value) }
        appendQueryParameter("l6n", Fcl.config.get(Config.KEY.Location))
    }.build()
}

fun Uri.openInWebView() {
    val context = LifecycleObserver.context() ?: throw FCLException(FclError.invaildContext)
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