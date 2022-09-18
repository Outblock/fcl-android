package io.outblock.fcl.utils

import android.os.Looper
import androidx.annotation.WorkerThread
import com.nftco.flow.sdk.FlowBlock
import com.nftco.flow.sdk.bytesToHex
import io.outblock.fcl.Fcl
import io.outblock.fcl.FlowApi
import io.outblock.fcl.cadence.CADENCE_VERIFY_USER_SIGNATURE
import io.outblock.fcl.models.FclResult
import io.outblock.fcl.request.SignMessageResponse
import io.outblock.fcl.utils.parse.parseFclResultBool


@WorkerThread
fun Fcl.getLatestBlock(sealed: Boolean = true): FlowBlock {
    return FlowApi.get().getLatestBlock()
}

@WorkerThread
fun Fcl.verifyUserSignature(message: String, signatures: List<SignMessageResponse>): Boolean {
    assert(Thread.currentThread() != Looper.getMainLooper().thread) { "can't call this method in main thread." }
    currentUser ?: throw FclException(FclError.unauthenticated)

    val result = query {
        cadence(CADENCE_VERIFY_USER_SIGNATURE)
        arg { address(signatures.firstOrNull()?.address.orEmpty()) }
        arg { string(message.toByteArray().bytesToHex()) }
        arg { array { signatures.map { int(it.keyId ?: 0) } } }
        arg { array { signatures.map { string(it.signature.orEmpty()) } } }
    }

    return when (result) {
        is FclResult.Success -> result.value.parseFclResultBool() ?: false
        else -> false
    }
}
