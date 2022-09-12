package io.outblock.fcl.utils

import io.outblock.fcl.lifecycle.LifecycleObserver


fun bringToForeground() {
    uiScope {
        logd("xxx","bringToForeground")
        val context = LifecycleObserver.context() ?: return@uiScope
        logd("xxx","bringToForeground context:$context")
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        logd("xxx","bringToForeground launchIntent:$launchIntent")
        context.startActivity(launchIntent)
    }
}