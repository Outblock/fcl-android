package io.outblock.fcl.strategies.walletconnect

import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.nftco.flow.sdk.hexToBytes
import com.walletconnect.sign.client.Sign
import io.outblock.fcl.lifecycle.LifecycleObserver
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.utils.FclError
import io.outblock.fcl.utils.FclException
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun launchDeeplink(uri: String) {
    val context = LifecycleObserver.context() ?: throw FclException(FclError.invaildContext)
    val host = WalletConnect.get().meta()?.url ?: throw FclException(FclError.invaildURL)
    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("${host.removeSuffix("/")}?uri=${URLEncoder.encode(uri, "UTF-8")}")
    })
}

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
}

private fun Sign.Model.SessionRequestResponse.dispatchAuthz() {

}