package io.outblock.fcl.request

import io.outblock.fcl.Fcl
import io.outblock.fcl.execHttpPost
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.provider.Provider
import kotlinx.coroutines.runBlocking

internal class AuthnRequest {
    fun authenticate(provider: Provider): PollingResponse {
        return runBlocking { execHttpPost(endpoint(provider).toString() + "authn") }
    }

    private fun endpoint(provider: Provider) =
        if (Fcl.isMainnet()) Fcl.providers.get(provider).endpoint else Fcl.providers.get(provider).testNetEndpoint

    companion object {
        private const val TAG = "FCLAuthn"
    }
}