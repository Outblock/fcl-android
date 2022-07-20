package io.outblock.fcl.example

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import io.outblock.fcl.Fcl
import io.outblock.fcl.config.FlowNetwork
import io.outblock.fcl.provider.WalletProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Fcl.config(
            appName = "FCLDemo",
            appIcon = "https://placekitten.com/g/200/200",
            location = "https://foo.com",
            env = FlowNetwork.MAINNET,
        )
        setupAuth()
        setupSendTransaction()
        setupQuery()
        setupSignMessage()
    }

    @SuppressLint("SetTextI18n")
    private fun setupQuery() {
        val edittext = findViewById<EditText>(R.id.query_cadence_edittext)
        edittext.setText(
            """
            pub fun main(a: Int, b: Int, addr: Address): Int {
              log(addr)
              return a + b
            }
        """.trimIndent()
        )
        findViewById<View>(R.id.button_query).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val cadence = edittext.text.toString()
                val result = Fcl.query {
                    cadence(cadence)
                    arg { int(7) }
                    arg { int(3) }
                    arg { address("0xba1132bc08f82fe2") }
                }
                CoroutineScope(Dispatchers.Main).launch { findViewById<TextView>(R.id.query_result_view).text = result }
            }
        }
    }

    private fun setupSignMessage() {
        findViewById<View>(R.id.button_sign_message).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val message = findViewById<EditText>(R.id.sign_message_edittext).text.toString()
                val signature = Fcl.signMessage(message)
                CoroutineScope(Dispatchers.Main).launch { findViewById<TextView>(R.id.signed_message_view).text = signature }
            }
        }
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
            CoroutineScope(Dispatchers.IO).launch {
                val auth = Fcl.authenticate(provider)
                Log.d(TAG, "authenticate complete:$auth")
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@MainActivity, "authenticate complete", Toast.LENGTH_SHORT).show()
                    findViewById<TextView>(R.id.address).text = auth.data?.address
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupSendTransaction() {
        val editText = findViewById<EditText>(R.id.transaction_edittext)
        val txidView = findViewById<TextView>(R.id.txid_view)
        val txidLayout = findViewById<View>(R.id.txid_wrapper)
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
            CoroutineScope(Dispatchers.IO).launch {
                val tid = Fcl.mutate {
                    cadence(editText.text.toString())
                    arg { string("Test2") }
                    arg { int(1) }
                    gasLimit(1000)
                }
                Log.d(TAG, "tid:$tid")
                CoroutineScope(Dispatchers.Main).launch {
                    txidView.text = tid
                    txidLayout.visibility = View.VISIBLE
                    viewOnFlowScanView.setOnClickListener {
                        "https://${if (Fcl.isMainnet()) "" else "testnet."}flowscan.org/transaction/$tid".openInSystemBrowser(
                            this@MainActivity
                        )
                    }
                }
            }
        }
    }

    private fun String.openInSystemBrowser(context: Context) {
        ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(this)), null)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}