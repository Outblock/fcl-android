package io.outblock.fcl.example

import android.app.Application
import io.outblock.fcl.Fcl
import io.outblock.fcl.FlowEnvironment
import io.outblock.fcl.FlowNetwork
import io.outblock.fcl.config.AppMetadata
import io.outblock.fcl.strategies.walletconnect.WalletConnectMeta

class FclApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        setupFcl()
    }

    private fun setupFcl() {
        val environment = FlowEnvironment(
            network = FlowNetwork.TESTNET,
            addressRegistry = listOf(
                Pair("0xFungibleToken", "0xf233dcee88fe0abe"),
                Pair("0xFUSD", "0x3c5959b568896393"),
            )
        )

        val appMetadata = AppMetadata(
            appName = "FCLDemo",
            appIcon = "https://placekitten.com/g/200/200",
            location = "https://foo.com",
            appId = "Awesome App (v0.0)",
            nonce = "75f8587e5bd5f9dcc9909d0dae1f0ac5814458b2ae129620502cb936fde7120a",
        )

        val walletConnectMeta = WalletConnectMeta(
            projectId = "29b38ec12be4bd19bf03d7ccef29aaa6",
            name = "FCL Android Wallet Connect Test",
            description = "Dapp description",
            url = "https://link.lilico.app",
            icon = "https://lilico.app/logo.png",
        )
        Fcl.config(
            appMetadata = appMetadata,
            env = environment,
            walletConnectMeta = walletConnectMeta
        )
    }
}