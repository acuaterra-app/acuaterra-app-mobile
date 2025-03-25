package com.example.monitoreoacua.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.monitoreoacua.R;
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
        
        // Use NotificationManager to process any notification intents
        String action = receivedIntent.getAction();
        if (action != null && action.equals("com.example.monitoreoacua.NOTIFICATION_CLICKED")) {
            Log.i(TAG, "onCreate: Notification click detected with action: " + action);
            if (com.example.monitoreoacua.firebase.NotificationManager.getInstance().processNotificationIntent(this, receivedIntent)) {
                Log.d(TAG, "onCreate: Notification successfully processed, finishing activity");
                finish();
                return;
            } else {
                Log.w(TAG, "onCreate: Notification processing returned false, continuing with normal flow");
            }
        } else if (com.example.monitoreoacua.firebase.NotificationManager.getInstance().processNotificationIntent(this, receivedIntent)) {
            // For backward compatibility with other notification intents
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
        
        // Use NotificationManager to process any notification intents
        String action = intent.getAction();
        if (action != null && action.equals("com.example.monitoreoacua.NOTIFICATION_CLICKED")) {
            Log.i(TAG, "onNewIntent: Notification click detected with action: " + action);
            boolean processed = com.example.monitoreoacua.firebase.NotificationManager.getInstance().processNotificationIntent(this, intent);
            Log.d(TAG, "onNewIntent: Notification processing result: " + (processed ? "successful" : "not handled"));
        } else {
            // For backward compatibility with other notification intents
            com.example.monitoreoacua.firebase.NotificationManager.getInstance().processNotificationIntent(this, intent);
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
