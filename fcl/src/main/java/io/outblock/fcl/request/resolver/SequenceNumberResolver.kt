package io.outblock.fcl.request.resolver

import com.nftco.flow.sdk.FlowAddress
import io.outblock.fcl.FlowApi
import io.outblock.fcl.models.Interaction

class SequenceNumberResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        val proposer = ix.proposer
        val account = ix.accounts[proposer]
        val address = account?.addr
        val keyId = account?.keyId

        if (proposer == null || account == null || address == null || keyId == null) {
            throw RuntimeException("Some necessary data is null")
        }

        val flowAddress = FlowAddress(address)

        if (account.sequenceNum != null) {
            return
        }

        val flowAccount = FlowApi.get().getAccountAtLatestBlock(flowAddress) ?: throw RuntimeException("Get flow account error")

        ix.accounts[proposer]?.sequenceNum = flowAccount.keys[keyId].sequenceNumber
    }
}