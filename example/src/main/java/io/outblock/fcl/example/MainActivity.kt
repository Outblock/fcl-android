package io.outblock.fcl.example

import android.annotation.SuppressLint
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
import io.outblock.fcl.utils.ioScope
import io.outblock.fcl.utils.logd
import io.outblock.fcl.utils.uiScope

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        kotlin.runCatching {
            FCL.config(
                appName = "FCLDemo",
                appIcon = "https://placekitten.com/g/200/200",
                location = "https://foo.com",
                walletNode = "https://fcl-http-post.vercel.app/api",
                accessNode = "https://access-testnet.onflow.org",
                env = "mainnet",
                scope = "email",
                authn = WalletProvider.BLOCTO.endpoint.toString(),
            )
        }
        setupAuth()
        setupSendTransaction()
        logd("xxx", "${byteArrayOf(1, 2) + byteArrayOf(5, 6)}")
    }

    private fun setupAuth() {
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        with(tabLayout) {
            addTab(newTab().apply { text = "Dapper" })
            val blocto = newTab().apply { text = "Blocto" }
            addTab(blocto)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
            selectTab(blocto)
        }

        findViewById<View>(R.id.auth_button).setOnClickListener {
            val provider = if (tabLayout.selectedTabPosition == 0) WalletProvider.DAPPER else WalletProvider.BLOCTO
            ioScope {
                val auth = FCL.authenticate(provider)
                Log.d(TAG, "authenticate complete:$auth")
                uiScope {
                    Toast.makeText(this, "authenticate complete", Toast.LENGTH_SHORT).show()
                    findViewById<TextView>(R.id.address).text = auth.address
                }
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
                    arg { string("Test2") }
                    arg { int(1) }
                    gaslimit(1000)
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}