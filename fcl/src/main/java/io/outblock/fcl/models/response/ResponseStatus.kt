package io.outblock.fcl.models.response

enum class ResponseStatus(val value: String) {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    DECLINED("DECLINED")
}