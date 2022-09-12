package io.outblock.fcl.strategies.walletconnect

import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
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
internal fun launchDeeplink(uri: String?) {
    val context = LifecycleObserver.context() ?: throw FclException(FclError.invaildContext)
    val host = WalletConnect.get().meta()?.url ?: throw FclException(FclError.invaildURL)
    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
        val param = if (uri.isNullOrBlank()) "" else "?uri=${URLEncoder.encode(uri, "UTF-8")}"
        data = Uri.parse("${host.removeSuffix("/")}$param")
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
    endpoint: String,
    params: Map<String, String>? = mapOf(),
    data: Any? = null,
) = suspendCoroutine<PollingResponse> { continuation ->
    val session = currentWcSession() ?: throw FclException(FclError.unauthenticated)
    val requestParams = Sign.Params.Request(
        sessionTopic = requireNotNull(session.topic),
        method = endpoint,
        params = "[${Gson().toJson(data)}]",
        chainId = session.chainId()
    )

    when (endpoint) {
        WalletConnectMethod.AUTHZ.value -> bindAuthzHook(continuation)
        WalletConnectMethod.PRE_AUTHZ.value -> bindPreAuthzHook(continuation)
        WalletConnectMethod.USER_SIGNATURE.value -> bindUserSignHook(continuation)
        WalletConnectMethod.SIGN_PROPOSER.value -> bindSignProposerHook(continuation)
        WalletConnectMethod.SIGN_PAYER.value -> bindSignPayerHook(continuation)
    }

    SignClient.request(requestParams) { error ->
        continuation.resumeWithException(
            FclException(
                FclError.invaildService,
                exception = error.throwable
            )
        )
    }

    if (authMethod.contains(endpoint)) {
        launchDeeplink("")
    }
}

private val authMethod by lazy {
    listOf(
        WalletConnectMethod.AUTHZ.value,
        WalletConnectMethod.SIGN_PROPOSER.value,
        WalletConnectMethod.USER_SIGNATURE.value,
    )
}