package io.outblock.fcl.strategies.walletconnect

import io.outblock.fcl.models.response.PollingResponse
import kotlin.coroutines.Continuation

internal var authnHook: Continuation<PollingResponse>? = null
    private set

internal var authzHook: Continuation<PollingResponse>? = null
    private set

internal var preAuthzHook: Continuation<PollingResponse>? = null
    private set

internal var userSignHook: Continuation<PollingResponse>? = null
    private set

internal var signProposerHook: Continuation<PollingResponse>? = null
    private set

internal var signPayerHook: Continuation<PollingResponse>? = null
    private set

internal fun bindAuthnHook(hook: Continuation<PollingResponse>) {
    authnHook = hook
}

internal fun releaseAuthnHook() {
    authnHook = null
}

internal fun bindAuthzHook(hook: Continuation<PollingResponse>) {
    authzHook = hook
}

internal fun releaseAuthzHook() {
    authzHook = null
}

internal fun bindPreAuthzHook(hook: Continuation<PollingResponse>) {
    preAuthzHook = hook
}

internal fun releasePreAuthzHook() {
    preAuthzHook = null
}

internal fun bindUserSignHook(hook: Continuation<PollingResponse>) {
    userSignHook = hook
}

internal fun releaseUserSignHook() {
    userSignHook = null
}

internal fun bindSignProposerHook(hook: Continuation<PollingResponse>) {
    signProposerHook = hook
}

internal fun releaseSignProposerHook() {
    signProposerHook = null
}

internal fun bindSignPayerHook(hook: Continuation<PollingResponse>) {
    signPayerHook = hook
}

internal fun releaseSignPayerHook() {
    signPayerHook = null
}