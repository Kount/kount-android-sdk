package com.kount.checkoutexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
        sessionID = sessionID.replace("-", "");
        textArea.append("Session ID:\n" + sessionID + "\n\n");

        DataCollector.getInstance().collectForSession(sessionID, new DataCollector.CompletionHandler() {
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
}
