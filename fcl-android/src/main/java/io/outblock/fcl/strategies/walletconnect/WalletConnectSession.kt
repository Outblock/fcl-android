package io.outblock.fcl.strategies.walletconnect

import com.walletconnect.sign.client.Sign


private var currentSession: Sign.Model.ApprovedSession? = null

fun Sign.Model.ApprovedSession.chainId(): String {
    val account = namespaces["flow"]!!.accounts.first()
    return account.replaceAfterLast(":", "").removeSuffix(":")
}

fun Sign.Model.ApprovedSession.address(): String {
    val account = namespaces["flow"]!!.accounts.first()
    return account.replaceBeforeLast(":", "").removePrefix(":")
}

internal fun updateWalletConnectSession(session: Sign.Model.ApprovedSession) {
    currentSession = session
}

internal fun currentWcSession() = currentSession