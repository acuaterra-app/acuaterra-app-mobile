package com.example.monitoreoacua.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
        setContentView(R.layout.activity_main);

        // Check if token exists in SharedPreferences immediately
        Log.d(TAG, "Checking for authentication token...");
        Intent intent;
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String token = sharedPreferences.getString(TOKEN_KEY, null);
            
            // Log token details (partially masked for security)
            if (token != null) {
                String maskedToken = token.length() > 10 ? 
                    token.substring(0, 4) + "..." + token.substring(token.length() - 4) : 
                    "***";
                Log.d(TAG, "Token found: " + maskedToken + " (length: " + token.length() + ")");
            } else {
                Log.d(TAG, "Token is null");
            }
            
            // If token exists and is valid, redirect to ListFarmsActivity, otherwise to LoginActivity
            if (token != null && !token.isEmpty() && token.trim().length() > 0) {
                Log.d(TAG, "Valid token found, redirecting to ListFarmsActivity");
                intent = new Intent(MainActivity.this, ListFarmsActivity.class);
            } else {
                Log.d(TAG, "No valid token found, redirecting to LoginActivity");
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking token: " + e.getMessage(), e);
            // Default to LoginActivity in case of any error
            intent = new Intent(MainActivity.this, LoginActivity.class);
        }
        
        startActivity(intent);
        finish();
    }

}
