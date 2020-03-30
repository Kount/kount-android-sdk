package com.kount.checkoutexample.kotlin

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.kount.api.DataCollector
import kotlinx.android.synthetic.main.activity_collection.*
import java.util.*

class CollectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        title = "Checkout Page"

        textarea.append("Collection Starting\n\n")
        val sessionID = UUID.randomUUID().toString()
        val deviceSessionID = sessionID.replace("-", "")
        textarea.append("Session ID:\n$sessionID\n\n")

        val dataCollector: DataCollector = DataCollector.getInstance()
        dataCollector.setDebug(true)
        dataCollector.setContext(this)
        dataCollector.setMerchantID(MainActivity.MERCHANT_ID)
        dataCollector.setEnvironment(DataCollector.ENVIRONMENT_TEST)
        dataCollector.setLocationCollectorConfig(DataCollector.LocationConfig.COLLECT)

        Handler().post {
            dataCollector.collectForSession(deviceSessionID, object : DataCollector.CompletionHandler {
                override fun completed(s: String?) {
                    textarea.append("Collection Completed")
                }

                override fun failed(s: String?, error: DataCollector.Error?) {
                    textarea.append("Collection Failed:\n\n " + (error?.description ?: "none"))
                }

            })
        }
    }
}