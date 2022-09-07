package io.outblock.fcl.request.resolver

import com.nftco.flow.sdk.DomainTag
import com.nftco.flow.sdk.bytesToHex
import io.outblock.fcl.models.*

class SignatureResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        assert(ix.tag.lowercase() != Interaction.Tag.transaction.value) { "Interaction tag error" }

        val insideSigners = ix.findInsideSigners()

        val tx = ix.toFlowTransaction()

        ix.accounts[ix.proposer.orEmpty()]?.sequenceNum = tx.proposalKey.sequenceNumber.toInt()

        val insidePayload = (DomainTag.TRANSACTION_DOMAIN_TAG + tx.canonicalPayload).bytesToHex()

        val publishers = insideSigners.map { fetchSignature(ix, insidePayload, it) }

        publishers.forEach {
            val id = it.first
            val signature = it.second
            ix.accounts[id]?.signature = signature
        }

        val outsideSigners = ix.findOutsideSigners()
        val outsidePayload = (DomainTag.TRANSACTION_DOMAIN_TAG + ix.toFlowTransaction().canonicalAuthorizationEnvelope).bytesToHex()

        val outPublishers = outsideSigners.map { fetchSignature(ix, outsidePayload, it) }

        outPublishers.forEach {
            val id = it.first
            val signature = it.second
            ix.accounts[id]?.signature = signature
        }
    }

    private suspend fun fetchSignature(ix: Interaction, payload: String, id: String): Pair<String, String> {
        val acct = ix.accounts[id] ?: throw RuntimeException("Can't find account by id")
        val signingFunction = acct.signingFunction ?: throw RuntimeException()
        val signable = buildSignable(ix, payload, acct)

        val response = signingFunction.invoke(signable)

        return Pair(id, response.data?.signature ?: response.compositeSignature?.signature.orEmpty())
    }

    private fun buildSignable(ix: Interaction, payload: String, account: SignableUser): Signable {
        return Signable(
            message = payload,
            keyId = account.keyId,
            addr = account.addr,
            roles = account.role,
            cadence = ix.message.cadence,
            args = ix.message.arguments.mapNotNull { ix.arguments[it]?.asArgument },
            interaction = ix,
        ).apply {
            voucher = generateVoucher()
        }
    }
}