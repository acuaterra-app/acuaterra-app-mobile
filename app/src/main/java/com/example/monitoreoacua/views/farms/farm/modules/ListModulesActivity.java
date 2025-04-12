package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.fragments.ListModulesFragment;
import com.example.monitoreoacua.views.BaseActivity;

public class ListModulesActivity extends BaseActivity implements ListModulesFragment.OnModuleInteractionListener {

    private Farm farm;
    private ListModulesFragment modulesFragment;
    private static final String TAG = "ListModulesActivity";
    private static final String ARG_MODULE_ID = "module_id";
    private static final String ARG_FARM = "farm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate started");

        // Obtener el Intent
        Intent intent = getIntent();
        if (intent == null) {
            Log.e(TAG, "Intent is null in onCreate");
            finish();
            return;
        } else {
            Log.d(TAG, "Intent received");
        }

        // Obtener el objeto Farm del intent
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            farm = intent.getParcelableExtra(ARG_FARM, Farm.class);
            Log.d(TAG, "Trying to get farm from intent using TIRAMISU method");
        } else {
            farm = intent.getParcelableExtra(ARG_FARM);
            Log.d(TAG, "Trying to get farm from intent using legacy method");
        }

        if (farm == null) {
            Log.e(TAG, "Farm object is null in onCreate");
            finish(); // Finalizar si no hay farm
            return;
        }

        Log.d(TAG, "Farm loaded successfully: " + farm.getName() + ", ID: " + farm.getId());

        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActivityTitle() {
        Log.d(TAG, "Setting activity title to: " + getString(R.string.modules_title));
        return getString(R.string.modules_title);
    }

    @Override
    protected void loadInitialFragment() {
        Log.d(TAG, "loadInitialFragment called");
        if (farm == null) {
            Log.e(TAG, "Farm is null in loadInitialFragment");
            return;
        }

        Log.d(TAG, "Creating ListModulesFragment with farm ID: " + farm.getId());

        modulesFragment = ListModulesFragment.newInstance(String.valueOf(farm.getId()));
        loadFragment(modulesFragment, false);
    }

    @Override
    public void onModuleSelected(Module module) {
        if (module == null) {
            Log.e(TAG, "Selected module is null");
            return;
        }

        Log.d(TAG, "Module selected: ID = " + module.getId());

        Intent intent = new Intent(this, ViewModuleActivity.class);
        intent.putExtra(ARG_MODULE_ID, module.getId());
        startActivity(intent);
    }

    @Override
    public void onRegisterNewModule() {
        Log.d(TAG, "onRegisterNewModule triggered");

        if (farm == null) {
            Log.e(TAG, "Farm object is null when trying to register new module");
            return;
        }

        Log.d(TAG, "Navigating to register module with farm: " + farm.getName() + ", ID: " + farm.getId());

        try {
            Intent intent = new Intent(this, RegisterModulesActivity.class);
            intent.putExtra(ARG_FARM, farm);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error launching RegisterModulesActivity: " + e.getMessage(), e);
        }
    }

    @Override
    public void navigateToRegisterModules(Farm farm) {
        Log.d(TAG, "navigateToRegisterModules called");

        if (farm == null) {
            Log.e(TAG, "Farm object is null in navigateToRegisterModules");
            return;
        }

        Log.d(TAG, "Navigating with explicit farm: " + farm.getName() + ", ID: " + farm.getId());

        try {
            Intent intent = new Intent(this, RegisterModulesActivity.class);
            intent.putExtra(ARG_FARM, farm);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error in navigateToRegisterModules: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        if (modulesFragment != null) {
            Log.d(TAG, "Refreshing modules in fragment");
            modulesFragment.refreshModules();
        } else {
            Log.w(TAG, "modulesFragment is null in onResume");
        }
    }
}
