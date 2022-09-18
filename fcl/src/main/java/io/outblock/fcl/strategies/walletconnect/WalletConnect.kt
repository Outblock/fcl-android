package io.outblock.fcl.strategies.walletconnect

import android.app.Application
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import io.outblock.fcl.lifecycle.LifecycleObserver
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.utils.ioScope
import io.outblock.fcl.utils.logd
import io.outblock.fcl.utils.loge
import io.outblock.fcl.utils.logw

private const val TAG = "WalletConnect"

internal class WalletConnect {
    private var meta: WalletConnectMeta? = null

    suspend fun pair(uri: String): PollingResponse? {
        return wcPairInternal(uri)
    }

    suspend fun pairUri(): String? {
        return wcFetchPairUriInternal()
    }

    fun sessionCount(): Int = sessions().size

    fun sessions() = SignClient.getListOfSettledSessions().filter { it.metaData != null }

    fun disconnect(topic: String) {
        SignClient.disconnect(Sign.Params.Disconnect(sessionTopic = topic)) { error -> loge(error.throwable) }
    }

    fun transaction() {

    }

    fun meta() = meta

    companion object {
        private lateinit var instance: WalletConnect

        fun init(meta: WalletConnectMeta) {
            ioScope {
                val application = LifecycleObserver.context()?.applicationContext as Application
                setup(application, meta)
                instance = WalletConnect().apply { this.meta = meta }
            }
        }

        fun get() = instance
    }
}

private fun setup(application: Application, meta: WalletConnectMeta) {
    logd(TAG, "setup meta:$meta")
    val initString = Sign.Params.Init(
        application = application,
        relayServerUrl = "wss://relay.walletconnect.com?projectId=${meta.projectId}".trim(),
        connectionType = Sign.ConnectionType.MANUAL,
        metadata = Sign.Model.AppMetaData(
            name = meta.name,
            description = meta.description,
            url = meta.url,
            icons = listOf(meta.icon),
            redirect = "${application.packageName}\$fromSdk",
        )
    )

    val result = runCatching {
        SignClient.initialize(initString) { error -> loge(error.throwable) }

        SignClient.setDappDelegate(WalletConnectDappDelegate())
        SignClient.WebSocket.open { error -> logw(TAG, "open error:$error") }
    }
    logd(TAG, "setup result:$result")
}

data class WalletConnectMeta(
    // register at https://walletconnect.com/register to get a project ID
    val projectId: String,
    val name: String,
    val description: String,
    val url: String,
    val icon: String,
)