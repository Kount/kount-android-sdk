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
import com.kount.api.analytics.AnalyticsCollector
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private val ENVIRONMENT: Int = AnalyticsCollector.ENVIRONMENT_TEST//For production need to add AnalyticsCollector.ENVIRONMENT_PRODUCTION
    var deviceSessionID: String? = ""
    private val KEY_UUID = "UUID"

    companion object Cons {
        val MERCHANT_ID = 999999 // Insert your valid merchant ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Sample"
        initView()

        //required section
        AnalyticsCollector.setMerchantId(MERCHANT_ID)
        //end required section

        // This turns the alpha collections on(true)/off(false). It defaults to true
        AnalyticsCollector.collectAnalytics(true)

        // For production you need to add AnalyticsCollector.ENVIRONMENT_PRODUCTION
        AnalyticsCollector.setEnvironment(ENVIRONMENT)

        //Optional SessionID section
        /** If you want to pass in a self generated sessionID(or one given to you by your servers)
         * you can set it using colde like this. Otherwise the AnalyticsCollector will generate one
         * for you.Make sure you set session id only one time in a user session.
         * To do so set your sessionID below. */
        if (savedInstanceState == null) {
            deviceSessionID = UUID.randomUUID().toString()
            AnalyticsCollector.setSessionId(deviceSessionID!!)
        } else {
            deviceSessionID = savedInstanceState.getString(KEY_UUID)
            AnalyticsCollector.setSessionId(deviceSessionID!!)
        }
        // END OPTIONAL SESSION_ID SECTION

        //Request location permission for Android 6.0 & above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestLocationPermissions(this)
        else
            AnalyticsCollector.collectDeviceDataForSession(this)
    }

    private fun initView() {
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

    fun requestLocationPermissions(activity: Activity?) {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION), AnalyticsCollector.REQUEST_PERMISSION_LOCATION)
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION), AnalyticsCollector.REQUEST_PERMISSION_LOCATION)
            }
        } else {
            //This block executes when permission is already granted.
            AnalyticsCollector.collectDeviceDataForSession(activity)
            location.text = "Allowed"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == AnalyticsCollector.REQUEST_PERMISSION_LOCATION) {
            //this block executes when a user grant/deny the permission
            AnalyticsCollector.collectDeviceDataForSession(this)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location.text = "Allowed"
            } else {
                location.text = "Denied"
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_UUID, deviceSessionID)
    }
}