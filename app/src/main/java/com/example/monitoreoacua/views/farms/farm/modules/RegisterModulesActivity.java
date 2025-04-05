package com.example.monitoreoacua.views.farms.farm.modules;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.fragments.RegisterModuleFragment;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Activity for registering a new module.
 * Extends BaseActivity for a common navigation structure.
 */
public class RegisterModulesActivity extends BaseActivity {
    private Farm farm;
    private static final String ARG_FARM = "farm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get farm object from intent
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            farm = getIntent().getParcelableExtra(ARG_FARM, Farm.class);
        } else {
            farm = (Farm) getIntent().getParcelableExtra(ARG_FARM);
        }
        
        if (farm == null) {
            Toast.makeText(this, "No se proporcionó información de la granja", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if farm is missing
            return;
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🔹 Llamar loadInitialFragment para que cargue el fragmento
        loadInitialFragment();
    }
    
    @Override
    protected String getActivityTitle() {
        return getString(R.string.register_new_module);
    }

    @Override
    protected void loadInitialFragment() {
        Log.d("RegisterModulesActivity", "Loading RegisterModuleFragment with farm: " + farm.getName());
        RegisterModuleFragment registerModuleFragment = RegisterModuleFragment.newInstance("", "");
        loadFragment(registerModuleFragment, false);
    }

    @Override
    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
            Log.d("RegisterModulesActivity", "Fragmento cargado correctamente");
        } catch (Exception e) {
            Log.e("RegisterModulesActivity", "Error al cargar el fragmento", e);
        }
    }
}
