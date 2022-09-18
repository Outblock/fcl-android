package io.outblock.fcl.example

import android.content.Context
import android.widget.Toast
import io.outblock.fcl.models.FclResult
import io.outblock.fcl.utils.FclException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun FclResult.Failure.toast(context: Context) {
    CoroutineScope(Dispatchers.Main).launch {
        val message = (throwable as? FclException)?.error?.toString() ?: throwable.message
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}