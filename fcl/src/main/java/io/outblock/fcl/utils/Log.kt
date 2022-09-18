package io.outblock.fcl.utils

import android.util.Log
import io.outblock.fcl.BuildConfig

private val PRINT_LOG = BuildConfig.DEBUG

internal fun logv(tag: String?, msg: Any?) {
    log(tag, msg, Log.VERBOSE)
}

internal fun logd(tag: String?, msg: Any?) {
    log(tag, msg, Log.DEBUG)
}

internal fun logi(tag: String?, msg: Any?) {
    log(tag, msg, Log.INFO)
}

internal fun logw(tag: String?, msg: Any?) {
    log(tag, msg, Log.WARN)
}

internal fun loge(tag: String?, msg: Any?) {
    log(tag, msg, Log.ERROR)
}

internal fun loge(msg: Throwable?, printStackTrace: Boolean = true) {
    log("Exception", msg?.message ?: "", Log.ERROR)
    if (PRINT_LOG && printStackTrace) {
        msg?.printStackTrace()
    }
}

private fun log(tag: String?, msg: Any?, version: Int) {
    if (!PRINT_LOG) {
        return
    }

    val tag = "[${tag ?: ""}]"
    val msg = msg?.toString() ?: return

    when (version) {
        Log.VERBOSE -> Log.v(tag, msg)
        Log.DEBUG -> Log.d(tag, msg)
        Log.INFO -> Log.i(tag, msg)
        Log.WARN -> Log.w(tag, msg)
        Log.ERROR -> Log.e(tag, msg)
    }
}