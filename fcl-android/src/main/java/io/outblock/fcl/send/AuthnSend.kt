package io.outblock.fcl.send

import io.outblock.fcl.FCL
import io.outblock.fcl.execHttpPost
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.provider.Provider
import kotlinx.coroutines.runBlocking

internal class AuthnSend {
    fun authenticate(provider: Provider): PollingResponse {
        return runBlocking { execHttpPost(FCL.providers.get(provider).endpoint.toString() + "authn") }
    }

    companion object {
        private const val TAG = "FCLAuthn"
    }
}