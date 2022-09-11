package io.outblock.fcl.strategies.walletconnect

enum class WalletConnectMethod(val value: String) {
    AUTHN("flow_authn"),
    AUTHZ("flow_authz"),
    USER_SIGN("flow_user_sign"),
}