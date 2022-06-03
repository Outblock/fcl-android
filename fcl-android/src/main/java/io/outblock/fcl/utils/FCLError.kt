package io.outblock.fcl.utils

class FCLException(
    private val error: FCLError,
    private val exception: Exception? = null,
) : Exception(error.value) {


}

enum class FCLError(val value: String) {
    generic("generic"),
    invaildURL("invaildURL"),
    invaildService("invaildService"),
    invalidSession("invalidSession"),
    declined("declined"),
    invalidResponse("invalidResponse"),
    decodeFailure("decodeFailure"),
    unauthenticated("unauthenticated"),
    missingPreAuthz("missingPreAuthz"),
    missingPayer("missingPayer"),
    encodeFailure("encodeFailure"),
    convertToTxFailure("convertToTxFailure"),
    invaildProposer("invaildProposer"),
    fetchAccountFailure("fetchAccountFailure"),
}