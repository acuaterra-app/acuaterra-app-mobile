package com.example.monitoreoacua.views.granjas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.response.ListFarmResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFarmsActivity extends AppCompatActivity {

    private TextView textViewFarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_farms);

        // Initialize UI components
        textViewFarms = findViewById(R.id.textViewFarms);

        // Fetch farm data from API
        fetchFarms();
    }

    /**
     * Fetches farm data from the API and updates the UI.
     */
    private void fetchFarms() {

        // Obtener el token de autenticación
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Error: Token no encontrado", Toast.LENGTH_SHORT).show();
           return;
        }

        ApiFarmsService apiFarmsService = ApiClient.getClient().create(ApiFarmsService.class);

        apiFarmsService.getFarms(token).enqueue(new Callback<ListFarmResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListFarmResponse> call, @NonNull Response<ListFarmResponse> response) {
                // Check if the response is successful and not null
                if (response.isSuccessful() && response.body() != null) {

                    // Handle errors if present in the response
                    if (response.body().hasErrors()) {
                        String errorMsg = "Errors were encountered while loading farms";
                        if (response.body().getErrors() != null && !response.body().getErrors().isEmpty()) {
                            errorMsg = response.body().getErrors().get(0);
                        }
                        Toast.makeText(ListFarmsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Convert response data to JSON format for display
                    String jsonResponse = new Gson().toJson(response.body().getData());
                    textViewFarms.setText(jsonResponse);
                } else {
                    // Handle failed response
                    String errorMessage = "Loading farms failed";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += ": " + response.errorBody().string();
                        } catch (Exception e) {
                            // Exception handling (optional logging)
                        }
                    }
                    Toast.makeText(ListFarmsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListFarmResponse> call, Throwable t) {
                // Handle network failure or API call failure
                Toast.makeText(ListFarmsActivity.this, "Connection error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}