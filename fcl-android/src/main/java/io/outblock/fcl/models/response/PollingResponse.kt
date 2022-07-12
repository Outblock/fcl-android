package io.outblock.fcl.models.response

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class PollingResponse(
    @SerializedName("status")
    val status: ResponseStatus,
    @SerializedName("data")
    val data: PollingData?,
    @SerializedName("updates")
    val updates: Service?,
    @SerializedName("local")
    val local: Any?,
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("compositeSignature")
    val compositeSignature: PollingData?,
    @SerializedName("authorizationUpdates")
    var authorizationUpdates: Service?,
) {
    fun isPending(): Boolean {
        return status == ResponseStatus.PENDING
    }

    fun isApproved(): Boolean {
        return status == ResponseStatus.APPROVED
    }

    fun isDeclined(): Boolean {
        return status == ResponseStatus.DECLINED
    }

    fun local(): Service? {
        local ?: return null
        val json = Gson().toJson(local)

        return try {
            if (local is ArrayList<*>) {
                Gson().fromJson<List<Service>>(json, object : TypeToken<List<Service>>() {}.type).first()
            } else {
                Gson().fromJson(json, Service::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }
}

data class PollingData(
    @SerializedName("addr")
    val addr: String?,
    @SerializedName("services")
    val services: List<Service>?,
    @SerializedName("f_type")
    val fType: String?,
    @SerializedName("f_vsn")
    val fVsn: String?,
    @SerializedName("proposer")
    val proposer: Service?,
    @SerializedName("payer")
    val payer: List<Service>?,
    @SerializedName("authorization")
    val authorization: List<Service>?,
    @SerializedName("signature")
    val signature: String?,
)

data class Service(
    @SerializedName("f_type")
    val fType: String?,
    @SerializedName("f_vsn")
    val fVsn: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("method")
    val method: String?,
    @SerializedName("endpoint")
    val endpoint: String?,
    @SerializedName("params")
    val params: Map<String, String>?,
    @SerializedName("uid")
    val uid: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("identity")
    val identity: Identity?,
    @SerializedName("provider")
    val provider: Provider?,
)

data class LocalService(
    @SerializedName("method")
    var method: String? = null,
    @SerializedName("endpoint")
    var endpoint: String? = null,
    @SerializedName("height")
    var height: String? = null,
    @SerializedName("width")
    var width: String? = null,
    @SerializedName("background")
    var background: String? = null
)

enum class FCLServiceType(val value: String) {
    authn("authn"),
    authz("authz"),
    preAuthz("pre-authz"),
    userSignature("user-signature"),
    backChannel("back-channel-rpc"),
    localView("local-view"),
    openID("open-id"),
}

enum class FCLServiceMethod(val value: String) {
    httpPost("HTTP/POST"),
    httpGet("HTTP/GET"),
    iframe("VIEW/IFRAME"),
    iframeRPC("IFRAME/RPC"),
    dataa("DATA"),
}

class Identity(
    @SerializedName("address")
    val address: String,
    @SerializedName("keyId")
    val keyId: Int?,
)

class Provider(
    @SerializedName("f_type")
    val fType: String?,
    @SerializedName("f_vsn")
    val fVsn: String?,
    @SerializedName("address")
    val address: String,
    @SerializedName("name")
    val name: String,
)