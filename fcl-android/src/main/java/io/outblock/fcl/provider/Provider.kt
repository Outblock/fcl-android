package io.outblock.fcl.provider

import java.net.URL

enum class ServiceMethod {
    HTTP_POST,
    HTTP_GET,
    IFRAME,
    IFRAME_RPC,
    DATA,
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
        "Dapper",
        ServiceMethod.HTTP_POST,
        URL("https://dapper-http-post.vercel.app/api/"),
        // Do not know if dapper wallet has testnet url, use mainnet instead here
        URL("https://dapper-http-post.vercel.app/api/"),
    ),
    BLOCTO(
        "Blocto",
        ServiceMethod.HTTP_POST,
        URL("https://flow-wallet.blocto.app/api/flow/"),
        URL("https://flow-wallet-testnet.blocto.app/api/flow/"),
    )
}