package com.example.monitoreoacua.views.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.auth.AuthToken;
import com.example.monitoreoacua.service.request.LoginRequest;
import com.example.monitoreoacua.service.response.LoginResponse;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiUsersService;
import com.example.monitoreoacua.views.menu.HomeActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private int loginAttempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);


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

        ApiUsersService apiUserService = ApiClient.getClient().create(ApiUsersService.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        btnLogin.setEnabled(false);
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
                        
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
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
}
