package com.kount.checkoutexample;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kount.api.KountSDK;
import com.kount.api.internal.analytics.entities.CollectionStatus;

public class CollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        setTitle("Checkout Page");

        final TextView textArea = findViewById(R.id.textarea);
        final String deviceSessionID = KountSDK.INSTANCE.getSessionId();
        textArea.append("Session ID:\n" + deviceSessionID + "\n\n");

        String status = KountSDK.INSTANCE.getCollectionStatus();
        textArea.append("Collection Status: " + status + "\n");
        if (status.equals(CollectionStatus.FAILED.toString())) {
            textArea.append("Error: " + status);
        }
    }
}