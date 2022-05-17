package io.outblock.fcl.response

/**
 * Authentication response
 *
 * @property [address] address of the authenticated account
 * @property [status] status of the authentication (approved or declined)
 * @property [reason] if authentication is declined this property will contain more description
 */
data class AuthnResponse(
    val address: String?,
    val status: ResponseStatus,
    val reason: String?
)