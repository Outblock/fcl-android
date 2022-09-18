package io.outblock.fcl

import android.os.Looper
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.simpleFlowScript
import io.outblock.fcl.config.AppMetadata
import io.outblock.fcl.config.Config
import io.outblock.fcl.models.FclResult
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.models.response.Service
import io.outblock.fcl.provider.Provider
import io.outblock.fcl.provider.Providers
import io.outblock.fcl.provider.WalletProvider
import io.outblock.fcl.request.*
import io.outblock.fcl.request.builder.FclBuilder
import io.outblock.fcl.strategies.walletconnect.WalletConnect
import io.outblock.fcl.strategies.walletconnect.WalletConnectMeta
import io.outblock.fcl.utils.ioScope
import io.outblock.fcl.utils.logd
import kotlinx.coroutines.runBlocking

object Fcl {
    val providers = Providers()

    val config = Config()

    var currentUser: User? = null
        private set

    const val version = "@outblock/fcl-android@0.0.1"

    init {
        providers.add(WalletProvider.LILICO)
        providers.add(WalletProvider.BLOCTO)
        providers.add(WalletProvider.DAPPER)
    }

    fun config(
        appMetadata: AppMetadata,
        env: FlowEnvironment,
        walletConnectMeta: WalletConnectMeta? = null,
    ): Config {

        walletConnectMeta?.let { WalletConnect.init(it) }

        return config.apply {
            with(appMetadata) {
                put(Config.KEY.Title, appName)
                put(Config.KEY.Icon, appIcon)
                put(Config.KEY.Location, location)
                appId?.let { put(Config.KEY.AppId, it) }
                nonce?.let { put(Config.KEY.Nonce, it) }
            }
            put(Config.KEY.Env, env.network.network)
            FlowApi.configure(env)
        }
    }

    /**
     * Starts a new authentication request for the provider.
     * Authentication process includes opening a browser with provided context for the user to sign in
     *
     * Example
     * ```kotlin
     * val auth = Fcl.authenticate(WalletProvider.BLOCTO)
     * ```
     *
     * @param [provider] provider used for authentication
     */
    fun authenticate(provider: Provider): FclResult<PollingResponse> {
        assert(Thread.currentThread() != Looper.getMainLooper().thread) { "can't call this method in main thread." }
        return processResult {
            AuthnRequest().authenticate(provider).apply {
                currentUser = User.fromAuthn(this)
            }
        }
    }

    fun authenticateAsync(provider: Provider, callback: (response: FclResult<PollingResponse>) -> Unit) {
        ioScope { callback(authenticate(provider)) }
    }

    /**
     * Mutate the chain: Send arbitrary transactions with your own signatures or via a user's wallet to perform state changes on chain.
     *
     * Example:
     * ```kotlin
     * val tid = Fcl.mutate {
     *   cadence("""
     *      transaction(test: String, testInt: Int) {
     *         prepare(signer: AuthAccount) {
     *            log(signer.address)
     *            log(test)
     *            log(testInt)
     *         }
     *      }
     *      """.trimIndent()
     *   )
     *   arg { string("Test2") }
     *   arg { int(1) }
     *   gaslimit(1000)
     * }
     * ```
     * @return transaction id
     *
     * @throws FCLException If run into problems
     */
    fun mutate(builder: FclBuilder.() -> Unit): FclResult<String> {
        assert(Thread.currentThread() != Looper.getMainLooper().thread) { "can't call this method in main thread." }
        return processResult {
            runBlocking { AuthzSend().send(builder) }
        }
    }

    /**
     * Query the chain: Send arbitrary Cadence scripts to the chain and receive back decoded values
     * Example
     * ```kotlin
     * val result = Fcl.query {
     *   cadence(
     *       """
     *       pub fun main(a: Int, b: Int, addr: Address): Int {
     *           log(addr)
     *           return a + b
     *       }
     *       """.trimIndent()
     *   )
     *   arg { int(7) }
     *   arg { int(3) }
     *   arg { address("0xba1132bc08f82fe2") }
     * }
     * ```
     *
     * @return executed result of cadence
     */
    fun query(builder: FclBuilder.() -> Unit): FclResult<String> {
        assert(Thread.currentThread() != Looper.getMainLooper().thread) { "can't call this method in main thread." }

        return processResult {
            val outBuilder = FclBuilder().apply { builder(this) }

            assert(!outBuilder.cadence.isNullOrBlank()) { "Script is empty" }

            logd("Fcl query", outBuilder)

            val response = FlowApi.get().simpleFlowScript {
                script { outBuilder.cadence!! }
                outBuilder.arguments.forEach { arg { it } }
            }
            String(response.bytes)
        }
    }

    fun signMessage(message: String): FclResult<SignMessageResponse> {
        assert(Thread.currentThread() != Looper.getMainLooper().thread) { "can't call this method in main thread." }

        return processResult {
            runBlocking { SignMessageRequest().request(message) }
        }
    }

    fun verifyAccountProof(includeDomainTag: Boolean = false): FclResult<Boolean> {
        assert(Thread.currentThread() != Looper.getMainLooper().thread) { "can't call this method in main thread." }
        return processResult {
            runBlocking { AccountProofRequest().request(includeDomainTag) }
        }
    }

    fun isMainnet(): Boolean = config.get(Config.KEY.Env) == FlowNetwork.MAINNET.network
}

class User(
    var fType: String = "USER",
    var fVsn: String = "1.0.0",
    val address: FlowAddress,
    var loggedIn: Boolean = false,
    var services: List<Service>?,
) {
    companion object {
        fun fromAuthn(authn: PollingResponse): User? {
            val address = authn.data?.address ?: return null
            return User(
                address = FlowAddress(address),
                services = authn.data.services,
                loggedIn = true,
            )
        }
    }
}

private fun <T> processResult(block: () -> T): FclResult<T> {
    return try {
        FclResult.Success(block())
    } catch (e: Throwable) {
        FclResult.Failure(e)
    }
}