package com.example.monitoreoacua.views.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.login.LoginActivity;

public class CloseSessionActivity extends AppCompatActivity {

    private static final String TAG = "CloseSessionActivity";
    private static final String PREFS_NAME = "user_prefs";
    private static final String TOKEN_KEY = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_close_session);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button buttonConfirmLogout = findViewById(R.id.buttonConfirmLogout);
        Button buttonCancelLogout = findViewById(R.id.buttonCancelLogout);

        buttonConfirmLogout.setOnClickListener(v -> logout());

        buttonCancelLogout.setOnClickListener(v -> cancelLogout());
    }


    private void logout() {
        Log.i(TAG, "Performing user logout");
        
        // Remove authentication token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
        
        Log.d(TAG, "Token removed from SharedPreferences");
        
        Intent intent = new Intent(CloseSessionActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
        startActivity(intent);
        finish();
    }

    private void cancelLogout() {
        Log.d(TAG, "Logout canceled by user");
        finish();
    }
}
