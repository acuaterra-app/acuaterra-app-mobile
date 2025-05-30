package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.fragments.RegisterModuleFragment;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Activity que permite registrar nuevos módulos en una granja seleccionada.
 * Extiende de BaseActivity para mantener una estructura común de navegación.
 */
public class RegisterModulesActivity extends BaseActivity {
    private Farm farm;
    private static final String EXTRA_FARM = "extra_farm";
    private static final String TAG = "RegisterModulesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() llamado");

        // Inflar el layout específico dentro del contenedor de fragmentos de BaseActivity
        FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);
        getLayoutInflater().inflate(R.layout.activity_register_modules, fragmentContainer, true);

        // Recuperar el objeto 'farm' desde el intent
        retrieveFarmFromIntent();

        if (farm == null) {
            Toast.makeText(this, "No se proporcionó información de la granja", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Objeto 'farm' no encontrado en el intent");
            finish(); // Cierra la actividad si no se proporciona una granja
            return;
        }

        // Ajustes de padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cargar fragmento inicial si es la primera vez que se crea
        if (savedInstanceState == null) {
            loadInitialFragment();
        }
    }

    /**
     * Recupera el objeto Farm desde el Intent que inició esta actividad.
     */
    private void retrieveFarmFromIntent() {
        Intent intent = getIntent();

        if (intent == null) {
            Log.e(TAG, "Intent recibido es null");
            return;
        }

        try {
            farm = com.example.monitoreoacua.utils.FarmBundleUtil.getFarmFromIntent(intent);

            if (farm != null) {
                Log.d(TAG, "Granja recibida: " + farm.getName() + ", ID: " + farm.getId());
            } else {
                Log.e(TAG, "El objeto 'farm' es null después de recuperar desde el Intent");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener la granja desde el intent", e);
        }
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.register_new_module);
    }

    @Override
    protected void loadInitialFragment() {
        Log.d(TAG, "Cargando RegisterModuleFragment con la granja: " + (farm != null ? farm.getName() : "null"));
        loadFragment(RegisterModuleFragment.newInstance("", "", farm), false);
    }
}
