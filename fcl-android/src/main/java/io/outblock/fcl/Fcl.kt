package io.outblock.fcl

import android.os.Looper
import androidx.annotation.WorkerThread
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.simpleFlowScript
import io.outblock.fcl.config.Config
import io.outblock.fcl.models.response.AuthnResponse
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.models.response.Service
import io.outblock.fcl.provider.Provider
import io.outblock.fcl.provider.Providers
import io.outblock.fcl.provider.WalletProvider
import io.outblock.fcl.request.AuthnRequest
import io.outblock.fcl.request.AuthzSend
import io.outblock.fcl.request.SignMessageSend
import io.outblock.fcl.request.builder.FclBuilder
import io.outblock.fcl.utils.ioScope
import kotlinx.coroutines.runBlocking

object Fcl {
    val providers = Providers()

    val config = Config()

    var currentUser: User? = null
        private set

    const val version = "@outblock/fcl-android@0.0.1"

    init {
        // TODO add from user
        providers.add(WalletProvider.DAPPER)
        providers.add(WalletProvider.BLOCTO)
    }

    fun config(
        appName: String,
        appIcon: String,
        location: String,
        env: String,
        walletNode: String = "",
        accessNode: String = "",
        scope: String = "",
        authn: String = "",
    ): Config {
        return config.apply {
            put(Config.KEY.Title, appName)
            put(Config.KEY.Icon, appIcon)
            put(Config.KEY.Location, location)
            put(Config.KEY.Wallet, walletNode)
            put(Config.KEY.AccessNode, accessNode)
            put(Config.KEY.Env, env)
            put(Config.KEY.Scope, scope)
            put(Config.KEY.Authn, authn)
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
    @WorkerThread
    fun authenticate(provider: Provider): AuthnResponse {
        assert(Thread.currentThread() != Looper.getMainLooper().thread) { "can't call this method in main thread." }

        val resp = AuthnRequest().authenticate(provider)
        currentUser = User.fromAuthn(resp)
        return AuthnResponse(resp.data?.addr, resp.status, resp.reason)
    }

    fun authenticateAsync(provider: Provider, callback: (response: AuthnResponse) -> Unit) {
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
    @WorkerThread
    fun mutate(builder: FclBuilder.() -> Unit): String {
        assert(Thread.currentThread() != Looper.getMainLooper().thread) { "can't call this method in main thread." }
        return runBlocking { AuthzSend().send(builder) }
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
    @WorkerThread
    fun query(builder: FclBuilder.() -> Unit): String {
        assert(Thread.currentThread() != Looper.getMainLooper().thread) { "can't call this method in main thread." }

        val outBuilder = FclBuilder().apply { builder(this) }

        assert(!outBuilder.cadence.isNullOrBlank()) { "Script is empty" }

        val response = FlowApi.get().simpleFlowScript {
            script { outBuilder.cadence!! }
            outBuilder.arguments.forEach { arg { it } }
        }
        return String(response.bytes)
    }

    /**
     * TODO : not support right now
     */
    @WorkerThread
    fun signMessage(message: String): String {
        assert(Thread.currentThread() != Looper.getMainLooper().thread) { "can't call this method in main thread." }

        return runBlocking { SignMessageSend().sign(message) }
    }

    fun unauthenticate() {

    }

    fun reauthenticate() {

    }

    fun authorization() {

    }

    fun isMainnet(): Boolean = config.get(Config.KEY.Env) == "mainnet"
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
            val address = authn.data?.addr ?: return null
            return User(
                address = FlowAddress(address),
                services = authn.data.services,
                loggedIn = true,
            )
        }
    }
}