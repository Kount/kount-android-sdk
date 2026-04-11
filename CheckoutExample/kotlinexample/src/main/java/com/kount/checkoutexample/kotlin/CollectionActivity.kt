package com.kount.checkoutexample.kotlin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kount.api.KountSDK
import com.kount.api.internal.analytics.common.CollectionStatus

class CollectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        title = "Checkout Page"

        val textArea = findViewById<TextView>(R.id.textarea)
        val deviceSessionID = KountSDK.getSessionId()
        textArea.append("Session ID:\n$deviceSessionID\n\n")

        val status = KountSDK.getCollectionStatus()
        textArea.append("Collection Status: $status\n\n")
        if (status == CollectionStatus.FAILED.toString()) {
            textArea.append("Error: " + status.toString())
        }
    }
}