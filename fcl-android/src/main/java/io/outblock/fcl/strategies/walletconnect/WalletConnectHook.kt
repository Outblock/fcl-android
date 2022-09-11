package io.outblock.fcl.strategies.walletconnect

import io.outblock.fcl.models.response.PollingResponse
import kotlin.coroutines.Continuation

internal var authnHook: Continuation<PollingResponse>? = null
    private set

internal var authzHook: Continuation<String?>? = null
    private set

internal fun bindAuthnHook(hook: Continuation<PollingResponse?>) {
    authnHook = hook
}

internal fun releaseAuthnHook() {
    authnHook = null
}

internal fun bindAuthzHook(hook: Continuation<String?>) {
    authzHook = hook
}

internal fun releaseAuthzHook() {
    authzHook = null
}