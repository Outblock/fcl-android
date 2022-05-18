package io.outblock.fcl.utils

import io.outblock.fcl.BuildConfig
import kotlinx.coroutines.*

fun ioScope(unit: suspend () -> Unit) = CoroutineScope(Dispatchers.IO).launch { execute(unit) }

suspend fun contextScope(unit: suspend () -> Unit) = withContext(Dispatchers.Main) { execute(unit) }

fun uiScope(unit: suspend () -> Unit) = CoroutineScope(Dispatchers.Main).launch { execute(unit) }

fun uiDelay(delayMs: Long, unit: suspend () -> Unit) = CoroutineScope(Dispatchers.Main).launch {
    delay(delayMs)
    execute(unit)
}

fun cpuScope(unit: suspend () -> Unit) = CoroutineScope(Dispatchers.Default).launch { execute(unit) }

suspend fun repeatWhen(
    predicate: suspend () -> Boolean,
    block: suspend () -> Unit,
) {
    while (predicate()) {
        block.invoke()
    }
}

suspend fun runBlockDelay(timeMillis: Long, block: suspend () -> Unit) {
    val startTime = System.currentTimeMillis()
    block.invoke()
    val elapsedTime = System.currentTimeMillis() - startTime
    delay(timeMillis - elapsedTime)
}

private suspend fun execute(unit: suspend () -> Unit) {
    if (BuildConfig.DEBUG) {
        try {
            unit.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        unit.invoke()
    }
}