package io.outblock.fcl.provider

import java.net.URL

enum class ServiceMethod(val value: String) {
    HTTP_RPC("HTTP/RPC"),
    HTTP_POST("HTTP/POST"),
    IFRAME_RPC("IFRAME/RPC"),
    POP_RPC("POP/RPC"),
    TAB_RPC("TAB/RPC"),
    EXT_RPC("EXT/RPC"),
    WC_RPC("WC/RPC"),
}

internal fun String?.toMethod(): ServiceMethod {
    if (this.isNullOrBlank()) {
        return ServiceMethod.HTTP_POST
    }
    return ServiceMethod.valueOf(this)
}

interface Provider {
    val title: String
    val method: ServiceMethod
    val endpoint: URL
    val testNetEndpoint: URL
}

data class Providers(
    private val providers: ArrayList<Provider> = ArrayList()
) {
    fun add(provider: Provider) {
        providers.add(provider)
    }

    fun get(provider: Provider): Provider {
        return providers.first { it.endpoint == provider.endpoint }
    }

    fun all() = providers.toList()
}

data class CustomProvider(
    override val title: String,
    override val method: ServiceMethod,
    override val endpoint: URL,
    override val testNetEndpoint: URL,
) : Provider

enum class WalletProvider(
    override val title: String,
    override val method: ServiceMethod,
    override val endpoint: URL,
    override val testNetEndpoint: URL,
) : Provider {
    DAPPER(
        title = "Dapper",
        method = ServiceMethod.HTTP_POST,
        endpoint = URL("https://dapper-http-post.vercel.app/api/"),
        // Do not know if dapper wallet has testnet url, use mainnet instead here
        testNetEndpoint = URL("https://dapper-http-post.vercel.app/api/"),
    ),
    BLOCTO(
        title = "Blocto",
        method = ServiceMethod.HTTP_POST,
        endpoint = URL("https://flow-wallet.blocto.app/api/flow/"),
        testNetEndpoint = URL("https://flow-wallet-testnet.blocto.app/api/flow/"),
    ),
    LILICO(
        title = "Lilico",
        method = ServiceMethod.WC_RPC,
        endpoint = URL("https://lilico.app/"),
        testNetEndpoint = URL("https://lilico.app/"),
    )
}