package io.outblock.fcl.config

import com.nftco.flow.sdk.FlowChainId

class Config {

    private val map = mutableMapOf<String, String>()

    enum class KEY(val value: String) {
        accessNode("accessNode.api"),
        icon("app.detail.icon"),
        title("app.detail.title"),
        handshake("challenge.handshake"),
        scope("challenge.scope"),
        wallet("discovery.wallet"),
        authn("authn"),
        env("env"),
        location("location"),
        openIDScope("service.OpenID.scopes"),
        domainTag("fcl.appDomainTag"),
    }

    fun configLens(regex: String): Map<String, String> {
        val r = Regex(regex)
        return map.filter { it.key.matches(r) }.mapKeys { it.key.replace(r, "") }
    }

    fun get(key: KEY): String? = map[key.value]
    fun get(key: String): String? = map[key]

    fun put(key: KEY, value: String): Config = apply { map[key.value] = value }
    fun put(key: String, value: String): Config = apply { map[key] = value }

    fun remove(key: KEY): Config = apply { map.remove(key.value) }
    fun remove(key: String): Config = apply { map.remove(key) }

    fun clear(): Config = apply { map.clear() }
}

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
fun Config.envToChainID(env: String): FlowChainId {
    return FlowChainId.of(env)
}