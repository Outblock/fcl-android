package io.outblock.fcl.response

data class PollingResponse(
    val status: ResponseStatus,
    val data: PollingData?,
    val updates: Service?,
    val local: Service?,
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
    val addr: String,
    val services: Array<Service>?
)

data class Service(
    val type: String?,
    val method: String?,
    val endpoint: String,
    var params: Map<String, String>
)