package com.kount.checkoutexample.kotlin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kount.api.KountSDK

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private val PERMISSIONS_REQUEST_LOCATION = 0
    private val ENVIRONMENT: Int =
        KountSDK.ENVIRONMENT_TEST//For production need to add AnalyticsCollector.ENVIRONMENT_PRODUCTION

    private lateinit var  merchant: TextView
    private lateinit var  environment: TextView
    private lateinit var  checkoutButton: Button
    private lateinit var  location: TextView

    companion object Cons {
        val MERCHANT_ID = "999999" // Insert your valid merchant ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        title = "Sample"

        merchant=findViewById(R.id.merchant)
        environment=findViewById(R.id.environment)
        checkoutButton=findViewById(R.id.checkoutButton)
        location=findViewById(R.id.location)


        KountSDK.setMerchantId(MERCHANT_ID)
        //This turns the alpha collections on(true)/off(false). It defaults to true
        KountSDK.setCollectAnalytics(true)
        KountSDK.setEnvironment(ENVIRONMENT)

        merchant.text = MERCHANT_ID
        when (ENVIRONMENT) {
            KountSDK.ENVIRONMENT_TEST -> environment.text = "Test"
            KountSDK.ENVIRONMENT_PRODUCTION -> environment.text = "Production"

            else -> environment.text = "Unknown"
        }

        checkoutButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, CollectionActivity::class.java))
        }
        Log.d("TAG", "DDC status is " +KountSDK.getCollectionStatus())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        // Check for location permissions so the Data Collector can gather the device location
            requestLocationPermission(this) else {
            location.text = "Allowed"

            //Calling this will start standard DeviceData Collection
            KountSDK.collectForSession(this,{sessionId ->
                Log.d("TAG", "Client success completed with sessionId $sessionId")
                Log.d("TAG", "DDC status is " +KountSDK.getCollectionStatus())

            },{ sessionId, error ->
                Log.d("TAG", "client failed with sessionId $error, $sessionId")
                Log.d("TAG", "DDC status is " +KountSDK.getCollectionStatus())
            })
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
                    KountSDK.REQUEST_PERMISSION_LOCATION
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    KountSDK.REQUEST_PERMISSION_LOCATION
                )
            }
        } else {
            location.text = "Allowed"
            KountSDK.collectForSession(activity,{sessionId ->
                Log.d("TAG", "utils success completed with sessionId $sessionId")
                Log.d("TAG", "DDC status is " +KountSDK.getCollectionStatus())
            },{ sessionId, error ->
                Log.d("TAG", "utils failed with sessionId $error, $sessionId")
                Log.d("TAG", "DDC status is " +KountSDK.getCollectionStatus())
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == KountSDK.REQUEST_PERMISSION_LOCATION) {
            KountSDK.collectForSession(this,{sessionId ->
                Log.d("TAG", "onrequest success completed with sessionId $sessionId")
                Log.d("TAG", "DDC status is " +KountSDK.getCollectionStatus())
            },{ sessionId, error ->
                Log.d("TAG", " onrequest failed with sessionId $error, $sessionId")
                Log.d("TAG", "DDC status is " +KountSDK.getCollectionStatus())
            })
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location.text = "Allowed"
            } else {
                location.text = "Denied"
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}