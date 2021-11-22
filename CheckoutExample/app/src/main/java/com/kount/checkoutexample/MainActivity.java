package com.kount.checkoutexample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kount.api.analytics.AnalyticsCollector;

import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    static final int MERCHANT_ID = 999999; // Insert your valid merchant ID
    static final int ENVIRONMENT = AnalyticsCollector.ENVIRONMENT_TEST;//For production need to add AnalyticsCollector.ENVIRONMENT_PRODUCTION
    static String deviceSessionID = "";
    private String KEY_UUID = "UUID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Sample");
        initView();

        //required section
        AnalyticsCollector.setMerchantId(MERCHANT_ID);
        //end required section

        // This turns the alpha collections on(true)/off(false). It defaults to true
        AnalyticsCollector.collectAnalytics(true);

        // For production you need to add AnalyticsCollector.ENVIRONMENT_PRODUCTION
        AnalyticsCollector.setEnvironment(ENVIRONMENT);

        //Optional SessionID section
        /** If you want to pass in a self generated sessionID(or one given to you by your servers)
         * you can set it using colde like this. Otherwise the AnalyticsCollector will generate one
         * for you.Make sure you set session id only one time in a user session.
         * To do so set your sessionID below.*/
        if (savedInstanceState == null) {
            deviceSessionID = UUID.randomUUID().toString();
            AnalyticsCollector.setSessionId(deviceSessionID);
        } else {
            deviceSessionID = savedInstanceState.getString(KEY_UUID);
            AnalyticsCollector.setSessionId(deviceSessionID);
        }
        // END OPTIONAL SESSION_ID SECTION

        //Request location permission for Android 6.0 & above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestLocationPermissions(this);
        else
            AnalyticsCollector.collectDeviceDataForSession(this);


    }

    private void initView() {
        final TextView merchant = (TextView) findViewById(R.id.merchant);
        final TextView environment = (TextView) findViewById(R.id.environment);
        final Button checkoutButton = (Button) findViewById(R.id.checkoutButton);

        merchant.setText(String.format(Locale.US, "%d", MERCHANT_ID));
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
    }

    void requestLocationPermissions(Activity activity) {
        final TextView location = (TextView) findViewById(R.id.location);

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AnalyticsCollector.REQUEST_PERMISSION_LOCATION);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AnalyticsCollector.REQUEST_PERMISSION_LOCATION);
            }
        } else {
            //This block executes when permission is already granted.
            AnalyticsCollector.collectDeviceDataForSession(activity);
            location.setText("Allowed");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        final TextView location = (TextView) findViewById(R.id.location);
        if (requestCode == AnalyticsCollector.REQUEST_PERMISSION_LOCATION) {
            //this block executes when a user grant/deny the permission
            AnalyticsCollector.collectDeviceDataForSession(this);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location.setText("Allowed");
            } else {
                location.setText("Denied");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_UUID, deviceSessionID);
    }

}