package io.outblock.fcl.request

import com.google.gson.annotations.SerializedName
import com.nftco.flow.sdk.bytesToHex
import io.outblock.fcl.Fcl
import io.outblock.fcl.models.response.FCLServiceType
import io.outblock.fcl.strategies.execHttpPost
import io.outblock.fcl.utils.FclError
import io.outblock.fcl.utils.FclException

internal class SignMessageSend {

    /**
     * TODO not support right now
     */
    suspend fun sign(message: String): String {

        val service = Fcl.currentUser?.services?.first { it.type == FCLServiceType.userSignature.value }
            ?: throw FclException(FclError.invaildService)

        val endpoint = service.endpoint ?: throw FclException(FclError.invaildService)

        val signable = SignableMessage(message.toByteArray().bytesToHex())

        val response = execHttpPost(endpoint, service.params, signable)

        return ""
    }

    companion object {
        private const val TAG = "FCLAuthn"
    }
}

private class SignableMessage(
    @SerializedName("message")
    val message: String,
)