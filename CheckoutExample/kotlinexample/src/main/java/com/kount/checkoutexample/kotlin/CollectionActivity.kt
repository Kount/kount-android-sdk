package com.kount.checkoutexample.kotlin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kount.api.analytics.AnalyticsCollector
import com.kount.api.analytics.DeviceDataCollector

class CollectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        title = "Checkout Page"

        val textArea = findViewById<TextView>(R.id.textarea)
        val deviceSessionID = AnalyticsCollector.getSessionId()
        textArea.append("Session ID:\n$deviceSessionID\n\n")

        val status = AnalyticsCollector.getCollectionStatus()
        textArea.append("Collection Status: $status\n\n")
        if (status == DeviceDataCollector.CollectionStatus.FAILED) {
            textArea.append("Error: " + status.getError()?.description)
        }
    }
}