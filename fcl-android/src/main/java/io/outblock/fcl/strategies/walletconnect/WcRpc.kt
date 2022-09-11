package io.outblock.fcl.strategies.walletconnect

import android.content.Intent
import android.net.Uri
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import io.outblock.fcl.Fcl
import io.outblock.fcl.lifecycle.LifecycleObserver
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.utils.FclError
import io.outblock.fcl.utils.FclException
import io.outblock.fcl.utils.loge
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * open wc app(lilico), pair
 */
internal fun launchDeeplink(uri: String) {
    val context = LifecycleObserver.context() ?: throw FclException(FclError.invaildContext)
    val host = WalletConnect.get().meta()?.url ?: throw FclException(FclError.invaildURL)
    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("${host.removeSuffix("/")}?uri=${URLEncoder.encode(uri, "UTF-8")}")
    })
}

/**
 * fetch wallet connect pair uri
 */
internal suspend fun wcFetchPairUriInternal() = suspendCoroutine<String?> { continuation ->
    val namespaces = mapOf(
        "flow" to Sign.Model.Namespace.Proposal(
            chains = listOf("flow:${if (Fcl.isMainnet()) "mainnet" else "testnet"}"),
            methods = WalletConnectMethod.values().map { it.value },
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

/**
 * open lilico & request authn service
 */
internal suspend fun wcPairInternal(uri: String) = suspendCoroutine<PollingResponse?> { continuation ->
    launchDeeplink(uri)
    bindAuthnHook(continuation)
}

internal suspend fun executeWcRpc(
    url: String,
    params: Map<String, String>? = mapOf(),
    data: Any? = null,
): PollingResponse {

}