package io.outblock.fcl.request

import com.google.gson.annotations.SerializedName
import io.outblock.fcl.Fcl
import io.outblock.fcl.config.Config
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.provider.Provider
import io.outblock.fcl.provider.ServiceMethod
import io.outblock.fcl.strategies.execHttpPost
import io.outblock.fcl.strategies.walletconnect.WalletConnect
import io.outblock.fcl.utils.FclError
import io.outblock.fcl.utils.FclException
import io.outblock.fcl.utils.bringToForeground
import kotlinx.coroutines.runBlocking

internal class AuthnRequest {
    fun authenticate(provider: Provider): PollingResponse {
        if (provider.method == ServiceMethod.WC_RPC) {
            val uri = runBlocking { WalletConnect.get().pairUri() } ?: throw FclException(FclError.fetchAccountFailure)
            val response = runBlocking { WalletConnect.get().pair(uri) } ?: throw FclException(FclError.fetchAccountFailure)
            bringToForeground()
            return response
        }

        val model = AuthnRequestModel(
            appIdentifier = Fcl.config.get(Config.KEY.AppId),
            accountProofNonce = Fcl.config.get(Config.KEY.Nonce),
        )
        return runBlocking { execHttpPost(endpoint(provider).toString() + "authn", data = model) }
    }

    private fun endpoint(provider: Provider) =
        if (Fcl.isMainnet()) Fcl.providers.get(provider).endpoint else Fcl.providers.get(provider).testNetEndpoint

    companion object {
        private const val TAG = "FCLAuthn"
    }
}

internal class AuthnRequestModel(
    @SerializedName("appIdentifier")
    val appIdentifier: String?,
    @SerializedName("accountProofNonce")
    val accountProofNonce: String?,
)