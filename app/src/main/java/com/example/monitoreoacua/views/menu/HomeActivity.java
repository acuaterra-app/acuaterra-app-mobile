package com.example.monitoreoacua.views.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.service.request.LogoutRequest;
import com.example.monitoreoacua.service.response.LogoutResponse;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiUsersService;
import com.example.monitoreoacua.views.login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnLogout = findViewById(R.id.btnLogout);
        
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        
        btnLogout.setEnabled(false);
        
        ApiUsersService apiService = ApiClient.getClient().create(ApiUsersService.class);
        
        LogoutRequest logoutRequest = new LogoutRequest();
        
        apiService.logout(logoutRequest).enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(@NonNull Call<LogoutResponse> call, @NonNull Response<LogoutResponse> response) {
                btnLogout.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().hasErrors()) {
                        String errorMsg = "Logout encountered errors";
                        if (response.body().getErrors() != null && !response.body().getErrors().isEmpty()) {
                            errorMsg = response.body().getErrors().get(0);
                        }
                        Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (response.isSuccessful() && response.body() != null) {

                    String message = response.body().getMessage();
                    
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.apply();
                    
                    Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle error response
                    String errorMessage = "Logout failed";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += ": " + response.errorBody().string();
                        } catch (Exception e) {
                        }
                    }
                    Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LogoutResponse> call, @NonNull Throwable t) {
                // Re-enable the button
                btnLogout.setEnabled(true);
                
                String errorMessage = "Error connecting to server: " + t.getLocalizedMessage();
                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
