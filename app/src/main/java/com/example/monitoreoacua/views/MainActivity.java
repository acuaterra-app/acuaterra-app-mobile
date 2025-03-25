package com.example.monitoreoacua.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.monitoreoacua.firebase.FireBaseNotificationManager;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.views.login.LoginActivity;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTag";
    private static final String PREF_NAME = "user_prefs";
    private static final String TOKEN_KEY = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: MainActivity is starting");
        setContentView(R.layout.activity_main);

        handleNotificationIfNeeded(this, getIntent().getExtras());

        Intent intent;

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
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: MainActivity is starting");
        setIntent(intent);
        handleNotificationIfNeeded(this, intent.getExtras());
    }

    private void handleNotificationIfNeeded(Context context, Bundle extras) {
        if (extras != null && extras.containsKey("notification")) {
            Notification notification = extras.getParcelable("notification", Notification.class);
            Log.d(TAG, "Notificación recibida a través del intent: " + notification.getMessage());
            new FireBaseNotificationManager().processNotification(context, notification);
        } else {
            Log.d(TAG, "No se recibieron notificaciones a través del intent");
        }
    }
}
