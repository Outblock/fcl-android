package io.outblock.fcl.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import io.outblock.fcl.FCL
import io.outblock.fcl.provider.WalletProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FCL.config(
            appName = "Test",
            appIcon = "https://lilico.app/logo.svg",
            location = "https://lilico.app",
            walletNode = "https://fcl-http-post.vercel.app/api",
            accessNode = "https://access-testnet.onflow.org",
            env = "mainnet",
            scope = "email",
            authn = WalletProvider.BLOCTO.endpoint.toString(),
        )
        setupAuth()
    }

    private fun setupAuth() {
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        findViewById<View>(R.id.auth_button).setOnClickListener {
            val provider = if (tabLayout.selectedTabPosition == 0) WalletProvider.DAPPER else WalletProvider.BLOCTO
            FCL.authenticate(this, provider) {
                Log.d(TAG, "authenticate complete:$it")
                Toast.makeText(this, "authenticate complete", Toast.LENGTH_SHORT).show()
                findViewById<TextView>(R.id.address).text = it.address
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        with(tabLayout) {
            addTab(newTab().apply { text = "Dapper" })
            addTab(newTab().apply { text = "Blocto" })
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}