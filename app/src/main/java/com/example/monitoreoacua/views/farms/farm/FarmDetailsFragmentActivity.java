package com.example.monitoreoacua.views.farms.farm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.fragments.FarmDetailsFragment;
import com.example.monitoreoacua.interfaces.OnFragmentInteractionListener;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.views.BaseActivity;
import com.example.monitoreoacua.views.farms.farm.modules.ListModulesActivity;

/**
 * Activity that hosts the FarmDetailsFragment using Fragment approach
 * This activity handles displaying the details of a farm using fragments
 */
public class FarmDetailsFragmentActivity extends BaseActivity implements OnFragmentInteractionListener {

    private static final String TAG = "FarmDetailsFragmentAct";
    private static final String EXTRA_FARM = "extra_farm";
    private Farm farm;

    /**
     * Create an intent to start this activity
     *
     * @param context The context to create the intent from
     * @param farm The farm to display details for
     * @return The intent to start this activity
     */
    public static Intent createIntent(@NonNull Context context, @NonNull Farm farm) {
        Intent intent = new Intent(context, FarmDetailsFragmentActivity.class);
        intent.putExtra(EXTRA_FARM, farm);
        // Set the action that matches the intent filter in AndroidManifest
        intent.setAction("com.example.monitoreoacua.VIEW_FARM_DETAILS_FRAGMENT");
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting FarmDetailsFragmentActivity");
        // Note: No need to set content view here as it's handled by BaseActivity
        // BaseActivity uses activity_base.xml which already has the necessary fragment containers:
        // - topBarContainer: For the top navigation bar
        // - fragmentContainer: For the main content fragments (used by loadFragment method)
        // - navBarContainer: For the bottom navigation bar

        // Get farm from intent
        if (getIntent() != null && getIntent().hasExtra(EXTRA_FARM)) {
            farm = getIntent().getParcelableExtra(EXTRA_FARM, Farm.class);
            Log.d(TAG, "Farm retrieved from intent: " + (farm != null ? farm.getName() : "null"));
            initializeWithFarm();
        } else {
            // Try to get farm ID from intent data if farm object not provided
            // This would be used when the activity is started from a deep link or notification
            tryRetrieveFarmFromId();
        }
    }

    /**
     * Try to retrieve farm information from an ID passed in the intent
     * This is used when the activity is started from a deep link or notification
     */
    private void tryRetrieveFarmFromId() {
        Log.d(TAG, "Farm object not found in intent, checking for farm ID");
        String farmId = null;
        
        // Check if we have a farm_id in the intent extras
        if (getIntent().hasExtra("farm_id")) {
            farmId = getIntent().getStringExtra("farm_id");
        }
        
        if (farmId != null) {
            Log.d(TAG, "Farm ID found in intent: " + farmId);
            // TODO: Load farm data from API using the farm ID
            // For now, show a temporary error message
            Toast.makeText(this, "Cargando información de la granja...", Toast.LENGTH_SHORT).show();
        } else {
            // No farm information provided at all
            Log.e(TAG, "No farm information provided in intent");
            Toast.makeText(this, "No se proporcionó información de la granja", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Initialize the activity with the provided farm object
     */
    private void initializeWithFarm() {
        if (farm == null) {
            Log.e(TAG, "Cannot initialize activity: farm object is null");
            finish();
            return;
        }

        // Set the title based on the farm name
        setActivityTitle(farm.getName());
        
        // Log that we're loading the initial fragment
        Log.d(TAG, "Initializing activity with farm: " + farm.getName());
    }

    @Override
    protected void loadInitialFragment() {
        if (farm != null) {
            // Create and load the farm details fragment
            Log.d(TAG, "Loading initial fragment with farm: " + farm.getName());
            loadFragment(FarmDetailsFragment.newInstance(farm), false);
        } else {
            Log.e(TAG, "Cannot load initial fragment: farm object is null");
        }
    }

    @Override
    public void onEditFarm(Farm farm) {
        // Handle edit farm action
        Log.d(TAG, "Navigating to edit farm: " + farm.getName());
        Intent intent = new Intent(this, FarmFormActivity.class);
        intent.putExtra("farm", farm);
        startActivity(intent);
    }

    @Override
    public void onDeleteFarm(Farm farm) {
        // Handle delete farm action
        Log.d(TAG, "Delete farm requested: " + farm.getName());
        // TODO: Implement farm deletion or show confirmation dialog
        Toast.makeText(this, "Funcionalidad de eliminación no implementada", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onFarmSelected(Farm farm) {
        // Handle farm selection - typically navigate to the farm's details
        // Since we're already in the farm details activity, we might update the UI
        // or reload the fragment with the new farm data
        Log.d(TAG, "Farm selected: " + farm.getName());
        this.farm = farm;
        setActivityTitle(farm.getName());
        loadFragment(FarmDetailsFragment.newInstance(farm), false);
    }

    @Override
    public void onNavigateBack() {
        // Handle back navigation from fragment
        Log.d(TAG, "Navigate back requested");
        onBackPressed();
    }

    @Override
    public void onViewFarmModules(Farm farm) {
        // Handle navigation to the farm modules screen
        Log.d(TAG, "Navigating to modules for farm: " + farm.getName());
        Intent intent = new Intent(this, ListModulesActivity.class);
        intent.putExtra("farm", farm);
        startActivity(intent);
    }

    @Override
    protected String getActivityTitle() {
        // Return the farm name as the activity title
        return farm != null ? farm.getName() : "Detalles de Granja";
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: Handling new intent");
        setIntent(intent);
        
        // Check if we need to update farm information
        if (intent.hasExtra(EXTRA_FARM)) {
            farm = intent.getParcelableExtra(EXTRA_FARM, Farm.class);
            if (farm != null) {
                Log.d(TAG, "Farm updated from new intent: " + farm.getName());
                setActivityTitle(farm.getName());
                loadFragment(FarmDetailsFragment.newInstance(farm), false);
            }
        }
    }
}

