package io.outblock.fcl.webview

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

internal class WebViewActivity : AppCompatActivity() {

    private val url by lazy { intent.getStringExtra(EXTRA_URL).orEmpty() }

    private val webView by lazy { FCLWebView(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContentView(webView)
        webView.loadUrl(url)
        FCLWebViewLifecycle.onWebViewOpen(url)

        actionBar?.hide()
        supportActionBar?.hide()
    }

    override fun onDestroy() {
        instance = null
        FCLWebViewLifecycle.onWebViewClose(webView.url)
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_URL = "extra_url"

        private var instance: WebViewActivity? = null

        fun close() {
            instance?.finish()
            instance = null
        }

        fun isOpening() = instance != null

        fun launchUrl(context: Context, url: String) {
            context.startActivity(Intent(context, WebViewActivity::class.java).apply {
                if (context is Application) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                putExtra(EXTRA_URL, url)
            })
        }
    }
}