package io.outblock.fcl

import android.content.Context
import io.outblock.fcl.authn.FCLAuthn
import io.outblock.fcl.provider.Provider
import io.outblock.fcl.provider.Providers
import io.outblock.fcl.provider.WalletProvider
import io.outblock.fcl.response.AuthnResponse

object FCL {
    val providers = Providers()

    init {
        // TODO add from user
        providers.add(WalletProvider.DAPPER)
        providers.add(WalletProvider.BLOCTO)
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