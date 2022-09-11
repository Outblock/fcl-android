package io.outblock.fcl.strategies.walletconnect

import io.outblock.fcl.models.response.PollingResponse
import kotlin.coroutines.Continuation

var authnHook: Continuation<PollingResponse>? = null
    private set

var authzHook: Continuation<String?>? = null
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