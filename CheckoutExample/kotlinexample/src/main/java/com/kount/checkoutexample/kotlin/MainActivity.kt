package com.kount.checkoutexample.kotlin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kount.api.DataCollector
import com.kount.api.analytics.AnalyticsCollector
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private val PERMISSIONS_REQUEST_LOCATION = 0
    private val ENVIRONMENT: Int =
        AnalyticsCollector.ENVIRONMENT_TEST//For production need to add AnalyticsCollector.ENVIRONMENT_PRODUCTION


    companion object Cons {
        val MERCHANT_ID = 999999 // Insert your valid merchant ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        title = "Sample"


        AnalyticsCollector.setMerchantId(MERCHANT_ID)
        //This turns the alpha collections on(true)/off(false). It defaults to true
        AnalyticsCollector.collectAnalytics(true)
        AnalyticsCollector.setEnvironment(ENVIRONMENT)
        DataCollector.getInstance().setDebug(true)
        merchant.text = "$MERCHANT_ID"
        when (ENVIRONMENT) {
            AnalyticsCollector.ENVIRONMENT_TEST -> environment.text = "Test"
            AnalyticsCollector.ENVIRONMENT_PRODUCTION -> environment.text = "Production"

            else -> environment.text = "Unknown"
        }

        checkoutButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, CollectionActivity::class.java))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        // Check for location permissions so the Data Collector can gather the device location
            requestLocationPermission(this) else {
            location.text = "Allowed"

            //Calling this will start standard DeviceData Collection
            AnalyticsCollector.collectDeviceDataForSession(this)
        }
        super.onCreate(savedInstanceState)

    }

    private fun requestLocationPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    AnalyticsCollector.REQUEST_PERMISSION_LOCATION
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    AnalyticsCollector.REQUEST_PERMISSION_LOCATION
                )
            }
        } else {
            location.text = "Allowed"
            AnalyticsCollector.collectDeviceDataForSession(activity)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == AnalyticsCollector.REQUEST_PERMISSION_LOCATION) {
            AnalyticsCollector.collectDeviceDataForSession(this)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location.text = "Allowed"
            } else {
                location.text = "Denied"
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}