package io.outblock.fcl.webview

import io.outblock.fcl.lifecycle.LifecycleObserver
import io.outblock.fcl.models.response.Service
import io.outblock.fcl.utils.FCLError
import io.outblock.fcl.utils.FCLException


fun Service.openAuthenticationWebView() {
    val url = endpoint ?: throw FCLException(FCLError.invaildURL)

    val context = LifecycleObserver.context() ?: throw FCLException(FCLError.invaildContext)
    WebViewActivity.launchUrl(context, url)
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