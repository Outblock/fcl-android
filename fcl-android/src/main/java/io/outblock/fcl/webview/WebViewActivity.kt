package io.outblock.fcl.webview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

    private val url by lazy { intent.getStringExtra(EXTRA_URL).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val webView = FCLWebView(this)
        setContentView(webView)
        webView.loadUrl(url)

        actionBar?.hide()
        supportActionBar?.hide()
    }

    companion object {
        private const val EXTRA_URL = "extra_url"

        fun launchUrl(context: Context, url: String) {
            context.startActivity(Intent(context, WebViewActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
            })
        }
    }
}