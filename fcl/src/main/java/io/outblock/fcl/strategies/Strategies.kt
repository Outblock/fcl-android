package io.outblock.fcl.strategies

import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.models.response.Service
import io.outblock.fcl.provider.ServiceMethod
import io.outblock.fcl.strategies.walletconnect.executeWcRpc

internal suspend fun Service.executeStrategies(
    data: Any? = null,
): PollingResponse {
    val endpoint = this.endpoint ?: throw RuntimeException("missing endpoint")
    val params = this.params.orEmpty()

    return when (method) {
        ServiceMethod.WC_RPC.value -> executeWcRpc(endpoint, params, data)
        else -> execHttpPost(endpoint, params, data)
    }
}