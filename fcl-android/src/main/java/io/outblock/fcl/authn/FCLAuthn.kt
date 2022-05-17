package io.outblock.fcl.authn

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import io.outblock.fcl.FCL
import io.outblock.fcl.RetrofitApi
import io.outblock.fcl.provider.Provider
import io.outblock.fcl.response.AuthnResponse
import io.outblock.fcl.response.PollingResponse
import io.outblock.fcl.retrofitApi
import io.outblock.fcl.utils.ioScope
import io.outblock.fcl.utils.makeServiceUrl
import io.outblock.fcl.utils.repeatWhen
import io.outblock.fcl.utils.runBlockDelay
import kotlinx.coroutines.withTimeout

class FCLAuthn {
    fun authenticate(
        context: Context,
        provider: Provider,
        onComplete: (AuthnResponse) -> Unit,
    ) {
        ioScope {
            val exception = kotlin.runCatching {
                val client = retrofitApi(FCL.providers.get(provider).endpoint.toString())

                val auth = client.requestAuthentication()
                val service = auth.local ?: throw Exception("not provided login iframe")

                this.openLoginTab(context, service.endpoint, service.params)

                client.getAuthenticationResult(auth) {
                    onComplete(AuthnResponse(it.data?.addr, it.status, it.reason))
                }
            }
        }
    }

    private fun RetrofitApi.getAuthenticationResult(
        authentication: PollingResponse,
        secondsTimeout: Long = 300,
        onComplete: (PollingResponse) -> Unit,
    ) {
        ioScope {

            if (authentication.updates == null) {
                throw Throwable("authentication response must include updates")
            }

            val uri = makeServiceUrl(
                authentication.updates.endpoint,
                authentication.updates.params,
                "https://foo.com",
            )

            var response: PollingResponse? = null
            withTimeout(secondsTimeout * 1000) {
                repeatWhen(predicate = { !(response?.isPending() ?: true) }) {
                    runBlockDelay(1000) {
                        response = getAuthentication(uri.toString())
                    }
                }
            }
            response?.let { onComplete.invoke(it) }
        }
    }

    private fun openLoginTab(
        context: Context,
        url: String,
        params: Map<String, String>,
    ) {
        val uri = makeServiceUrl(url, params, "http://foo.com")

        val tabIntent = CustomTabsIntent.Builder().build()
        tabIntent.launchUrl(context, uri)
    }
}