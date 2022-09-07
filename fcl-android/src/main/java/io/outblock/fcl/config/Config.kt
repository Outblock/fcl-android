package io.outblock.fcl.config

import java.util.regex.Pattern

class Config {

    private val map = mutableMapOf<String, String>()

    enum class KEY(val value: String) {
        AccessNode("accessNode.api"),
        Icon("app.detail.icon"),
        Title("app.detail.title"),
        Handshake("challenge.handshake"),
        Scope("challenge.scope"),
        Wallet("discovery.wallet"),
        Authn("authn"),
        Env("env"),
        Location("location"),
        OpenIDScope("service.OpenID.scopes"),
        DomainTag("fcl.appDomainTag"),
        AppId("appIdentifier"),
        Nonce("accountProofNonce"),
        WcProjectId("wc.projectId"),
        WcName("wc.name"),
        WcDescription("wc.description"),
        WcUrl("wc.url"),
        WcIcons("wc.icons"),
        WcRedirect("wc.redirect")
    }

    fun configLens(regex: String): Map<String, String> {
        val r = Pattern.compile(regex)
        return map.filter { r.matcher(it.key).find() }.mapKeys { it.key.replace(Regex(regex), "") }
    }

    fun get(key: KEY): String? = map[key.value]
    fun get(key: String): String? = map[key]

    fun put(key: KEY, value: String): Config = apply { put(key.value, value) }

    fun put(key: String, value: String): Config = apply {
        map[key] = value
    }

    fun remove(key: KEY): Config = apply { map.remove(key.value) }
    fun remove(key: String): Config = apply { map.remove(key) }

    fun clear(): Config = apply { map.clear() }

    fun data() = map.toMap()
}

class AppMetadata(
    val appName: String,
    val appIcon: String,
    val location: String = "",
    val appId: String? = null,
    val nonce: String? = null,
)