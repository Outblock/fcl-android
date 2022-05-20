package io.outblock.fcl

import android.content.Context
import io.outblock.fcl.authn.FCLAuthn
import io.outblock.fcl.config.Config
import io.outblock.fcl.provider.Provider
import io.outblock.fcl.provider.Providers
import io.outblock.fcl.provider.WalletProvider
import io.outblock.fcl.response.AuthnResponse

object FCL {
    val providers = Providers()

    val config = Config()

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
    ) {
        with(config) {
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
        FCLAuthn().authenticate(context, provider, onComplete)
    }
}