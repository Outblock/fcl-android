package io.outblock.fcl.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nftco.flow.sdk.FlowScriptResponse
import java.lang.reflect.Type


fun FlowScriptResponse.toMap(): Map<String, Any>? {
    return String(bytes).fromJson<Map<String, Any>>(object : TypeToken<Map<String, Any>>() {}.type)
}

fun <T> String.fromJson(clz: Class<T>): T? {
    return try {
        Gson().fromJson(this, clz)
    } catch (e: Exception) {
        loge(e)
        null
    }
}

fun <T> String.fromJson(typeOfT: Type): T? {
    return try {
        Gson().fromJson<T>(this, typeOfT)
    } catch (e: Exception) {
        loge(e)
        null
    }
}