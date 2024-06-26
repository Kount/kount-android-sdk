package com.kount.checkoutexample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kount.api.analytics.AnalyticsCollector;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    final int PERMISSIONS_REQUEST_LOCATION = 0;
    static final String MERCHANT_ID = "999999"; // Insert your valid merchant ID
    static final int ENVIRONMENT = AnalyticsCollector.ENVIRONMENT_TEST;//For production need to add AnalyticsCollector.ENVIRONMENT_PRODUCTION
    TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        setTitle("Sample");
        location = (TextView) findViewById(R.id.location);

        AnalyticsCollector.setMerchantId(MERCHANT_ID);
        // This turns the alpha collections on(true)/off(false). It defaults to true
        AnalyticsCollector.collectAnalytics(true);
        AnalyticsCollector.setEnvironment(ENVIRONMENT);

        final TextView merchant = (TextView) findViewById(R.id.merchant);
        final TextView environment = (TextView) findViewById(R.id.environment);
        final Button checkoutButton = (Button) findViewById(R.id.checkoutButton);

        merchant.setText(MERCHANT_ID);
        switch (ENVIRONMENT) {
            case AnalyticsCollector.ENVIRONMENT_TEST:
                environment.setText("Test");
                break;
            case AnalyticsCollector.ENVIRONMENT_PRODUCTION:
                environment.setText("Production");
                break;
            default:
                environment.setText("Unknown");
        }

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                startActivity(intent);
            }
        });
        Log.d("TAG", "DDC status is " +AnalyticsCollector.getCollectionStatus());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check for location permissions so the Data Collector can gather the device location
            requestLocationPermission(this);
        } else {
            location.setText("Allowed");

            //Calling this will start standard DeviceData Collection
            AnalyticsCollector.collectDeviceDataForSession(this,(sessionId)->
            {
                Log.d("TAG", "Client success completed with sessionId "+sessionId);
                Log.d("TAG", "DDC status is " +AnalyticsCollector.getCollectionStatus());
                return null;
            },(sessionId, error)->
            {
                Log.d("TAG", "client failed with sessionId $error, $sessionId");
                Log.d("TAG", "DDC status is " +AnalyticsCollector.getCollectionStatus());
                return null;
            });
        }
        super.onCreate(savedInstanceState);
    }

    private void requestLocationPermission(Activity activity) {
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
                        new String[]{(Manifest.permission.ACCESS_FINE_LOCATION)},
                        AnalyticsCollector.REQUEST_PERMISSION_LOCATION
                );
            } else {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{(Manifest.permission.ACCESS_FINE_LOCATION)},
                        AnalyticsCollector.REQUEST_PERMISSION_LOCATION
                );
            }
        } else {
            location.setText("Allowed");
            AnalyticsCollector.collectDeviceDataForSession(activity,(sessionId)->
            {
                Log.d("TAG", "Util success completed with sessionId "+sessionId);
                Log.d("TAG", "DDC status is " +AnalyticsCollector.getCollectionStatus());
                return null;
            },(sessionId, error)->
            {
                Log.d("TAG", "Util failed with sessionId $error, $sessionId");
                Log.d("TAG", "DDC status is " +AnalyticsCollector.getCollectionStatus());
                return null;
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        final TextView location = (TextView) findViewById(R.id.location);
        if (requestCode == AnalyticsCollector.REQUEST_PERMISSION_LOCATION) {
            AnalyticsCollector.collectDeviceDataForSession(this,(sessionId)->
            {
                Log.d("TAG", "onrequest success completed with sessionId "+sessionId);
                Log.d("TAG", "DDC status is " +AnalyticsCollector.getCollectionStatus());
                return null;
            },(sessionId, error)->
            {
                Log.d("TAG", "onrequest failed with sessionId $error, $sessionId");
                Log.d("TAG", "DDC status is " +AnalyticsCollector.getCollectionStatus());
                return null;
            });
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location.setText("Allowed");
            } else {
                location.setText("Denied");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}