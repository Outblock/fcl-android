package io.outblock.fcl

import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.impl.FlowAccessApiImpl
import io.outblock.fcl.utils.addAddressPrefix

internal object FlowApi {
    private const val HOST_MAINNET = "access.mainnet.nodes.onflow.org"
    private const val HOST_TESTNET = "access.devnet.nodes.onflow.org"
    private const val HOST_CANARYNET = "access.canary.nodes.onflow.org"

    private var api: FlowAccessApi? = null

    private var chainId: FlowChainId? = null

    fun configure(environment: FlowEnvironment) {
        this.chainId = environment.network.network.envToChainID()
        (api as? FlowAccessApiImpl)?.close()
        Flow.configureDefaults(chainId = this.chainId!!, addressRegistry = environment.toAddressRegistry())
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

private fun FlowEnvironment.toAddressRegistry(): AddressRegistry {
    val chainId = network.network.envToChainID()
    return AddressRegistry().apply {
        addressRegistry?.forEach { register(it.first, FlowAddress(it.second.addAddressPrefix()), chainId) }
        if (network == FlowNetwork.TESTNET) {
            register("0xFCLCrypto", FlowAddress("0x74daa6f9c7ef24b1"), chainId)
        } else {
            register("0xFCLCrypto", FlowAddress("0xb4b82a1c9d21d284"), chainId)
        }
    }
}

class FlowEnvironment(
    val network: FlowNetwork,
    val addressRegistry: List<Pair<String, String>>? = null,
)

/**
 * Get FlowChainId by env
 * -------------------------
 * flow-mainnet
 * flow-testnet
 * flow-canarynet
 * flow-emulator
 * unknown
 * -------------------------
 */
private fun String.envToChainID(): FlowChainId {
    val id = (if (this.startsWith("flow-")) this else "flow-$this").lowercase()
    return FlowChainId.of(id)
}

enum class FlowNetwork(val network: String) {
    MAINNET("mainnet"),
    TESTNET("testnet"),
    CANARYNET("canarynet"),
    EMULATOR("emulator"),
    UNKNOWN("unknown"),
}