package io.outblock.fcl.example

import android.app.Application
import io.outblock.fcl.Fcl
import io.outblock.fcl.config.FlowNetwork
import io.outblock.fcl.strategies.walletconnect.WalletConnectMeta

class FclApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fcl.config(
            appName = "FCLDemo",
            appIcon = "https://placekitten.com/g/200/200",
            location = "https://foo.com",
            env = FlowNetwork.MAINNET,
            walletConnectMeta = WalletConnectMeta(
                projectId = "29b38ec12be4bd19bf03d7ccef29aaa6",
                name = "FCL Wallet Connect Test",
                description = "Dapp description",
                url = "https://lilico.app",
                icon = "https://lilico.app/logo.png",
                redirect = "https://google.com",
            )
        )
    }
}