package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
    private static final String ARG_FARM = "farm";
    private static final String TAG = "RegisterModulesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() llamado");

        setContentView(R.layout.activity_register_modules); // Este layout debe contener un FrameLayout con id fragmentContainer

        // Obtener la granja desde el intent
        retrieveFarmFromIntent();

        if (farm == null) {
            Toast.makeText(this, "No se proporcionó información de la granja", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Finalizando actividad: objeto 'farm' es null");
            finish();
            return;
        }

        // Cargar fragmento inicial solo si es la primera creación
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                farm = intent.getParcelableExtra(ARG_FARM, Farm.class);
            } else {
                farm = intent.getParcelableExtra(ARG_FARM);
            }

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
        // Crear y cargar el fragmento directamente
        loadFragment(RegisterModuleFragment.newInstance("", "", farm), false);
    }

    @Override
    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment == null) {
            Log.e(TAG, "Intento de cargar un fragmento null");
            return;
        }

        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerRegisterModule, fragment);

            if (addToBackStack) {
                transaction.addToBackStack(null);
            }

            transaction.commit();
            Log.d(TAG, "Fragmento cargado exitosamente: " + fragment.getClass().getSimpleName());

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar el fragmento", e);
            Toast.makeText(this, "Error al cargar el formulario", Toast.LENGTH_LONG).show();
        }
    }
}
