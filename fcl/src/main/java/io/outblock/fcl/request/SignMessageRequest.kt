package io.outblock.fcl.request

import com.google.gson.annotations.SerializedName
import com.nftco.flow.sdk.bytesToHex
import io.outblock.fcl.Fcl
import io.outblock.fcl.models.response.FCLServiceType
import io.outblock.fcl.strategies.executeStrategies
import io.outblock.fcl.utils.FclError
import io.outblock.fcl.utils.FclException

internal class SignMessageRequest {

    suspend fun request(message: String): SignMessageResponse {
        Fcl.currentUser ?: throw FclException(FclError.unauthenticated)
        val service = Fcl.currentUser?.services?.first { it.type == FCLServiceType.userSignature.value }
            ?: throw FclException(FclError.invaildService)

        val signable = SignableMessage(message.toByteArray().bytesToHex())

        val response = service.executeStrategies(signable)

        val data = response.data ?: throw FclException(FclError.invalidResponse)

        return SignMessageResponse(
            address = data.address,
            signature = data.signature,
            keyId = data.keyId,
        )
    }

    companion object {
        private const val TAG = "SignMessageSend"
    }
}

class SignMessageResponse(
    @SerializedName("addr")
    val address: String?,
    @SerializedName("signature")
    val signature: String?,
    @SerializedName("keyId")
    val keyId: Int?,
)

private class SignableMessage(
    @SerializedName("message")
    val message: String,
)