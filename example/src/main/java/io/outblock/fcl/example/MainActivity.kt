package io.outblock.fcl.example

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import io.outblock.fcl.FCL
import io.outblock.fcl.provider.WalletProvider
import io.outblock.fcl.send.send
import io.outblock.fcl.utils.ioScope

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
        setupSendTransaction()
    }

    private fun setupAuth() {
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

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

        findViewById<View>(R.id.auth_button).setOnClickListener {
            val provider = if (tabLayout.selectedTabPosition == 0) WalletProvider.DAPPER else WalletProvider.BLOCTO
            FCL.authenticate(this, provider) {
                Log.d(TAG, "authenticate complete:$it")
                Toast.makeText(this, "authenticate complete", Toast.LENGTH_SHORT).show()
                findViewById<TextView>(R.id.address).text = it.address
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSendTransaction() {
        val editText = findViewById<EditText>(R.id.transaction_edittext)
        val txidView = findViewById<TextView>(R.id.txid_view)
        val viewOnFlowScanView = findViewById<View>(R.id.view_on_flow_scan)
        val button = findViewById<View>(R.id.button_send_transaction)
        editText.clearFocus()
        editText.setText(
            """
           transaction(test: String, testInt: Int) {
               prepare(signer: AuthAccount) {
                    log(signer.address)
                    log(test)
                    log(testInt)
               }
           }
        """.trimIndent()
        )

        button.setOnClickListener {
            ioScope {
                FCL.send {
                    script(editText.text.toString())
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}