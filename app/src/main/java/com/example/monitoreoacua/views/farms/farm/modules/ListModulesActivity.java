package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
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
    
    private Farm farm;
    private ListModulesFragment modulesFragment;

    private static final String ARG_MODULE_ID = "module_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get farm object from intent
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            farm = getIntent().getParcelableExtra("farm", Farm.class);
        } else {

        farm = (Farm) getIntent().getParcelableExtra("farm");
        }
        if (farm == null) {
            finish(); // Close activity if farm is missing
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
        modulesFragment = ListModulesFragment.newInstance(String.valueOf(farm.getId()));
        loadFragment(modulesFragment, false);
    }
    
    @Override
    public void onModuleSelected(Module module) {
        Log.d("My_Tag", "onModuleSelected: " + module.getId());
        Intent intent = new Intent(this, ViewModuleActivity.class);
        intent.putExtra(ARG_MODULE_ID, module.getId());
        startActivity(intent);
    }
    
    @Override
    public void onRegisterNewModule() {
        // Navigate to RegisterModulesActivity
        Intent intent = new Intent(this, RegisterModulesActivity.class);
        intent.putExtra("farm", farm);
        startActivity(intent);
    }
    
    @Override
    public void navigateToRegisterModules(Farm farm) {
        Intent intent = new Intent(this, RegisterModulesActivity.class);
        intent.putExtra("farm", farm);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

