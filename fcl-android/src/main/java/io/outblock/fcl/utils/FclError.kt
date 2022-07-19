package io.outblock.fcl.utils

import io.outblock.fcl.BuildConfig

class FCLException(
    private val error: FclError,
    private val exception: Exception? = null,
) : Exception(error.value) {

    init {
        if (BuildConfig.DEBUG) {
            printStackTrace()
        }
    }

}

enum class FclError(val value: String) {
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
    invaildContext("invaildContext"),
}