package io.outblock.fcl.strategies.walletconnect

import android.app.Application
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import io.outblock.fcl.Fcl
import io.outblock.fcl.lifecycle.LifecycleObserver
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.utils.ioScope
import io.outblock.fcl.utils.logd
import io.outblock.fcl.utils.loge
import io.outblock.fcl.utils.logw
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "WalletConnect"

internal class WalletConnect {
    private var meta: WalletConnectMeta? = null

    suspend fun pair(uri: String): PollingResponse? {
        return pairInternal(uri)
    }

    suspend fun pairUri(): String? {
        return pairUriInternal()
    }

    fun sessionCount(): Int = sessions().size

    fun sessions() = SignClient.getListOfSettledSessions().filter { it.metaData != null }

    fun disconnect(topic: String) {
        SignClient.disconnect(Sign.Params.Disconnect(sessionTopic = topic)) { error -> loge(error.throwable) }
    }

    private fun setup(meta: WalletConnectMeta) {
        this.meta = meta
    }

    fun meta() = meta

    companion object {
        private lateinit var instance: WalletConnect

        fun init(meta: WalletConnectMeta) {
            ioScope {
                val application = LifecycleObserver.context()?.applicationContext as Application
                setup(application, meta)
                instance = WalletConnect().apply { setup(meta) }
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
            redirect = meta.redirect,
        )
    )

    SignClient.initialize(initString) { error -> loge(error.throwable) }

    SignClient.setDappDelegate(WalletConnectDappDelegate())
    SignClient.WebSocket.open { error -> logw(TAG, "open error:$error") }
}

private suspend fun pairUriInternal() = suspendCoroutine<String?> { continuation ->
    val namespaces = mapOf(
        "flow" to Sign.Model.Namespace.Proposal(
            chains = listOf("flow:${if (Fcl.isMainnet()) "mainnet" else "testnet"}"),
            methods = listOf("flow_authn", "flow_authz", "flow_user_sign"),
            events = listOf("chainChanged", "accountsChanged"),
            extensions = null
        )
    )

    val connectParams = Sign.Params.Connect(namespaces = namespaces)

    SignClient.connect(connectParams,
        onProposedSequence = { continuation.resume((it as Sign.Model.ProposedSequence.Pairing).uri) },
        onError = { error ->
            loge(error.throwable)
            continuation.resumeWithException(error.throwable)
        }
    )
}

private suspend fun pairInternal(uri: String) = suspendCoroutine<PollingResponse?> { continuation ->
    launchDeeplink(uri)
    bindAuthnHook(continuation)
}

data class WalletConnectMeta(
    // register at https://walletconnect.com/register to get a project ID
    val projectId: String,
    val name: String,
    val description: String,
    val url: String,
    val icon: String,
    val redirect: String,
)