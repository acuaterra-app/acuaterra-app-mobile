package com.example.monitoreoacua.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.farms.FarmDetailsActivity;
import com.example.monitoreoacua.views.login.LoginActivity;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PREF_NAME = "user_prefs";
    private static final String TOKEN_KEY = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: MainActivity is starting");
        setContentView(R.layout.activity_main);

        // Log details about the intent that started this activity
        Intent receivedIntent = getIntent();
        logIntentDetails(receivedIntent);
        
        // Check if we were launched from a notification with a farmId
        if (receivedIntent != null && receivedIntent.hasExtra("farmId")) {
            String farmId = receivedIntent.getStringExtra("farmId");
            Log.d(TAG, "onCreate: Received farmId from notification: " + farmId);
            
            // Show a toast for debugging purposes
            Toast.makeText(this, "Received farmId: " + farmId, Toast.LENGTH_LONG).show();
            
            // Navigate to FarmDetailsActivity with the farmId
            Intent farmDetailsIntent = new Intent(this, FarmDetailsActivity.class);
            farmDetailsIntent.putExtra("farmId", farmId);
            
            // Add any other extras from the original intent
            if (receivedIntent.hasExtra("notificationSource")) {
                farmDetailsIntent.putExtra("notificationSource", 
                    receivedIntent.getStringExtra("notificationSource"));
            }
            if (receivedIntent.hasExtra("notificationType")) {
                farmDetailsIntent.putExtra("notificationType", 
                    receivedIntent.getStringExtra("notificationType"));
            }
            
            Log.d(TAG, "onCreate: Launching FarmDetailsFragmentActivity with farmId: " + farmId);
            startActivity(farmDetailsIntent);
            finish();
            return;
        }
        
        Intent intent;

        try {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String token = sharedPreferences.getString(TOKEN_KEY, null);

            if (token == null || token.isEmpty() || token.trim().length() < 0) {
                Log.d(TAG, "No valid token found, redirecting to LoginActivity");
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }

            intent = new Intent(MainActivity.this, ListFarmsActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error checking token: " + e.getMessage(), e);

            intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: Received new intent");
        
        // Log details about the new intent
        logIntentDetails(intent);
        
        // Check if the new intent contains a farmId
        if (intent != null && intent.hasExtra("farmId")) {
            String farmId = intent.getStringExtra("farmId");
            Log.d(TAG, "onNewIntent: Received farmId from notification: " + farmId);
            
            // Show a toast for debugging purposes
            Toast.makeText(this, "Received farmId in onNewIntent: " + farmId, Toast.LENGTH_LONG).show();
            
            // Navigate to FarmDetailsActivity with the farmId
            Intent farmDetailsIntent = new Intent(this, FarmDetailsActivity.class);
            farmDetailsIntent.putExtra("farmId", farmId);
            
            // Add any other extras from the original intent
            if (intent.hasExtra("notificationSource")) {
                farmDetailsIntent.putExtra("notificationSource", intent.getStringExtra("notificationSource"));
            }
            if (intent.hasExtra("notificationType")) {
                farmDetailsIntent.putExtra("notificationType", intent.getStringExtra("notificationType"));
            }
            
            Log.d(TAG, "onNewIntent: Launching FarmDetailsFragmentActivity with farmId: " + farmId);
            startActivity(farmDetailsIntent);
            finish();
        }
    }
    

    private void logIntentDetails(Intent intent) {
        if (intent == null) {
            Log.d(TAG, "logIntentDetails: Intent is null");
            return;
        }
        
        Log.d(TAG, "Intent details:");
        Log.d(TAG, "  Action: " + intent.getAction());
        Log.d(TAG, "  Data URI: " + (intent.getData() != null ? intent.getData().toString() : "null"));
        
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(TAG, "  Extras:");
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d(TAG, "    " + key + ": " + (value != null ? value.toString() : "null"));
            }
        } else {
            Log.d(TAG, "  No extras in the intent");
        }
    }
}
