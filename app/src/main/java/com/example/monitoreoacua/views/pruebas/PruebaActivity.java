package com.example.monitoreoacua.views.pruebas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.models.adapters.ModuloAdapter;
import com.example.monitoreoacua.models.objects.Modulo;
import com.example.monitoreoacua.models.response.ModuloResponse;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulosService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PruebaActivity extends AppCompatActivity {

    private RecyclerView recyclerViewModulos;
    private ModuloAdapter adapter;
    private TextView jsonTextView; // Para mostrar el JSON en pantalla
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        jsonTextView = findViewById(R.id.jsonTextView); // Conectar con el TextView del layout

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (token != null && userId != -1) {
            loadModules(token, userId);
        } else {

        }
    }

    private void loadModules(String token, Integer userId) {
        ApiModulosService apiModulosService = ApiClient.getClient().create(ApiModulosService.class);

        apiModulosService.getAllModules(token).enqueue(new Callback<List<Modulo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Modulo>> call, @NonNull Response<List<Modulo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Modulo> allModules = response.body(); // Get the list directly
                
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(allModules);
                    jsonTextView.setText(jsonResponse);
                
                    adapter = new ModuloAdapter(allModules);
                    recyclerViewModulos.setAdapter(adapter);
                } else {
                    jsonTextView.setText("Error en la respuesta: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<Modulo>> call, Throwable t) {
                jsonTextView.setText("Error en la llamada a la API: " + t.getMessage());
            }
        });
    }
}
