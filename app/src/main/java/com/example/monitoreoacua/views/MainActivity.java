package com.example.monitoreoacua.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request notification permission for Android 13+ (API 33+)
        checkNotificationPermission();
        
        // Use a Handler to delay the transition to RegisterActivity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create the intent to navigate to RegisterActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent); // start RegisterActivity
                finish();
            }
        }, 3000); // delay of 2000 miliseconds
    }

    /**
     * Checks if notification permission is required and requests it if needed on Android 13+
     */
    private void checkNotificationPermission() {
        // Only for Android 13 (TIRAMISU) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if permission is not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE
                );
                Log.d(TAG, "Requesting notification permission for Android 13+");
            } else {
                Log.d(TAG, "Notification permission already granted");
            }
        } else {
            Log.d(TAG, "Notification permission not required for this Android version");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted");
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Notification permission denied");
                Toast.makeText(this, "Las notificaciones están desactivadas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
