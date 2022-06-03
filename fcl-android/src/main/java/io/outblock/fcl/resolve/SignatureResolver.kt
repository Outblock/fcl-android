package io.outblock.fcl.resolve

import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowSignature
import com.nftco.flow.sdk.FlowTransaction
import com.nftco.flow.sdk.bytesToHex
import io.outblock.fcl.models.*

class SignatureResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        assert(ix.tag != Interaction.Tag.transaction.value) { "Interaction tag error" }

        val insideSigners = ix.findInsideSigners()

        val tx = ix.toFlowTransaction()

        ix.accounts[ix.proposer.orEmpty()]?.sequenceNum = tx.proposalKey.sequenceNumber.toInt()

        val insidePayload = tx.canonicalPayload.bytesToHex()

        val publishers = insideSigners.map { fetchSignature(ix, insidePayload, it) }

        publishers.forEach {
            val id = it.first
            val signature = it.second
            ix.accounts[id]?.signature = signature
        }

        val outsideSigners = ix.findOutsideSigners()
        val outsidePayload = encodeOutsideMessage(tx, ix, insideSigners)

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

    private fun encodeOutsideMessage(tx: FlowTransaction, ix: Interaction, insideSigners: List<String>): String {
        insideSigners.forEach {
            ix.accounts[it]?.let { account ->
                val address = account.addr ?: return@let
                val keyId = account.keyId ?: return@let
                val signature = account.signature ?: return@let

                tx.addPayloadSignature(FlowAddress(address), keyIndex = keyId, signature = FlowSignature(signature))
            }
        }
        // TODO not sure canonicalPaymentEnvelope is ok
        return tx.canonicalPaymentEnvelope.bytesToHex()
    }

}