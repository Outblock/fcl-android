package io.outblock.fcl.strategies.walletconnect

import com.google.gson.Gson
import com.nftco.flow.sdk.hexToBytes
import com.walletconnect.sign.client.Sign
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.utils.FclError
import io.outblock.fcl.utils.FclException
import io.outblock.fcl.utils.bringToForeground
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal fun dispatchWcRequestResponse(response: Sign.Model.SessionRequestResponse) {
    when (response.method) {
        WalletConnectMethod.AUTHN.value -> response.dispatch(authnHook) { releaseAuthnHook() }
        WalletConnectMethod.AUTHZ.value -> response.dispatch(authzHook) {
            releaseAuthzHook()
            bringToForeground()
        }
        WalletConnectMethod.PRE_AUTHZ.value -> response.dispatch(preAuthzHook) { releasePreAuthzHook() }
        WalletConnectMethod.SIGN_PROPOSER.value -> response.dispatch(signProposerHook) { releaseSignProposerHook() }
        WalletConnectMethod.SIGN_PAYER.value -> response.dispatch(signPayerHook) {
            releaseSignPayerHook()
            bringToForeground()
        }
        WalletConnectMethod.USER_SIGNATURE.value -> response.dispatch(userSignHook) {
            releaseUserSignHook()
            bringToForeground()
        }
    }
}

private fun Sign.Model.SessionRequestResponse.pollingResponse(): PollingResponse {
    val json = String((result as Sign.Model.JsonRpcResponse.JsonRpcResult).result.hexToBytes())
    return Gson().fromJson(json, PollingResponse::class.java)
}

private fun Sign.Model.SessionRequestResponse.dispatch(hook: Continuation<PollingResponse>?, callback: () -> Unit) {
    try {
        hook?.resume(pollingResponse())
    } catch (e: Exception) {
        hook?.resumeWithException(FclException(FclError.invaildService, exception = e))
    }
    callback()
}
