package com.example.monitoreoacua.views.farms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.monitoreoacua.views.farms.farm.FarmFormActivity;
import com.example.monitoreoacua.views.farms.farm.modules.ModulesActivity;

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
    private static final String EXTRA_FARM_ID = "farmId";
    private Farm farm;
    private int farmId;
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
    
    /**
     * Create an intent to start this activity
     *
     * @param context The context to create the intent from
     * @param farmId The ID of the farm to display details for
     * @return The intent to start this activity
     */
    public static Intent createIntent(@NonNull Context context, int farmId) {
        Intent intent = new Intent(context, FarmDetailsActivity.class);
        intent.putExtra(EXTRA_FARM_ID, farmId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get farm or farmId from intent
        if (getIntent() != null) {
            if (getIntent().hasExtra(EXTRA_FARM)) {
                farm = getIntent().getParcelableExtra(EXTRA_FARM, Farm.class);
                initializeWithFarm();
            } else if (getIntent().hasExtra(EXTRA_FARM_ID)) {
                farmId = getIntent().getIntExtra(EXTRA_FARM_ID, -1);
                if (farmId != -1) {
                    // Set a temporary title while loading
                    setTitle("Cargando detalles...");
                    fetchFarmDetails(farmId);
                } else {
                    // Invalid farmId
                    Toast.makeText(this, "ID de granja inválido", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                // No farm or farmId provided
                Toast.makeText(this, "No se proporcionó información de la granja", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
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
    
    /**
     * Fetch farm details from the API using the provided farmId
     * 
     * @param farmId The ID of the farm to fetch details for
     */
    private void fetchFarmDetails(int farmId) {
        // Get auth token from SharedPreferences
        String token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("token", null);
        
        if (token == null) {
            Toast.makeText(this, "No se pudo obtener el token de autenticación", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Create API service
        ApiFarmsService apiFarmsService = ApiClient.getClient().create(ApiFarmsService.class);
        
        // Make API call to get farm details
        apiFarmsService.getFarmById(token, farmId).enqueue(new Callback<FarmResponse>() {
            @Override
            public void onResponse(@NonNull Call<FarmResponse> call, @NonNull Response<FarmResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    farm = response.body().getFarm();
                    if (farm != null) {
                        initializeWithFarm();
                    } else {
                        Toast.makeText(FarmDetailsActivity.this, "No se encontraron detalles para esta granja", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(FarmDetailsActivity.this, "Error al obtener detalles de la granja", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<FarmResponse> call, @NonNull Throwable t) {
                Toast.makeText(FarmDetailsActivity.this, "Error de conexión: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching farm details", t);
                finish();
            }
        });
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
        // Launch ModulesActivity with the farmId parameter
        Intent intent = new Intent(this, ModulesActivity.class);
        intent.putExtra("farmId", farm.getId());
        startActivity(intent);
    }
    
    @Override
    protected String getActivityTitle() {
        // Return the farm name as the activity title
        return farm != null ? farm.getName() : "Farm Details";
    }
}
