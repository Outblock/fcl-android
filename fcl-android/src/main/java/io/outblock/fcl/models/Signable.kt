package io.outblock.fcl.models


import com.google.gson.annotations.SerializedName
import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.cadence.Field
import io.outblock.fcl.FlowApi
import io.outblock.fcl.models.response.PollingResponse
import io.outblock.fcl.utils.createFlowField
import io.outblock.fcl.utils.removeAddressPrefix

data class Signable(
    @SerializedName("addr")
    val addr: String? = null,
//    @SerializedName("app")
//    val app: App,
    @SerializedName("args")
    val args: List<AsArgument>,
    @SerializedName("cadence")
    val cadence: String? = null,
    @SerializedName("client")
    val client: Client? = null,
    @SerializedName("data")
    val data: Data? = null,
    @SerializedName("f_type")
    val fType: String = "Signable",
    @SerializedName("f_vsn")
    val fVsn: String = "1.0.1",
    @SerializedName("interaction")
    val interaction: Interaction,
    @SerializedName("keyId")
    val keyId: Int?,
    @SerializedName("message")
    val message: String,
    @SerializedName("roles")
    val roles: Roles,
//    @SerializedName("service")
//    val service: Any,
    @SerializedName("voucher")
    var voucher: Voucher? = null,
) {
}

data class App(
    @SerializedName("icon")
    val icon: String,
    @SerializedName("title")
    val title: String
)

data class Client(
    @SerializedName("fclLibrary")
    val fclLibrary: String,
    @SerializedName("fclVersion")
    val fclVersion: String,
    @SerializedName("hostname")
    val hostname: Any
)

class Data

data class Interaction(
    @SerializedName("account")
    var account: Account = Account(),
    @SerializedName("accounts")
    var accounts: Map<String, SignableUser> = mapOf(),
    @SerializedName("arguments")
    var arguments: Map<String, Argument> = mapOf(),
    @SerializedName("assigns")
    var assigns: Map<String, String> = mapOf(),
    @SerializedName("authorizations")
    var authorizations: MutableList<String> = mutableListOf(),
    @SerializedName("block")
    var block: Block = Block(),
    @SerializedName("collection")
    var collection: Id = Id(),
    @SerializedName("events")
    var events: Events = Events(),
    @SerializedName("message")
    var message: Message = Message(),
    @SerializedName("params")
    var params: Map<String, String> = mapOf(),
    @SerializedName("payer")
    var payer: String? = null,
    @SerializedName("proposer")
    var proposer: String? = null,
    @SerializedName("status")
    var status: String = Status.ok.value,
    @SerializedName("tag")
    var tag: String = Tag.unknown.value,
    @SerializedName("transaction")
    var transaction: Id = Id(),
    var reason: String? = null,
) {

    class Collection

    enum class Tag(val value: String) {
        unknown("UNKNOWN"),
        script("SCRIPT"),
        transaction("TRANSACTION"),
        getTransactionStatus("GET_TRANSACTION_STATUS"),
        getAccount("GET_ACCOUNT"),
        getEvents("GET_EVENTS"),
        getLatestBlock("GET_LATEST_BLOCK"),
        ping("PING"),
        getTransaction("GET_TRANSACTION"),
        getBlockById("GET_BLOCK_BY_ID"),
        getBlockByHeight("GET_BLOCK_BY_HEIGHT"),
        getBlock("GET_BLOCK"),
        getBlockHeader("GET_BLOCK_HEADER"),
        getCollection("GET_COLLECTION"),
    }

    enum class Status(val value: String) {
        ok("OK"),
        bad("BAD"),
    }
}

class Id(
    var id: String? = null,
)

class Block(
    var id: String? = null,
    var height: Int? = null,
    var isSealed: Boolean? = null,
)

class Account(
    @SerializedName("addr")
    var addr: String? = null,
)

data class SignableUser(
    @SerializedName("addr")
    val addr: String? = null,
    @SerializedName("keyId")
    val keyId: Int? = null,
    @SerializedName("kind")
    val kind: String? = null,
    @SerializedName("role")
    val role: Roles,
    @SerializedName("sequenceNum")
    var sequenceNum: Int? = null,
    @SerializedName("signature")
    var signature: String? = null,
    @SerializedName("tempId")
    var tempId: String? = null,

    var signingFunction: (suspend (data: String) -> PollingResponse)? = null,
)

data class Argument(
    @SerializedName("asArgument")
    val asArgument: AsArgument,
    @SerializedName("kind")
    val kind: String,
    @SerializedName("tempId")
    val tempId: String,
    @SerializedName("value")
    val value: String,
    @SerializedName("xform")
    val xform: Xform
) {

    data class Xform(
        @SerializedName("label")
        val label: String
    )
}


data class AsArgument(
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: String
)

data class Events(
    @SerializedName("blockIds")
    val blockIds: List<String>? = listOf(),
    @SerializedName("eventType")
    var eventType: String? = null,
    @SerializedName("start")
    var start: String? = null,
    @SerializedName("end")
    var end: String? = null,
)

data class Message(
    @SerializedName("arguments")
    var arguments: List<String> = listOf(),
    @SerializedName("authorizations")
    var authorizations: List<String> = listOf(),
    @SerializedName("cadence")
    var cadence: String? = null,
    @SerializedName("computeLimit")
    var computeLimit: Int? = null,
    @SerializedName("params")
    var params: List<String> = listOf(),
    @SerializedName("refBlock")
    var refBlock: String? = null,
)

data class Roles(
    @SerializedName("authorizer")
    var authorizer: Boolean = false,
    @SerializedName("payer")
    var payer: Boolean = false,
    @SerializedName("proposer")
    var proposer: Boolean = false,
) {
    fun merge(role: Roles) {
        proposer = proposer || role.proposer
        authorizer = authorizer || role.authorizer
        payer = payer || role.payer
    }
}

