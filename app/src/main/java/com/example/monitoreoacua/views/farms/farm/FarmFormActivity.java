package com.example.monitoreoacua.views.farms.farm;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.request.BaseRequest;
import com.example.monitoreoacua.service.response.FarmResponse;
import com.example.monitoreoacua.views.menu.CloseSessionActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class FarmFormActivity extends AppCompatActivity {

    private static final String TAG = "FarmFormActivity";
    
    // UI components
    private TextView textViewTitle;
    private EditText editTextName, editTextAddress, editTextLatitude, editTextLongitude;
    private Button buttonSaveFarm;
    
    // Navigation bar elements
    private AppCompatImageButton navHome, navSettings, navProfile, navCloseSesion;
    
    // Farm object - will be null for new farm creation
    private Farm farmToEdit;
    
    // Mode flag - true for edit mode, false for create mode
    private boolean isEditMode = false;
    
    // Request helper
    private BaseRequest baseRequest = new BaseRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_form);
        
        // Initialize UI components
        initViews();
        
        // Set up navigation bar
        setupNavigationBar();
        
        // Check if we're editing an existing farm
        checkEditMode();
        
        // Set up save button
        buttonSaveFarm.setOnClickListener(v -> saveFarm());
    }
    
    /**
     * Initialize all the views in the layout
     */
    private void initViews() {
        textViewTitle = findViewById(R.id.textViewTitle);
        editTextName = findViewById(R.id.editTextFarmName);
        editTextAddress = findViewById(R.id.editTextFarmAddress);
        editTextLatitude = findViewById(R.id.editTextFarmLatitude);
        editTextLongitude = findViewById(R.id.editTextFarmLongitude);
        buttonSaveFarm = findViewById(R.id.buttonSaveFarm);
        
        // Navigation elements
        navHome = findViewById(R.id.navHome);
        navProfile = findViewById(R.id.navProfile);
        navCloseSesion = findViewById(R.id.navCloseSesion);
    }
    
    /**
     * Set up the navigation bar listeners
     */
    private void setupNavigationBar() {
        // Events for the navigation bar
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(FarmFormActivity.this, ListFarmsActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FarmFormActivity.this, SupportActivity.class);
            startActivity(intent);
        });
        
        navCloseSesion.setOnClickListener(v -> {
            Intent intent = new Intent(FarmFormActivity.this, CloseSessionActivity.class);
            startActivity(intent);
        });
    }
    
    /**
     * Check if we're in edit mode and set up the form accordingly
     */
    private void checkEditMode() {
        // Check for farm object in the intent
        farmToEdit = getIntent().getParcelableExtra("farm");
        
        if (farmToEdit != null) {
            // We're in edit mode
            isEditMode = true;
            textViewTitle.setText("Editar Granja");
            populateFormWithFarmData();
        } else {
            // We're in create mode
            isEditMode = false;
            textViewTitle.setText("Crear Granja");
        }
    }
    
    /**
     * Populate the form fields with the farm data
     */
    private void populateFormWithFarmData() {
        editTextName.setText(farmToEdit.getName());
        editTextAddress.setText(farmToEdit.getAddress());
        editTextLatitude.setText(farmToEdit.getLatitude());
        editTextLongitude.setText(farmToEdit.getLongitude());
    }
    
    /**
     * Validate the form input
     * @return true if valid, false otherwise
     */
    private boolean validateForm() {
        boolean isValid = true;
        
        // Check name
        if (TextUtils.isEmpty(editTextName.getText())) {
            editTextName.setError("El nombre es requerido");
            isValid = false;
        }
        
        // Check address
        if (TextUtils.isEmpty(editTextAddress.getText())) {
            editTextAddress.setError("La dirección es requerida");
            isValid = false;
        }
        
        // Check latitude
        String latitude = editTextLatitude.getText().toString();
        if (TextUtils.isEmpty(latitude)) {
            editTextLatitude.setError("La latitud es requerida");
            isValid = false;
        } else {
            try {
                double lat = Double.parseDouble(latitude);
                if (lat < -90 || lat > 90) {
                    editTextLatitude.setError("La latitud debe estar entre -90 y 90");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                editTextLatitude.setError("La latitud debe ser un número válido");
                isValid = false;
            }
        }
        
        // Check longitude
        String longitude = editTextLongitude.getText().toString();
        if (TextUtils.isEmpty(longitude)) {
            editTextLongitude.setError("La longitud es requerida");
            isValid = false;
        } else {
            try {
                double lng = Double.parseDouble(longitude);
                if (lng < -180 || lng > 180) {
                    editTextLongitude.setError("La longitud debe estar entre -180 y 180");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                editTextLongitude.setError("La longitud debe ser un número válido");
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    /**
     * Save the farm data (create new or update existing)
     */
    private void saveFarm() {
        // Validate the form first
        if (!validateForm()) {
            return;
        }
        
        // Get data from form
        String name = editTextName.getText().toString();
        String address = editTextAddress.getText().toString();
        String latitude = editTextLatitude.getText().toString();
        String longitude = editTextLongitude.getText().toString();
        
        // Show progress or disable button
        buttonSaveFarm.setEnabled(false);
        buttonSaveFarm.setText("Guardando...");
        
        // Get the API service
        ApiFarmsService service = ApiClient.getClient().create(ApiFarmsService.class);
        
        // Get auth token
        baseRequest.setRequiresAuthentication(true);
        String authToken = "Bearer " + baseRequest.getAuthToken();
        
        if (isEditMode) {
            // Update existing farm
            updateFarm(service, authToken, name, address, latitude, longitude);
        } else {
            // Create new farm
            createFarm(service, authToken, name, address, latitude, longitude);
        }
    }
    
    /**
     * Create a new farm with the API
     */
    private void createFarm(ApiFarmsService service, String authToken, 
                            String name, String address, String latitude, String longitude) {
        // Since the API methods don't exist yet, we'll add them to the service interface
        // This is just a placeholder - the actual implementation would use properly defined endpoints
        
        Call<FarmResponse> call = null;
        
        try {
            // Try to use the createFarm method via reflection or other means
            // This is a placeholder for the actual API call
            
            // For now, we'll just simulate success after a brief delay
            Toast.makeText(this, "Creando granja...", Toast.LENGTH_SHORT).show();
            
            // Simulate network delay
            buttonSaveFarm.postDelayed(() -> {
                Toast.makeText(FarmFormActivity.this, 
                    "Granja creada exitosamente (simulado)", Toast.LENGTH_SHORT).show();
                
                // Navigate back to the farms list
                Intent intent = new Intent(FarmFormActivity.this, ListFarmsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }, 1500);
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating farm", e);
            buttonSaveFarm.setEnabled(true);
            buttonSaveFarm.setText("Guardar");
            Toast.makeText(this, "Error al crear la granja: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Update an existing farm with the API
     */
    private void updateFarm(ApiFarmsService service, String authToken, 
                            String name, String address, String latitude, String longitude) {
        // Since the API methods don't exist yet, we'll add them to the service interface
        // This is just a placeholder - the actual implementation would use properly defined endpoints
        
        Call<FarmResponse> call = null;
        
        try {
            // Try to use the updateFarm method via reflection or other means
            // This is a placeholder for the actual API call
            
            // For now, we'll just simulate success after a brief delay
            Toast.makeText(this, "Actualizando granja...", Toast.LENGTH_SHORT).show();
            
            // Simulate network delay
            buttonSaveFarm.postDelayed(() -> {
                Toast.makeText(FarmFormActivity.this, 
                    "Granja actualizada exitosamente (simulado)", Toast.LENGTH_SHORT).show();
                
                // Create a result intent with the updated farm
                Intent resultIntent = new Intent();
                Farm updatedFarm = new Farm(
                    farmToEdit.getId(),
                    name,
                    address,
                    latitude,
                    longitude,
                    farmToEdit.getCreatedAt(),
                    farmToEdit.getUpdatedAt()
                );
                resultIntent.putExtra("farm", updatedFarm);
                
                // Return to the calling activity
                setResult(RESULT_OK, resultIntent);
                finish();
            }, 1500);
            
        } catch (Exception e) {
            Log.e(TAG, "Error updating farm", e);
            buttonSaveFarm.setEnabled(true);
            buttonSaveFarm.setText("Guardar");
            Toast.makeText(this, "Error al actualizar la granja: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Create an extension interface with farm creation and update methods
     * This would be added to ApiFarmsService in a real implementation
     */
    private interface FarmUpdateService {
        @POST("api/v2/owner/farms")
        Call<FarmResponse> createFarm(
            @Header("Authorization") String token,
            @Body FarmData farmData
        );
        
        @PUT("api/v2/owner/farms/{id}")
        Call<FarmResponse> updateFarm(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body FarmData farmData
        );
    }
    
    /**
     * Data class for farm creation/update requests
     */
    private static class FarmData {
        private String name;
        private String address;
        private String latitude;
        private String longitude;
        
        public FarmData(String name, String address, String latitude, String longitude) {
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}

