package io.outblock.fcl.strategies.walletconnect

import android.content.Intent
import android.net.Uri
import io.outblock.fcl.lifecycle.LifecycleObserver
import io.outblock.fcl.utils.FclError
import io.outblock.fcl.utils.FclException
import java.net.URLEncoder

fun launchDeeplink(uri: String) {
    val context = LifecycleObserver.context() ?: throw FclException(FclError.invaildContext)
    val host = WalletConnect.get().meta()?.url ?: throw FclException(FclError.invaildURL)
    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("${host.removeSuffix("/")}?uri=${URLEncoder.encode(uri, "UTF-8")}")
    })
}