package com.example.monitoreoacua.views.farms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.monitoreoacua.views.farms.farm.FarmFormActivity;
import com.example.monitoreoacua.views.farms.farm.modules.ListModulesActivity;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.fragments.FarmDetailsFragment;
import com.example.monitoreoacua.interfaces.OnFragmentInteractionListener;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.response.FarmResponse;
import com.example.monitoreoacua.views.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity that hosts the FarmDetailsFragment
 */
public class FarmDetailsActivity extends BaseActivity implements OnFragmentInteractionListener {

    private static final String EXTRA_FARM = "extra_farm";
    private Farm farm;
    private static final String TAG = "FarmDetailsFragment";

    /**
     * Create an intent to start this activity
     *
     * @param context The context to create the intent from
     * @param farm The farm to display details for
     * @return The intent to start this activity
     */
    public static Intent createIntent(@NonNull Context context, @NonNull Farm farm) {
        Intent intent = new Intent(context, FarmDetailsActivity.class);
        intent.putExtra(EXTRA_FARM, farm);
        return intent;
    }
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get farm from intent
        if (getIntent() != null && getIntent().hasExtra(EXTRA_FARM)) {
            farm = getIntent().getParcelableExtra(EXTRA_FARM, Farm.class);
            initializeWithFarm();
        } else {
            // No farm provided
            Toast.makeText(this, "No se proporcionó información de la granja", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    /**
     * Initialize the activity with the provided farm object
     */
    private void initializeWithFarm() {
        if (farm == null) {
            finish();
            return;
        }

        // Set the title based on the farm name
        setTitle(farm.getName());
        
        // Load the initial fragment with farm details
        loadInitialFragment();
    }
    

    @Override
    protected void loadInitialFragment() {
        // Create and load the farm details fragment
        loadFragment(FarmDetailsFragment.newInstance(farm), false);
    }

    @Override
    public void onEditFarm(Farm farm) {
        // Handle edit farm action
        Intent intent = new Intent(this, FarmFormActivity.class);
        intent.putExtra("extra_farm", farm);
        startActivity(intent);
    }

    @Override
    public void onDeleteFarm(Farm farm) {
        // Handle delete farm action
        // Show confirmation dialog and handle deletion
    }
    
    @Override
    public void onFarmSelected(Farm farm) {
        // Handle farm selection - typically navigate to the farm's details
        // Since we're already in the farm details activity, we might update the UI
        // or reload the fragment with the new farm data
        this.farm = farm;
        setTitle(farm.getName());
        loadFragment(FarmDetailsFragment.newInstance(farm), false);
    }

    @Override
    public void onNavigateBack() {
        // Handle back navigation from fragment
        onBackPressed();
    }

    @Override
    public void onViewFarmModules(Farm farm) {
        // Handle navigation to the farm modules screen
        // Launch ListModulesActivity with the farm object
        Intent intent = new Intent(this, ListModulesActivity.class);
        intent.putExtra("farm", farm);
        startActivity(intent);
    }
    @Override
    protected String getActivityTitle() {
        // Return the farm name as the activity title
        return farm != null ? farm.getName() : "Farm Details";
    }
}
