package io.outblock.fcl

import android.content.Context
import com.nftco.flow.sdk.FlowAddress
import io.outblock.fcl.authn.FCLAuthn
import io.outblock.fcl.config.Config
import io.outblock.fcl.models.response.AuthnResponse
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.models.response.Service
import io.outblock.fcl.provider.Provider
import io.outblock.fcl.provider.Providers
import io.outblock.fcl.provider.WalletProvider

object FCL {
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
        walletNode: String,
        accessNode: String,
        env: String,
        scope: String,
        authn: String,
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
     * @param [context] application context used for opening a browser
     * @param [provider] provider used for authentication
     * @param [onComplete] callback function called on completion with response data
     */
    fun authenticate(
        context: Context,
        provider: Provider,
        onComplete: (AuthnResponse) -> Unit,
    ) {
        FCLAuthn().authenticate(context, provider) { resp ->
            onComplete.invoke(AuthnResponse(resp.data?.addr, resp.status, resp.reason))
            currentUser = User.fromAuthn(resp)
        }
    }

    fun unauthenticate() {

    }

    fun reauthenticate() {

    }

    fun authorization() {

    }

    fun signUserMessage(message: String): String {
        return ""
    }
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