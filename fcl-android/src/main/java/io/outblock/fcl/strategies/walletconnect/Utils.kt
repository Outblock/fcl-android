package io.outblock.fcl.strategies.walletconnect

import com.google.gson.Gson
import com.nftco.flow.sdk.hexToBytes
import com.walletconnect.sign.client.Sign
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.utils.FclError
import io.outblock.fcl.utils.FclException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal fun dispatchWcRequestResponse(response: Sign.Model.SessionRequestResponse) {
    when (response.method) {
        "flow_authn" -> response.dispatchAuthn()
        "flow_authz" -> response.dispatchAuthz()
    }
}

private fun Sign.Model.SessionRequestResponse.dispatchAuthn() {
    try {
        val json = String((result as Sign.Model.JsonRpcResponse.JsonRpcResult).result.hexToBytes())
        val response = Gson().fromJson(json, PollingResponse::class.java)
        authnHook?.resume(response)
    } catch (e: Exception) {
        authnHook?.resumeWithException(FclException(FclError.fetchAccountFailure, exception = e))
    }
    releaseAuthnHook()
}

private fun Sign.Model.SessionRequestResponse.dispatchAuthz() {

}