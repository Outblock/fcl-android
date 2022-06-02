package io.outblock.fcl

import com.nftco.flow.sdk.Flow
import com.nftco.flow.sdk.FlowAccessApi
import com.nftco.flow.sdk.FlowChainId
import com.nftco.flow.sdk.impl.FlowAccessApiImpl

internal object FlowApi {
    private const val HOST_MAINNET = "access.mainnet.nodes.onflow.org"
    private const val HOST_TESTNET = "access.devnet.nodes.onflow.org"
    private const val HOST_CANARYNET = "access.canary.nodes.onflow.org"

    private var api: FlowAccessApi? = null

    private var chainId: FlowChainId? = null

    fun configure(chainId: FlowChainId) {
        this.chainId = chainId
        (api as? FlowAccessApiImpl)?.close()
        Flow.configureDefaults(chainId = chainId)
        api = Flow.newAccessApi(chainIdToNet(), 9000)
    }

    fun get() = api ?: Flow.newAccessApi(chainIdToNet(), 9000)

    private fun chainIdToNet(): String {
        return when (chainId ?: FlowChainId.MAINNET) {
            FlowChainId.TESTNET -> HOST_TESTNET
            FlowChainId.CANARYNET -> HOST_CANARYNET
            else -> HOST_MAINNET
        }
    }
}