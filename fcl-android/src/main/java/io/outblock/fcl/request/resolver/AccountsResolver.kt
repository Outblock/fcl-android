package io.outblock.fcl.request.resolver

import io.outblock.fcl.Fcl
import io.outblock.fcl.models.*
import io.outblock.fcl.models.response.FCLServiceType
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.models.response.Service
import io.outblock.fcl.strategies.executeStrategies
import io.outblock.fcl.utils.serviceOfType

class AccountsResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        if (!ix.isTransaction()) {
            return
        }

        collectAccounts(ix)
    }

    private suspend fun collectAccounts(ix: Interaction) {
        val currentUser = Fcl.currentUser ?: throw RuntimeException("FCL unauthenticated")
        assert(currentUser.loggedIn, lazyMessage = { "FCL unauthenticated" })

        val service = currentUser.services.serviceOfType(FCLServiceType.preAuthz) ?: throw RuntimeException("missing preAuthz")

        val preSignable = ix.buildPreSignable(Roles())

        val response = service.executeStrategies(data = preSignable)

        val signableUsers = response.getAccounts()
        val accounts = mutableMapOf<String, SignableUser>()

        ix.authorizations.clear()
        signableUsers.forEach { user ->
            val tempID = "${user.addr}-${user.keyId}"
            user.tempId = tempID

            if (accounts.keys.contains(tempID)) {
                accounts[tempID]?.role?.merge(user.role)
            }

            accounts[tempID] = user

            if (user.role.proposer) {
                ix.proposer = tempID
            }

            if (user.role.payer) {
                ix.payer = tempID
            }

            if (user.role.authorizer) {
                ix.authorizations.add(tempID)
            }
        }

        ix.accounts = accounts
    }

    private fun PollingResponse.getAccounts(): List<SignableUser> {
        val axs = mutableListOf<Pair<String, Service>>()
        data?.proposer?.let { axs.add(Pair("PROPOSER", it)) }

        data?.payer?.forEach { axs.add(Pair("PAYER", it)) }

        data?.authorization?.forEach { axs.add(Pair("AUTHORIZER", it)) }

        return axs.mapNotNull {
            val role = it.first
            val service = it.second
            val address = service.identity?.address
            val keyId = service.identity?.keyId

            if (address == null || keyId == null) {
                null
            } else {
                SignableUser(
                    tempId = "$address|$keyId",
                    addr = address,
                    keyId = keyId,
                    role = Roles(
                        proposer = role == "PROPOSER",
                        authorizer = role == "AUTHORIZER",
                        payer = role == "PAYER",
                    )
                ) { data ->
                    service.executeStrategies(data = data)
                }
            }
        }
    }
}