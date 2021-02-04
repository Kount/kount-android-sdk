package com.kount.checkoutexample.kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kount.api.analytics.AnalyticsCollector
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_LOCATION = 0
    private val ENVIRONMENT: Int = AnalyticsCollector.ENVIRONMENT_TEST//For production need to add AnalyticsCollector.ENVIRONMENT_PRODUCTION


    companion object Cons {
        val MERCHANT_ID = 999999 // Insert your valid merchant ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Sample"

        // Check for location permissions so the Data Collector can gather the device location
        requestLocationPermissions()

        AnalyticsCollector.setMerchantId(MERCHANT_ID)
        //This turns the alpha collections on(true)/off(false). It defaults to true
        AnalyticsCollector.collectAnalytics(true)
        AnalyticsCollector.setEnvironment(ENVIRONMENT)

        merchant.text = "$MERCHANT_ID"
        when (ENVIRONMENT) {
            AnalyticsCollector.ENVIRONMENT_TEST -> environment.text = "Test"
            AnalyticsCollector.ENVIRONMENT_PRODUCTION -> environment.text = "Production"

            else -> environment.text = "Unknown"
        }

        checkoutButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, CollectionActivity::class.java))
        }

    }

    private fun requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_LOCATION)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_LOCATION)
                }
            } else {
                location.text = "Allowed"
            }
        } else {
            location.text = "Allowed"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location.text = "Allowed"
            } else {
                location.text = "Denied"
            }
        }
    }
}