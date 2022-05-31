package io.outblock.fcl.models


import com.google.gson.annotations.SerializedName
import com.nftco.flow.sdk.cadence.Field

data class Signable(
    @SerializedName("addr")
    val addr: String,
    @SerializedName("app")
    val app: App,
    @SerializedName("args")
    val args: List<Arg>,
    @SerializedName("cadence")
    val cadence: String,
    @SerializedName("client")
    val client: Client,
    @SerializedName("data")
    val data: Data,
    @SerializedName("f_type")
    val fType: String,
    @SerializedName("f_vsn")
    val fVsn: String,
    @SerializedName("interaction")
    val interaction: Interaction,
    @SerializedName("keyId")
    val keyId: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("roles")
    val roles: Roles,
    @SerializedName("service")
    val service: Service,
    @SerializedName("voucher")
    val voucher: Voucher
) {
}

data class App(
    @SerializedName("icon")
    val icon: String,
    @SerializedName("title")
    val title: String
)

data class Arg(
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: String
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
    var authorizations: List<String> = listOf(),
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
    val addr: String,
    @SerializedName("keyId")
    val keyId: Int,
    @SerializedName("kind")
    val kind: Any,
    @SerializedName("role")
    val role: Roles,
    @SerializedName("sequenceNum")
    val sequenceNum: Any,
    @SerializedName("signature")
    val signature: Any,
    @SerializedName("tempId")
    val tempId: String
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
    val authorizer: Boolean,
    @SerializedName("payer")
    val payer: Boolean,
    @SerializedName("proposer")
    val proposer: Boolean
)

class Service

data class Voucher(
    @SerializedName("arguments")
    val arguments: List<AsArgument>,
    @SerializedName("authorizers")
    val authorizers: List<String>,
    @SerializedName("cadence")
    val cadence: String,
    @SerializedName("computeLimit")
    val computeLimit: Int,
    @SerializedName("envelopeSigs")
    val envelopeSigs: List<EnvelopeSig>,
    @SerializedName("payer")
    val payer: String,
    @SerializedName("payloadSigs")
    val payloadSigs: List<PayloadSig>,
    @SerializedName("proposalKey")
    val proposalKey: ProposalKey,
    @SerializedName("refBlock")
    val refBlock: String
)

data class EnvelopeSig(
    @SerializedName("address")
    val address: String,
    @SerializedName("keyId")
    val keyId: Int
)

data class PayloadSig(
    @SerializedName("address")
    val address: String,
    @SerializedName("keyId")
    val keyId: Int,
    @SerializedName("sig")
    val sig: String
)

data class ProposalKey(
    @SerializedName("address")
    val address: String,
    @SerializedName("keyId")
    val keyId: Int,
    @SerializedName("sequenceNum")
    val sequenceNum: Int
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

private fun randomId(length: Int = 10): String {
    return (('a'..'z') + ('0'..'9')).toList().shuffled().take(length).joinToString { "" }
}