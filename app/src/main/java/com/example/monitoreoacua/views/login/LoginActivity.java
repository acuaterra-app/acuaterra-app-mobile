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
import android.os.Handler;
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
import com.example.monitoreoacua.service.ApiAuthService;
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

        checkNotificationPermission();
        btnLogin.setOnClickListener(v -> login());
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

                        String deviceToken = task.getResult();

                        proceedWithLogin(email, password, deviceToken);
                    }
                });
    }

    private void proceedWithLogin(String email, String password, String deviceToken) {
        ApiAuthService apiUserService = ApiClient.getClient().create(ApiAuthService.class);
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
                        
                        // Add null check before accessing user object
                        String userName = "User";
                        if (loginResponse.getUser() != null) {
                            userName = loginResponse.getUser().getName() != null ? 
                                       loginResponse.getUser().getName() : "User";
                        }
                        
                        Toast.makeText(LoginActivity.this, "Welcome, " + userName, Toast.LENGTH_SHORT).show();
                        loginAttempts = 0;

                        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, ListFarmsActivity.class);
                        startActivity(intent);
                        new Handler().postDelayed(() -> finish(), 100);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing login data", e);
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
                Log.e(TAG, "Error logging in", t);
                Toast.makeText(LoginActivity.this, "Error connecting to server: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void checkNotificationPermission() {
        // Check if device is running Android 13 (API 33) or higher
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        // Check if the permission is already granted
        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }

        Log.d(TAG, "Requesting notification permission for Android 13+");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                NOTIFICATION_PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted");
                txtNotificationsPermissions.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "Notification permission denied");
                showPermissionMessage();
                txtNotificationsPermissions.setOnClickListener(v -> {
                    openAppSettings();
                });
            }
        }
    }

    private void showPermissionMessage() {
        txtNotificationsPermissions.setVisibility(View.VISIBLE);
        String baseMessage = "Esta app necesita permisos para un correcto funcionamiento";
        String clickText = " Click para reparar";

        SpannableString spannableString = new SpannableString(baseMessage + clickText);

        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.white)),
                baseMessage.length(),
                baseMessage.length() + clickText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

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
