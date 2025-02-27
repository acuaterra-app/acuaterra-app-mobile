package com.example.monitoreoacua.views.pruebas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.models.objects.Modulo;
import com.example.monitoreoacua.models.response.ModuloResponse;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulosService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PruebaJson extends AppCompatActivity {
    private ApiModulosService apiService;
    private SharedPreferences sharedPreferences;
    private String token;
    private TextView tvJsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_json);

        // Inicializar TextView
        tvJsonResponse = findViewById(R.id.tvJsonResponse);

        // Inicializar API
        apiService = ApiClient.getClient().create(ApiModulosService.class);

        // Obtener token desde SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        Log.i("PRUEBA JSON","token:" + token);

        if (token != null && !token.isEmpty()) {
            obtenerModulos(token);
        } else {
            Toast.makeText(this, "Error: Token no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerModulos(String token) {
        Call<List<Modulo>> call = apiService.getAllModules("Bearer " + token);
        call.enqueue(new Callback<List<Modulo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Modulo>> call, @NonNull Response<List<Modulo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //List<Modulo> modulos = response.body();
                    String jsonResponse = new Gson().toJson(response.body());

                    // Mostrar en TextView
                    tvJsonResponse.setText(jsonResponse);
                    //mostrarModulos(modulos);
                } else {
                    manejarError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Modulo>> call, @NonNull Throwable t) {
                Toast.makeText(PruebaJson.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API Error", t.getMessage(), t);
            }
        });
    }

    private void mostrarModulos(List<Modulo> modulos) {
        StringBuilder sb = new StringBuilder();
        for (Modulo modulo : modulos) {
            sb.append("Módulo: ").append(modulo.getNombre())
                    .append("\nUbicación: ").append(modulo.getUbicacion())
                    .append("\nEspecies: ").append(modulo.getEspeciePescados())
                    .append("\n\n");
        }
        tvJsonResponse.setText(sb.toString());
    }

    private void manejarError(Response<List<Modulo>> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Error desconocido";
            Log.e("API Error", "Código: " + response.code() + " - " + errorBody);
            Toast.makeText(this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("API Error", "Error al leer cuerpo de error", e);
        }
    }
}