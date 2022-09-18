package io.outblock.fcl.request

import com.nftco.flow.sdk.DomainTag
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.bytesToHex
import com.nftco.flow.sdk.hexToBytes
import io.outblock.fcl.Fcl
import io.outblock.fcl.cadence.CADENCE_VERIFY_ACCOUNT_PROOF
import io.outblock.fcl.config.Config
import io.outblock.fcl.models.FclResult
import io.outblock.fcl.models.response.FCLServiceType
import io.outblock.fcl.utils.FclError
import io.outblock.fcl.utils.FclException
import io.outblock.fcl.utils.addAddressPrefix
import io.outblock.fcl.utils.parse.parseFclResultBool
import io.outblock.fcl.utils.removeAddressPrefix
import org.tdf.rlp.RLP
import org.tdf.rlp.RLPCodec

private val accountProofTag = DomainTag.normalize("FCL-ACCOUNT-PROOF-V0.0")

internal class AccountProofRequest {

    fun request(includeDomainTag: Boolean = false): Boolean {
        Fcl.currentUser ?: throw FclException(FclError.unauthenticated)

        val service = Fcl.currentUser?.services?.first { it.type == FCLServiceType.accountProof.value }
            ?: throw FclException(FclError.invaildService)
        val appIdentifier = Fcl.config.get(Config.KEY.AppId) ?: throw  FclException(FclError.invaildService)
        val nonce = Fcl.config.get(Config.KEY.Nonce) ?: throw  FclException(FclError.invaildService)
        val address = service.data?.address ?: throw  FclException(FclError.invaildService)
        val signatures = service.data.signatures ?: throw  FclException(FclError.invaildService)

        val rpl = RLPCodec.encode(AccountProof(appIdentifier, address.removeAddressPrefix().hexToBytes(), nonce.hexToBytes()))

        val encoded = if (includeDomainTag) {
            accountProofTag + rpl
        } else rpl

        val result = Fcl.query {
            cadence(CADENCE_VERIFY_ACCOUNT_PROOF)

            arg { address(FlowAddress(address.addAddressPrefix())) }
            arg { string(encoded.bytesToHex()) }
            arg { array(signatures.map { int(it.keyId ?: -1) }) }
            arg { array(signatures.map { string(it.signature.orEmpty()) }) }
        }
        return when (result) {
            is FclResult.Success -> result.value.parseFclResultBool() ?: false
            else -> false
        }
    }

    companion object {
        private const val TAG = "AccountProofRequest"
    }
}

private class AccountProof(
    @RLP(0) val appIdentifier: String,
    @RLP(1) val address: ByteArray,
    @RLP(2) val nonce: ByteArray,
)
