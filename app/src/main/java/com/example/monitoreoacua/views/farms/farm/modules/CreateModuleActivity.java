package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.request.CreateModuleRequest;
import com.example.monitoreoacua.service.response.CreateModuleResponse;
import com.example.monitoreoacua.views.login.LoginActivity;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateModuleActivity extends AppCompatActivity {

    private EditText etModuleName, etLocation, etLatitude, etLongitude, etFishType, etFishQuantity, etFishAge, etVolumeUnit;
    private Button btnRegisterModule;
    private int farmId, createdByUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_modules);

        etModuleName = findViewById(R.id.etModuleName);
        etLocation = findViewById(R.id.etLocation);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        etFishType = findViewById(R.id.etFishType);
        etFishQuantity = findViewById(R.id.etFishQuantity);
        etFishAge = findViewById(R.id.etFishAge);
        etVolumeUnit = findViewById(R.id.etVolumeUnit);
        btnRegisterModule = findViewById(R.id.btnRegisterModule);

        farmId = getIntent().getIntExtra("farmId", 0);
        createdByUserId = getIntent().getIntExtra("created_by_user_id", 0);
        btnRegisterModule.setOnClickListener(v -> registerModule());

        Toast.makeText(CreateModuleActivity.this, "CMA - User id: "+ createdByUserId , Toast.LENGTH_SHORT).show();
    }

    private void registerModule() {
        String moduleName = etModuleName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String latitudeStr = etLatitude.getText().toString().trim();
        String longitudeStr = etLongitude.getText().toString().trim();
        String fishType = etFishType.getText().toString().trim();
        String fishQuantityStr = etFishQuantity.getText().toString().trim();
        String fishAgeStr = etFishAge.getText().toString().trim();
        String volumeUnit = etVolumeUnit.getText().toString().trim();

        // Validaciones de entrada
        if (moduleName.isEmpty() || location.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()
                || fishType.isEmpty() || fishQuantityStr.isEmpty() || fishAgeStr.isEmpty() || volumeUnit.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar si los valores numéricos son correctos
        double latitude, longitude;
        int fishQuantity, fishAge;

        try {
            latitude = Double.parseDouble(latitudeStr);
            longitude = Double.parseDouble(longitudeStr);
            fishQuantity = Integer.parseInt(fishQuantityStr);
            fishAge = Integer.parseInt(fishAgeStr);

            if (latitude < -90 || latitude > 90) {
                Toast.makeText(this, "Latitud fuera de rango (-90 a 90)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (longitude < -180 || longitude > 180) {
                Toast.makeText(this, "Longitud fuera de rango (-180 a 180)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (fishQuantity < 0) {
                Toast.makeText(this, "La cantidad de peces no puede ser negativa", Toast.LENGTH_SHORT).show();
                return;
            }
            if (fishAge < 0) {
                Toast.makeText(this, "La edad de los peces no puede ser negativa", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear solicitud al API
        ApiModulesService apiModulesService = ApiClient.getClient().create(ApiModulesService.class);
        CreateModuleRequest createModuleRequest = new CreateModuleRequest(
                moduleName, location, latitudeStr, longitudeStr,
                fishType, fishQuantityStr, fishAgeStr, volumeUnit,
                farmId, createdByUserId, Collections.singletonList(0)
        );

        String authToken = createModuleRequest.getAuthToken();
        apiModulesService.createModule(createModuleRequest, authToken).enqueue(new Callback<CreateModuleResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateModuleResponse> call, @NonNull Response<CreateModuleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreateModuleResponse createModuleResponse = response.body();
                    Log.d("API_RESPONSE", "Respuesta exitosa: " + createModuleResponse);

                    Toast.makeText(CreateModuleActivity.this, createModuleResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    String errorMsg = "Error en la respuesta del servidor";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e("API_ERROR", "Error al leer el cuerpo de error", e);
                        }
                    }
                    Log.e("API_ERROR", "Código: " + response.code() + " - Mensaje: " + errorMsg);
                    Toast.makeText(CreateModuleActivity.this, "Error en la respuesta del servidor: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateModuleResponse> call, @NonNull Throwable t) {
                Log.e("API_FAILURE", "Fallo en la conexión", t);
                Toast.makeText(CreateModuleActivity.this, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
