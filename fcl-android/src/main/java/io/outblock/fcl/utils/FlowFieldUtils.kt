package io.outblock.fcl.utils

import com.nftco.flow.sdk.cadence.*

internal fun createFlowField(type: String, value: String): Field<*>? {
    // TODO support all types
    return when (type) {
        TYPE_VOID -> VoidField()
//        TYPE_OPTIONAL -> OptionalField(value)
        TYPE_BOOLEAN -> BooleanField(value.toBoolean())
        TYPE_STRING -> StringField(value)
        TYPE_INT -> IntNumberField(value)
        TYPE_UINT -> UIntNumberField(value)
        TYPE_INT8 -> Int8NumberField(value)
        TYPE_UINT8 -> UInt8NumberField(value)
        TYPE_INT16 -> Int16NumberField(value)
        TYPE_UINT16 -> UInt16NumberField(value)
        TYPE_INT32 -> Int32NumberField(value)
        TYPE_UINT32 -> UInt32NumberField(value)
        TYPE_INT64 -> Int64NumberField(value)
        TYPE_UINT64 -> UInt64NumberField(value)
        TYPE_INT128 -> Int128NumberField(value)
        TYPE_UINT128 -> UInt128NumberField(value)
        TYPE_INT256 -> Int256NumberField(value)
        TYPE_UINT256 -> UInt256NumberField(value)
        TYPE_WORD8 -> Word8NumberField(value)
        TYPE_WORD16 -> Word16NumberField(value)
        TYPE_WORD32 -> Word32NumberField(value)
        TYPE_WORD64 -> Word64NumberField(value)
        TYPE_FIX64 -> Fix64NumberField(value)
        TYPE_UFIX64 -> UFix64NumberField(value)
//        TYPE_ARRAY -> ArrayField(value)
//        TYPE_DICTIONARY -> DictionaryField(value)
        TYPE_ADDRESS -> AddressField(value)
//        TYPE_PATH -> PathField(value)
//        TYPE_CAPABILITY -> CapabilityField(value)
//        TYPE_STRUCT -> StructField(value)
//        TYPE_RESOURCE -> ResourceField(value)
//        TYPE_EVENT -> EventField(value)
//        TYPE_CONTRACT -> ContractField(value)
//        TYPE_ENUM -> EnumField(value)
//        TYPE_TYPE -> TypeField(value)
        else -> null
    }
}