package io.outblock.fcl.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.walletconnect.android.Core
import com.walletconnect.android.relay.RelayClient
import io.outblock.fcl.utils.logw

class AppLifecycleObserver : DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        onAppToForeground()
    }

    override fun onStop(owner: LifecycleOwner) {
        onAppToBackground()
    }

    private fun onAppToForeground() {
        kotlin.runCatching {
            RelayClient.connect { error: Core.Model.Error ->
                logw("RelayClient", "RelayClient connect error: $error")
            }
        }
    }

    private fun onAppToBackground() {
    }

    companion object {

        fun observe() {
            ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
        }
    }
}