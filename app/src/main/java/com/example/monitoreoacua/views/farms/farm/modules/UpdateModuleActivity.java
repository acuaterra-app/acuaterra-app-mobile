package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;

import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.fragments.UpdateModuleFragment;
import com.example.monitoreoacua.views.BaseActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.monitoreoacua.databinding.ActivityUpdateModuleBinding;

import com.example.monitoreoacua.R;

public class UpdateModuleActivity extends BaseActivity {

    private static final String TAG = "UpdateModuleActivity";
    private static final String ARG_FARM = "farm";
    private static final String ARG_MODULE = "module";

    private Farm farm;
    private Module module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() llamado");

        // Inflar el layout específico dentro del contenedor de fragmentos de BaseActivity
        FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);
        getLayoutInflater().inflate(R.layout.activity_update_module, fragmentContainer, true);

        retrieveDataFromIntent();


        //if (farm == null || module == null)
        if (farm == null ) {
            Toast.makeText(this, "Datos insuficientes para actualizar el módulo", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Faltan datos: farm = " + farm + ", module = " + module);
            finish();
            return;
        }

        // Ajustes de padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            loadInitialFragment();
        }
    }

    private void retrieveDataFromIntent() {
        Intent intent = getIntent();
        if (intent == null) return;

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                farm = intent.getParcelableExtra(ARG_FARM, Farm.class);
                module = intent.getParcelableExtra(ARG_MODULE, Module.class);
            } else {
                farm = intent.getParcelableExtra(ARG_FARM);
                module = (Module) intent.getParcelableExtra(ARG_MODULE);
            }

            Log.d(TAG, "Datos recibidos: Farm = " + (farm != null ? farm.getName() : "null") +
                    ", Module = " + (module != null ? module.getName() : "null"));

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener datos del Intent", e);
        }
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.update_module);
    }

    @Override
    protected void loadInitialFragment() {
        Log.d(TAG, "Cargando UpdateModuleFragment con módulo: " + module.getName());
        loadFragment(UpdateModuleFragment.newInstance(module), false);
    }
}