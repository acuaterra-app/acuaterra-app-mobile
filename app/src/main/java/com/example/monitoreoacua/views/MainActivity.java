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

        // Verificar token primero
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString(TOKEN_KEY, null);

        if (token == null || token.isEmpty() || token.trim().length() < 0) {
            Log.d(TAG, "No valid token found, redirecting to LoginActivity");
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Verificar si hay notificación pendiente
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("notification")) {
            Log.d(TAG, "Notification found in intent extras, processing...");
            // Procesamos la notificación primero
            handleNotificationIfNeeded(this, extras);
            // No hacemos nada más, la navegación será manejada por el handler de notificaciones
        } else {
            Log.d(TAG, "No notification found, redirecting to ListFarmsActivity");
            // Si no hay notificación, ir a ListFarmsActivity
            Intent farmsIntent = new Intent(MainActivity.this, ListFarmsActivity.class);
            startActivity(farmsIntent);
            finish();
        }
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
            if (notification != null) {
                Log.d(TAG, "Processing notification: " + new Gson().toJson(notification));
                new FireBaseNotificationManager().processNotification(context, notification);
                // No iniciamos automáticamente ListFarmsActivity
                // porque la navegación será manejada por el handler de notificaciones
            } else {
                Log.e(TAG, "Notification object is null even though key exists");
                // Si la notificación es nula, navegamos a ListFarmsActivity como fallback
                Intent intent = new Intent(context, ListFarmsActivity.class);
                startActivity(intent);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).finish();
                }
            }
        } else {
            Log.d(TAG, "No se recibieron notificaciones a través del intent");
        }
    }
}
