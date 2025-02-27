package com.example.monitoreoacua.views.pruebas;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.models.objects.Bitacora;
import com.example.monitoreoacua.service.ApiBitacoraService;
import com.example.monitoreoacua.service.ApiClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// PruebaBitacora.java (corregido)
public class PruebaBitacora extends AppCompatActivity {

    private ApiBitacoraService apiBitacoraService;
    private TextView tvJsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_bitacora);

        tvJsonResponse = findViewById(R.id.tvJsonResponseBitacora);
        apiBitacoraService = ApiClient.getClient().create(ApiBitacoraService.class);

        obtenerBitacoras();
    }

    private void obtenerBitacoras() {
        Call<List<Bitacora>> call = apiBitacoraService.getAllBitacoras();
        call.enqueue(new Callback<List<Bitacora>>() {
            @Override
            public void onResponse(@NonNull Call<List<Bitacora>> call,
                                   @NonNull Response<List<Bitacora>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Bitacora> bitacoras = response.body();
                    mostrarRespuesta(bitacoras);
                } else {
                    manejarError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Bitacora>> call,
                                  @NonNull Throwable t) {
                manejarError("Fallo en la conexión: " + t.getMessage());
            }
        });
    }

    private void mostrarRespuesta(List<Bitacora> bitacoras) {
        String jsonResponse = new Gson().toJson(bitacoras);
        tvJsonResponse.setText(jsonResponse);
        // Opcional: Log detallado
        Log.d("BITACORAS_RESPONSE", "Datos recibidos: " + jsonResponse);
    }

    private void manejarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        Log.e("API_ERROR", mensaje);
        tvJsonResponse.setText(mensaje);
    }
}

// BitacoraResponse.java (ELIMINAR ESTA CLASE - No es necesaria)
// La API directamente devuelve un array de Bitacoras