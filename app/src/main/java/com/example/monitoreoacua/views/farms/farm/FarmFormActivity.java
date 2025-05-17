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
import com.example.monitoreoacua.views.menu.LogoutActivity;
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
    private static final String EXTRA_FARM = "extra_farm";
    
    private TextView textViewTitle;
    private EditText editTextName, editTextAddress, editTextLatitude, editTextLongitude;
    private Button buttonSaveFarm;
    
    private AppCompatImageButton navHome, navSettings, navProfile, navCloseSesion;
    
    private Farm farmToEdit;
    
    private boolean isEditMode = false;
    
    private BaseRequest baseRequest = new BaseRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_form);
        
        initViews();
        
        setupNavigationBar();
        
        checkEditMode();
        
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
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(FarmFormActivity.this, ListFarmsActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FarmFormActivity.this, SupportActivity.class);
            startActivity(intent);
        });
        
        navCloseSesion.setOnClickListener(v -> {
            Intent intent = new Intent(FarmFormActivity.this, LogoutActivity.class);
            startActivity(intent);
        });
    }
    

    private void checkEditMode() {
        // Check for farm object in the intent
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            farmToEdit = getIntent().getParcelableExtra(EXTRA_FARM, Farm.class);
        } else {
            farmToEdit = getIntent().getParcelableExtra(EXTRA_FARM);
        }
        
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
    
    private void populateFormWithFarmData() {
        editTextName.setText(farmToEdit.getName());
        editTextAddress.setText(farmToEdit.getAddress());
        editTextLatitude.setText(farmToEdit.getLatitude());
        editTextLongitude.setText(farmToEdit.getLongitude());
    }
    

    private boolean validateForm() {
        boolean isValid = true;
        
        if (TextUtils.isEmpty(editTextName.getText())) {
            editTextName.setError("El nombre es requerido");
            isValid = false;
        }
        
        if (TextUtils.isEmpty(editTextAddress.getText())) {
            editTextAddress.setError("La dirección es requerida");
            isValid = false;
        }
        
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
    

    private void saveFarm() {
        if (!validateForm()) {
            return;
        }
        
        String name = editTextName.getText().toString();
        String address = editTextAddress.getText().toString();
        String latitude = editTextLatitude.getText().toString();
        String longitude = editTextLongitude.getText().toString();
        
        buttonSaveFarm.setEnabled(false);
        buttonSaveFarm.setText("Guardando...");
        
        ApiFarmsService service = ApiClient.getClient().create(ApiFarmsService.class);
        
        baseRequest.setRequiresAuthentication(true);
        String authToken = "Bearer " + baseRequest.getAuthToken();
        

    }
}

