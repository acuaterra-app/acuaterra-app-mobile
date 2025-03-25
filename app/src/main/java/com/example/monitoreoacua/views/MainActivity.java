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
    }
}
