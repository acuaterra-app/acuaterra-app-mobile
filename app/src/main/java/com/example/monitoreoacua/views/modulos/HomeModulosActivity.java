/*
package com.example.monitoreoacua.views.modulos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.models.adapters.ModuloAdapter;
import com.example.monitoreoacua.models.objects.Modulo;
import com.example.monitoreoacua.models.response.ModuloResponse;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulosService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeModulosActivity extends AppCompatActivity {

    private RecyclerView rvModules;
    private ModuloAdapter moduloAdapter;
    private ApiModulosService apiService;
    private Button btnNuevoModulo;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_modulos);

        // Configurar RecyclerView
        rvModules = findViewById(R.id.rvModules);
        rvModules.setLayoutManager(new LinearLayoutManager(this));
        moduloAdapter = new ModuloAdapter(new ArrayList<>());
        rvModules.setAdapter(moduloAdapter);

        // Inicializar servicio API desde ApiClient
        apiService = ApiClient.getClient().create(ApiModulosService.class);

        // Recuperar token desde SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");


        //int userId = sharedPreferences.getInt("user_id", -1);
        Log.i("antes de funcion obtener modulos","token:" + token);

        // if (token != null && userId != -1) {
        if (token != null) {
            obtenerModulos(token);
        } else {
            Toast.makeText(this, "Error: Token o ID de usuario no encontrados", Toast.LENGTH_SHORT).show();
        }

        // Configurar botón para navegar a otra actividad
        btnNuevoModulo = findViewById(R.id.btnNuevoModulo);
        btnNuevoModulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeModulosActivity.this, RegisterModuloActivity.class);
                startActivity(intent);
            }
        });

    }

    private void obtenerModulos(String token) {
        Log.i("funcion obtener modulos","token:" + token);
        //Call<ModuloResponse> call = apiService.getAllModules("Bearer " + token);
        Call<ModuloResponse> call = apiService.getAllModules(token);
        call.enqueue(new Callback<ModuloResponse>() {
            @Override
            public void onResponse(@NonNull Call<ModuloResponse> call, @NonNull Response<ModuloResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_RESPONSE", "Éxito: " + new Gson().toJson(response.body()));
                    List<Modulo> modulos = response.body().getModules();
                    moduloAdapter.updateData(modulos);
                } else {
                    Toast.makeText(HomeModulosActivity.this, "Error al obtener datos: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("API_RESPONSE", "Error en la respuesta: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModuloResponse> call, @NonNull Throwable t) {
                Toast.makeText(HomeModulosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                //Log.e("API Error", t.getMessage());
                Log.e("API_RESPONSE", "Error en la llamada: " + t.getMessage(), t);
            }
        });
    }
}

*/