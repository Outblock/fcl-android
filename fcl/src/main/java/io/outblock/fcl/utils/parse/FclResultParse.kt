package io.outblock.fcl.utils.parse

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.outblock.fcl.utils.loge

internal fun String.parseFclResultAddress(): String? {
    // {"type":"Optional","value":{"type":"Address","value":"0x5d2cd5bf303468fa"}}
    return try {
        val json = Gson().fromJson<Map<String, Any>>(this, object : TypeToken<Map<String, Any>>() {}.type)
        (json["value"] as Map<*, *>)["value"].toString()
    } catch (e: Exception) {
        loge(e)
        return null
    }
}

internal fun String.parseFclResultBool(default: Boolean = false): Boolean? {
    // {"type":"Bool","value":false}
    return try {
        val json = Gson().fromJson<Map<String, Any>>(this, object : TypeToken<Map<String, Any>>() {}.type)
        (json["value"] as? Boolean) ?: default
    } catch (e: Exception) {
        loge(e)
        return default
    }
}

internal fun String.parseFclResultBoolList(): List<Boolean>? {
    // {"type":"Array","value":[{"type":"Bool","value":true},{"type":"Bool","value":true},{"type":"Bool","value":true},{"type":"Bool","value":true},{"type":"Bool","value":false}]}
    return try {
        val result = Gson().fromJson(this, FlowBoolListResult::class.java)
        return result.value.map { it.value }
    } catch (e: Exception) {
        loge(e)
        null
    }
}

internal fun String?.parseFclResultFloat(default: Float = 0f): Float {
    // {"type":"UFix64","value":"12.34"}
    this ?: return default
    return try {
        val json = Gson().fromJson<Map<String, String>>(this, object : TypeToken<Map<String, String>>() {}.type)
        (json["value"]?.toFloatOrNull()) ?: default
    } catch (e: Exception) {
        loge(e)
        return default
    }
}

internal data class FlowBoolListResult(
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: List<Value>
) {
    data class Value(
        @SerializedName("type")
        val type: String,
        @SerializedName("value")
        val value: Boolean
    )
}