package com.kount.checkoutexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kount.api.DataCollector;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final int PERMISSIONS_REQUEST_LOCATION = 0;
    static final int MERCHANT_ID = 0; // Insert your valid merchant ID
    static final int ENVIRONMENT = DataCollector.ENVIRONMENT_TEST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Sample");

        // Check for location permissions so the Data Collector can gather the device location
        requestLocationPermissions();

        final TextView merchant = (TextView) findViewById(R.id.merchant);
        final TextView environment = (TextView) findViewById(R.id.environment);
        final Button checkoutButton = (Button) findViewById(R.id.checkoutButton);

        merchant.setText(String.format(Locale.US, "%d", MERCHANT_ID));
        switch (ENVIRONMENT) {
            case DataCollector.ENVIRONMENT_TEST:
                environment.setText("Test");
                break;
            case DataCollector.ENVIRONMENT_PRODUCTION:
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

    void requestLocationPermissions() {
        final TextView location = (TextView) findViewById(R.id.location);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                }
            } else {
                location.setText("Allowed");
            }
        } else {
            // The permissions are allowed by default if installed on a device with a OS less than M
            location.setText("Allowed");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        final TextView location = (TextView) findViewById(R.id.location);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location.setText("Allowed");
            } else {
                location.setText("Denied");
            }
        }
    }

}
