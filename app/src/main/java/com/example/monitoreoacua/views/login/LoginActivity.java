package com.example.monitoreoacua.views.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.auth.AuthToken;
import com.example.monitoreoacua.service.request.LoginRequest;
import com.example.monitoreoacua.service.response.LoginResponse;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiUsersService;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView txtNotificationsPermissions;
    private int loginAttempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtNotificationsPermissions = findViewById(R.id.txtNotificationsPermissions);

        // Check notification permission for Android 13+
        checkNotificationPermission(false);
        btnLogin.setOnClickListener(v -> login());
        
        txtNotificationsPermissions.setOnClickListener(v -> {
            checkNotificationPermission(true);
        });
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            btnLogin.setEnabled(false);
            Toast.makeText(this, "Access blocked due to multiple failed attempts", Toast.LENGTH_LONG).show();
            return;
        }

        btnLogin.setEnabled(false);
        
        // Get Firebase token and proceed with login
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, 
                            "Failed to get device token. Please try again.", 
                            Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get the FCM token
                    String deviceToken = task.getResult();
                    
                    // Proceed with login using the token
                    proceedWithLogin(email, password, deviceToken);
                }
            });
    }
    
    private void proceedWithLogin(String email, String password, String deviceToken) {
        ApiUsersService apiUserService = ApiClient.getClient().create(ApiUsersService.class);
        LoginRequest loginRequest = new LoginRequest(email, password, deviceToken);
        
        apiUserService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    try {
                        AuthToken authToken = loginResponse.getToken();
                        String token = authToken != null ? authToken.getToken() : null;
                        if (token == null) {
                            Toast.makeText(LoginActivity.this, "Authentication error: Invalid response", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        token = token.trim();
                        String userName = loginResponse.getUser().getName();

                        Toast.makeText(LoginActivity.this, "Welcome, " + userName, Toast.LENGTH_SHORT).show();
                        loginAttempts = 0;

                        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, ListFarmsActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Error processing login data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loginAttempts++;

                    String errorMessage = "Invalid credentials. Attempt " + loginAttempts + " of " + MAX_LOGIN_ATTEMPTS;
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                        }
                    }

                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Error connecting to server: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Checks if notification permission is needed (Android 13+) and requests it if not granted
     * @param forceRequest if true, will request permission regardless of whether it was asked before
     */
    private void checkNotificationPermission(boolean forceRequest) {
        // Check if device is running Android 13 (API 33) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                
                // Check if we should show the permission request
                SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
                boolean askedBefore = sharedPreferences.getBoolean("notification_permission_asked", false);
                
                // Check if we can show the permission dialog or if we need to direct to settings
                boolean canShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.POST_NOTIFICATIONS);
                
                if (!askedBefore || (forceRequest && canShowRationale)) {
                    // First time asking or user hasn't permanently denied, save that we've asked
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("notification_permission_asked", true);
                    editor.apply();
                    
                    Log.d(TAG, "Requesting notification permission for Android 13+");
                    // Request the permission
                    ActivityCompat.requestPermissions(this, 
                            new String[]{Manifest.permission.POST_NOTIFICATIONS}, 
                            NOTIFICATION_PERMISSION_CODE);
                } else if (forceRequest && !canShowRationale) {
                    // User has permanently denied permission, direct to app settings
                    openAppSettings();
                } else {
                    // Already asked before, just show the TextView
                    showPermissionMessage();
                }
            } else {
                Log.d(TAG, "Notification permission already granted");
                txtNotificationsPermissions.setVisibility(View.GONE);
            }
        } else {
            Log.d(TAG, "Notification permission not needed for this Android version");
            txtNotificationsPermissions.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            // Verifica si el permiso fue concedido o no
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Log.d(TAG, "Notification permission granted");
                txtNotificationsPermissions.setVisibility(View.GONE);
            } else {
                // Permiso denegado, muestra el mensaje inmediatamente
                Log.d(TAG, "Notification permission denied");
                showPermissionMessage();
            }
        }
    }

    /**
     * Helper method to show the permission message with formatted text
     */
    /**
     * Helper method to show the permission message with formatted text
     */
    private void showPermissionMessage() {
        txtNotificationsPermissions.setVisibility(View.VISIBLE);
        String baseMessage = "Esta app necesita permisos para un correcto funcionamiento";
        String clickText = " Click para reparar";
        
        // Create a SpannableString with the complete message
        SpannableString spannableString = new SpannableString(baseMessage + clickText);
        
        // Apply white color and bold style to the "Click para reparar" part
        spannableString.setSpan(
            new ForegroundColorSpan(getResources().getColor(R.color.white)),
            baseMessage.length(), 
            baseMessage.length() + clickText.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        
        // Apply bold style to the "Click para reparar" part
        spannableString.setSpan(
            new StyleSpan(Typeface.BOLD),
            baseMessage.length(), 
            baseMessage.length() + clickText.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        
        txtNotificationsPermissions.setText(spannableString);
    }
    private void openAppSettings() {
        Toast.makeText(this, "Habilite los permisos de notificación en configuración",
                Toast.LENGTH_LONG).show();
        
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(android.net.Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }
}
