package com.kount.checkoutexample;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kount.api.analytics.AnalyticsCollector;
import com.kount.api.analytics.DeviceDataCollector;

public class CollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        setTitle("Checkout Page");

        final TextView textArea = findViewById(R.id.textarea);
        final String deviceSessionID = AnalyticsCollector.getSessionId();
        textArea.append("Session ID:\n" + deviceSessionID + "\n\n");

        DeviceDataCollector.CollectionStatus status = AnalyticsCollector.getCollectionStatus();
        textArea.append("Collection Status: " + status + "\n");
        if (status == DeviceDataCollector.CollectionStatus.FAILED) {
            textArea.append("Error: " + status.getError());
        }
    }
}