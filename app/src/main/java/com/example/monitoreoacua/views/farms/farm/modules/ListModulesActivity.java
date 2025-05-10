package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.fragments.ListModulesFragment;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Activity for displaying and managing modules.
 * Extends BaseActivity to use the common navigation and layout structure.
 */
public class ListModulesActivity extends BaseActivity implements ListModulesFragment.OnModuleInteractionListener {

    private static final String TAG = "ListModulesActivity";
    public static final String ARG_MODULE = "module";
    private static final String ARG_MODULE_ID = "module_id";

    private Farm farm;
    private ListModulesFragment modulesFragment;
    private Module module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() llamado");

        Log.d(TAG, "Intent recibido: " + getIntent());

        // Obtener el objeto Farm desde el intent
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            farm = getIntent().getParcelableExtra("farm", Farm.class);
            module = getIntent().getParcelableExtra("module", Module.class);
        } else {
            farm = (Farm) getIntent().getParcelableExtra("farm");
            module =(Module)  getIntent().getParcelableExtra("module");
        }

        if (farm == null) {
            Log.e(TAG, "Farm no recibido. Cerrando actividad.");
            finish(); // Finaliza si no se recibió la finca
            return;
        } else {
            Log.d(TAG, "Farm recibido: ID = " + farm.getId() + ", Nombre = " + farm.getName());
        }

        if (module != null) {
            Log.d(TAG, "Módulo recibido: ID = " + module.getId() + ", Nombre = " + module.getName());
        } else {
            Log.d(TAG, "No se recibió un módulo en el Intent.");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActivityTitle() {
        Log.d(TAG, "Título de actividad solicitado.");
        return getString(R.string.modules_title);
    }

    @Override
    protected void loadInitialFragment() {
        Log.d(TAG, "Cargando fragmento inicial ListModulesFragment para farm ID: " + farm.getId());
        modulesFragment = ListModulesFragment.newInstance(String.valueOf(farm.getId()));
        loadFragment(modulesFragment, false);
    }

    @Override
    public void onModuleSelected(Module module) {
        Log.d(TAG, "onModuleSelected: ID = " + module.getId() + ", Nombre = " + module.getName());
        Intent intent = new Intent(this, ViewModuleActivity.class);
        intent.putExtra(ARG_MODULE_ID, module.getId());
        startActivity(intent);
    }

    @Override
    public void onRegisterNewModule() {
        Log.d(TAG, "Navegando a RegisterModulesActivity con farm: ID = " + farm.getId());
        Intent intent = new Intent(this, RegisterModulesActivity.class);
        intent.putExtra("farm", farm);
        startActivity(intent);
    }

    @Override
    public void navigateToRegisterModules(Farm farm) {
        Log.d(TAG, "navigateToRegisterModules llamado con farm ID: " + farm.getId());
        Intent intent = new Intent(this, RegisterModulesActivity.class);
        intent.putExtra("farm", farm);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void navigateToUpdateModules(Module module){
        farm = module.getFarm();
        Log.d(TAG, "navigateToUpdateModules llamado con farm ID: " + farm.getId() +
                ", y módulo: " + (module != null ? module.getName() : "null"));
        Intent intent = new Intent(this, UpdateModuleActivity.class);
        intent.putExtra("farm", farm);
        intent.putExtra("module", (Parcelable) module);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume llamado. Refrescando módulos si es posible.");
        if (modulesFragment != null) {
            Log.d(TAG, "Refrescando módulo desde fragment.");
            modulesFragment.refreshModules();
        } else {
            Log.d(TAG, "modulesFragment es null. No se refrescan módulos.");
        }
    }
}