data class Voucher(
    @SerializedName("arguments")
    val arguments: List<AsArgument>?,
    @SerializedName("authorizers")
    val authorizers: List<String>?,
    @SerializedName("cadence")
    val cadence: String?,
    @SerializedName("computeLimit")
    val computeLimit: Int?,
    @SerializedName("payer")
    val payer: String?,
    @SerializedName("payloadSigs")
    val payloadSigs: List<Singature>?,
    @SerializedName("envelopeSigs")
    val envelopeSigs: List<Singature>?,
    @SerializedName("proposalKey")
    val proposalKey: ProposalKey,
    @SerializedName("refBlock")
    val refBlock: String?,
)

data class Singature(
    @SerializedName("address")
    val address: String,
    @SerializedName("keyId")
    val keyId: Int?,
    @SerializedName("sig")
    val sig: String?,
)

data class ProposalKey(
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("keyId")
    val keyId: Int? = null,
    @SerializedName("sequenceNum")
    val sequenceNum: Int? = null,
)

class PreSignable(
    @SerializedName("f_type")
    val fType: String = "PreSignable",
    @SerializedName("f_vsn")
    val fVsn: String = "1.0.1",
    @SerializedName("roles")
    val roles: Roles,
    @SerializedName("cadence")
    val cadence: String,
    @SerializedName("args")
    val args: List<AsArgument> = listOf(),
    @SerializedName("data")
    val data: Map<String, String> = mapOf(),
    @SerializedName("interaction")
    val interaction: Interaction = Interaction(),
//    @SerializedName("voucher")
//    val voucher: Voucher,
)

fun <T> Field<T>.toFclArgument(): Argument {
    return Argument(
        kind = "ARGUMENT",
        asArgument = AsArgument(type, value.toString()),
        value = value.toString(),
        xform = Argument.Xform(type),
        tempId = randomId(10),
    )
}

fun Interaction.toFlowTransaction(): FlowTransaction {
    val proposalKey = createFlowProposalKey()

    val payerAccount = payer
    val payerAddress = accounts[payerAccount]?.addr ?: throw RuntimeException("missing payer")

    val tx = FlowTransaction(
        script = FlowScript(message.cadence.orEmpty()),
        arguments = message.arguments.mapNotNull { arguments[it]?.asArgument }.mapNotNull { createFlowField(it.type, it.value) }
            .map { FlowArgument(it) },
        referenceBlockId = FlowId(message.refBlock.orEmpty()),
        gasLimit = (message.computeLimit ?: 100).toLong(),
        proposalKey = proposalKey,
        payerAddress = FlowAddress(payerAddress.orEmpty()),
        authorizers = authorizations.mapNotNull { accounts[it]?.addr }.distinct().map { FlowAddress(it) }
    )

    findInsideSigners().forEach { signer ->
        accounts[signer]?.let {
            val address = it.addr
            val keyId = it.keyId
            val signature = it.signature

            tx.addPayloadSignature(FlowAddress(address.orEmpty()), keyIndex = keyId ?: 0, signature = FlowSignature(signature.orEmpty()))
        }
    }

    findOutsideSigners().forEach { signer ->
        accounts[signer]?.let {
            val address = it.addr
            val keyId = it.keyId
            val signature = it.signature

            tx.addEnvelopeSignature(FlowAddress(address.orEmpty()), keyIndex = keyId ?: 0, signature = FlowSignature(signature.orEmpty()))
        }
    }
    return tx
}

fun Interaction.createFlowProposalKey(): FlowTransactionProposalKey {
    val proposer = this.proposer
    val account = accounts[proposer]
    val address = account?.addr
    val keyId = account?.keyId

    if (proposer == null || account == null || address == null || keyId == null) {
        throw RuntimeException("Invalid proposer")
    }

    val flowAddress = FlowAddress(address)

    if (account.sequenceNum == null) {
        val flowAccount = FlowApi.get().getAccountAtLatestBlock(flowAddress) ?: throw RuntimeException("Get flow account error")
        account.sequenceNum = flowAccount.keys[keyId].sequenceNumber
    }

    return FlowTransactionProposalKey(
        address = FlowAddress(address),
        keyIndex = keyId,
        sequenceNumber = (account.sequenceNum ?: 0).toLong()
    )
}

fun Interaction.createProposalKey(): ProposalKey {
    val proposer = proposer ?: return ProposalKey()
    val account = accounts[proposer] ?: return ProposalKey()
    return ProposalKey(
        address = account.addr?.removeAddressPrefix(),
        keyId = account.keyId,
        sequenceNum = account.sequenceNum,
    )
}

fun Interaction.isTransaction() = tag == Interaction.Tag.transaction.value

fun Interaction.isScript() = tag == Interaction.Tag.script.value

fun Interaction.buildPreSignable(roles: Roles): PreSignable {
    return PreSignable(
        roles = roles,
        cadence = message.cadence.orEmpty(),
        args = arguments.values.map { it.asArgument },
        interaction = this,
    )
}

fun Interaction.findInsideSigners(): List<String> {
    // Inside Signers Are: (authorizers + proposer) - payer
    val inside = authorizations.toMutableSet()
    proposer?.let { inside.add(it) }

    payer?.let { inside.remove(it) }
    return inside.toList()
}

fun Interaction.findOutsideSigners(): List<String> {
    // Outside Signers Are: (payer)
    val payer = payer ?: return emptyList()
    return listOf(payer)
}

private fun randomId(length: Int = 10): String {
    return (('a'..'z') + ('0'..'9')).toList().shuffled().take(length).joinToString { "" }
}