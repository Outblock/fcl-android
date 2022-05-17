package io.outblock.fcl.response

enum class ResponseStatus(val value: String) {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    DECLINED("DECLINED")
}