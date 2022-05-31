package io.outblock.fcl.authn

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import io.outblock.fcl.FCL
import io.outblock.fcl.RetrofitApi
import io.outblock.fcl.config.Config
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.provider.Provider
import io.outblock.fcl.retrofitApi
import io.outblock.fcl.utils.*
import kotlinx.coroutines.withTimeout

internal class FCLAuthn {
    fun authenticate(
        context: Context,
        provider: Provider,
        onComplete: (PollingResponse) -> Unit,
    ) {
        ioScope {
            kotlin.runCatching {
                val client = retrofitApi(FCL.providers.get(provider).endpoint.toString())

                val auth = client.requestAuthentication()
                val service = auth.local ?: throw Exception("not provided login iframe")

                this.openLoginTab(context, service.endpoint.orEmpty(), service.params.orEmpty())

                client.getAuthenticationResult(auth) { onComplete(it) }
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
                authentication.updates.endpoint.orEmpty(),
                authentication.updates.params.orEmpty(),
                FCL.config.get(Config.KEY.Location).orEmpty(),
            )

            var response: PollingResponse? = null
            withTimeout(secondsTimeout * 1000) {
                repeatWhen(predicate = { (response?.isPending() ?: true) }) {
                    runBlockDelay(1000) {
                        response = getAuthentication(uri.toString())
                        logd(TAG, "getAuthenticationResult: $response")
                    }
                }
            }
            uiScope { response?.let { onComplete.invoke(it) } }
        }
    }

    private fun openLoginTab(
        context: Context,
        url: String,
        params: Map<String, String>,
    ) {
        val uri = makeServiceUrl(url, params, FCL.config.get(Config.KEY.Location).orEmpty())

        val tabIntent = CustomTabsIntent.Builder().build()
        tabIntent.launchUrl(context, uri)
    }

    companion object {
        private const val TAG = "FCLAuthn"
    }
}