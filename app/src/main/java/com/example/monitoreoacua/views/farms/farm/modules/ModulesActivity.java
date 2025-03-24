package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.fragments.ListModulesFragment;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Activity for displaying and managing modules.
 * Extends BaseActivity to use the common navigation and layout structure.
 */
public class ModulesActivity extends BaseActivity implements ListModulesFragment.OnModuleInteractionListener {
    
    private String farmId;
    private ListModulesFragment modulesFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get farmId from intent
        farmId = getIntent().getStringExtra("farmId");
        if (farmId == null || farmId.isEmpty()) {
            finish(); // Close activity if farmId is missing
            return;
        }
        
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected String getActivityTitle() {
        return getString(R.string.modules_title);
    }

    @Override
    protected void loadInitialFragment() {
        // Create and load the ListModulesFragment
        modulesFragment = ListModulesFragment.newInstance(farmId);
        loadFragment(modulesFragment, false);
    }
    
    @Override
    public void onModuleSelected(Module module) {
        // Handle module selection - currently just display a toast
        // In the future, this could navigate to a module detail view
        // Toast.makeText(this, "Módulo seleccionado: " + module.getName(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onRegisterNewModule() {
        // Navigate to RegisterModulesActivity
        Intent intent = new Intent(this, RegisterModulesActivity.class);
        intent.putExtra("farmId", farmId);
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh modules list when returning to this activity
        if (modulesFragment != null) {
            modulesFragment.refreshModules();
        }
    }
}

