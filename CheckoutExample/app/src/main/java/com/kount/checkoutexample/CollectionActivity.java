package com.kount.checkoutexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kount.api.DataCollector;

import java.util.UUID;

public class CollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        setTitle("Checkout Page");

        final TextView textArea = (TextView) findViewById(R.id.textarea);
        textArea.append("Collection Starting\n\n");

        String sessionID = UUID.randomUUID().toString();
        final String deviceSessionID = sessionID.replace("-", "");
        textArea.append("Session ID:\n" + sessionID + "\n\n");

        // Configure the collector
        final DataCollector dataCollector = com.kount.api.DataCollector.getInstance();
        dataCollector.setDebug(true);
        dataCollector.setContext(this);
        dataCollector.setMerchantID(MainActivity.MERCHANT_ID);
        dataCollector.setEnvironment(MainActivity.ENVIRONMENT);
        dataCollector.setLocationCollectorConfig(DataCollector.LocationConfig.COLLECT);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                dataCollector.collectForSession(deviceSessionID, new com.kount.api.DataCollector.CompletionHandler() {
                    @Override
                    public void completed(String s) {
                        textArea.append("Collection Completed");
                    }

                    @Override
                    public void failed(String s, final DataCollector.Error error) {
                        textArea.append("Collection Failed\n\n");
                        textArea.append(error.toString());
                    }

                });
            }
        });
    }
}
