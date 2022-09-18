package io.outblock.fcl.utils

class FclException(
    val error: FclError,
    val exception: Throwable? = null,
) : Exception(error.value) {

    init {
        exception?.printStackTrace()
    }
}

enum class FclError(val value: String) {
    declined("declined"),
    invaildURL("invaildURL"),
    generic("generic"),
    invaildService("invaildService"),
    invalidSession("invalidSession"),
    invalidResponse("invalidResponse"),
    decodeFailure("decodeFailure"),
    unauthenticated("unauthenticated"),
    missingPreAuthz("missingPreAuthz"),
    missingPayer("missingPayer"),
    encodeFailure("encodeFailure"),
    convertToTxFailure("convertToTxFailure"),
    invaildProposer("invaildProposer"),
    fetchAccountFailure("fetchAccountFailure"),
    invaildContext("invaildContext"),
}