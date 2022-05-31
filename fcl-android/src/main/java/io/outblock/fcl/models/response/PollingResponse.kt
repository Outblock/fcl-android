package io.outblock.fcl.models.response

import com.google.gson.annotations.SerializedName

data class PollingResponse(
    @SerializedName("status")
    val status: ResponseStatus,
    @SerializedName("data")
    val data: PollingData?,
    @SerializedName("updates")
    val updates: Service?,
    @SerializedName("local")
    val local: Service?,
    @SerializedName("reason")
    val reason: String?
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
}

data class PollingData(
    @SerializedName("addr")
    val addr: String,
    @SerializedName("services")
    val services: List<Service>?
)

data class Service(
    @SerializedName("type")
    val type: String?,
    @SerializedName("method")
    val method: String?,
    @SerializedName("endpoint")
    val endpoint: String?,
    @SerializedName("params")
    val params: Map<String, String>?,
)