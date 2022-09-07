package io.outblock.fcl.cadence

val CADENCE_VERIFY_ACCOUNT_PROOF = """
  import FCLCrypto from 0xFCLCrypto
  pub fun main(
      address: Address,
      message: String,
      keyIndices: [Int],
      signatures: [String]
  ): Bool {
    return FCLCrypto.verifyAccountProofSignatures(address: address, message: message, keyIndices: keyIndices, signatures: signatures)
  }
""".trimIndent()


val CADENCE_VERIFY_USER_SIGNATURE = """
  import FCLCrypto from 0xFCLCrypto
  pub fun main(
      address: Address,
      message: String,
      keyIndices: [Int],
      signatures: [String]
  ): Bool {
    return FCLCrypto.verifyUserSignatures(address: address, message: message, keyIndices: keyIndices, signatures: signatures)
  }
""".trimIndent()