package io.outblock.fcl.models

sealed class FclResult<out T> {
    data class Success<out R>(val value: R) : FclResult<R>()
    data class Failure(val throwable: Throwable) : FclResult<Nothing>()
}