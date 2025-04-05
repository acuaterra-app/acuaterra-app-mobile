package com.example.monitoreoacua.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.request.RegisterModuleRequest;
import com.example.monitoreoacua.service.response.RegisterModuleResponse;
import com.example.monitoreoacua.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterModuleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterModuleFragment extends Fragment {

    private EditText etModuleName, etLocation, etLatitude, etLongitude, etFishSpecies, etFishQuantity, etFishAge, etDimensions;
    private MaterialButton btnRegisterModulo, btnCancelar;
    private ProgressBar progressBar;
    private static final String TAG = "RegisterModuleFragment";
    private SessionManager sessionManager;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public RegisterModuleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterModuleFragment.
     */
    public static RegisterModuleFragment newInstance(String param1, String param2) {
        RegisterModuleFragment fragment = new RegisterModuleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_module, container, false);

        // Initialize session manager
        sessionManager = new SessionManager(requireContext());
        
        // Initialize view elements
        etModuleName = view.findViewById(R.id.etModuleName);
        etLocation = view.findViewById(R.id.etLocation);
        // Fix incorrect field mappings
        etLatitude = view.findViewById(R.id.etFishType);       // This was incorrectly mapped
        etLongitude = view.findViewById(R.id.etFishCount);     // This was incorrectly mapped
        etFishSpecies = view.findViewById(R.id.etGrowthTime);  // This was incorrectly mapped
        etFishQuantity = view.findViewById(R.id.etFishQuantity);
        etFishAge = view.findViewById(R.id.etFishAge);
        etDimensions = view.findViewById(R.id.etVolumeUnit);
        
        progressBar = view.findViewById(R.id.progressBar);
        
        btnRegisterModulo = view.findViewById(R.id.btnRegisterModulo);
        btnCancelar = view.findViewById(R.id.btnCancelar);
        
        // Set up button click listeners
        btnRegisterModulo.setOnClickListener(v -> {
            if (validateInputs()) {
                registerModule();
            }
        });
        
        btnCancelar.setOnClickListener(v -> {
            closeFragment();
        });

        return view;
    }

    /**
     * Validates all input fields before submission
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validate Module Name
        if (TextUtils.isEmpty(etModuleName.getText())) {
            etModuleName.setError("El nombre del módulo es requerido");
            isValid = false;
        }
        
        // Validate Location
        if (TextUtils.isEmpty(etLocation.getText())) {
            etLocation.setError("La ubicación es requerida");
            isValid = false;
        }
        
        // Validate Latitude
        String latitude = etLatitude.getText().toString().trim();
        if (TextUtils.isEmpty(latitude)) {
            etLatitude.setError("La latitud es requerida");
            isValid = false;
        } else {
            try {
                double lat = Double.parseDouble(latitude);
                if (lat < -90 || lat > 90) {
                    etLatitude.setError("La latitud debe estar entre -90 y 90");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etLatitude.setError("La latitud debe ser un número válido");
                isValid = false;
            }
        }
        
        // Validate Longitude
        String longitude = etLongitude.getText().toString().trim();
        if (TextUtils.isEmpty(longitude)) {
            etLongitude.setError("La longitud es requerida");
            isValid = false;
        } else {
            try {
                double lon = Double.parseDouble(longitude);
                if (lon < -180 || lon > 180) {
                    etLongitude.setError("La longitud debe estar entre -180 y 180");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etLongitude.setError("La longitud debe ser un número válido");
                isValid = false;
            }
        }
        
        // Validate Fish Species
        if (TextUtils.isEmpty(etFishSpecies.getText())) {
            etFishSpecies.setError("La especie de pez es requerida");
            isValid = false;
        }
        
        // Validate Fish Quantity
        String quantity = etFishQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantity)) {
            etFishQuantity.setError("La cantidad de peces es requerida");
            isValid = false;
        } else {
            try {
                int qty = Integer.parseInt(quantity);
                if (qty <= 0) {
                    etFishQuantity.setError("La cantidad debe ser mayor a cero");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etFishQuantity.setError("La cantidad debe ser un número entero");
                isValid = false;
            }
        }
        
        // Validate Fish Age
        if (TextUtils.isEmpty(etFishAge.getText())) {
            etFishAge.setError("La edad de los peces es requerida");
            isValid = false;
        }
        
        // Validate Dimensions
        if (TextUtils.isEmpty(etDimensions.getText())) {
            etDimensions.setError("Las dimensiones son requeridas");
            isValid = false;
        }
        
        return isValid;
    }

    /**
     * Registers a new module with the API
     */
    private void registerModule() {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        btnRegisterModulo.setEnabled(false);
        
        String moduleName = Objects.requireNonNull(etModuleName.getText()).toString().trim();
        String location = Objects.requireNonNull(etLocation.getText()).toString().trim();
        String latitude = Objects.requireNonNull(etLatitude.getText()).toString().trim();
        String longitude = Objects.requireNonNull(etLongitude.getText()).toString().trim();
        String fishSpecies = Objects.requireNonNull(etFishSpecies.getText()).toString().trim();
        String fishQuantity = Objects.requireNonNull(etFishQuantity.getText()).toString().trim();
        String fishAge = Objects.requireNonNull(etFishAge.getText()).toString().trim();
        String moduleDimensions = Objects.requireNonNull(etDimensions.getText()).toString().trim();

        // Get user ID and farm ID from session manager
        int idFarm = sessionManager.getFarmId();
        int createdByUserId = sessionManager.getUserId();
        
        if (idFarm <= 0 || createdByUserId <= 0) {
            // Fallback to defaults if session data is not available
            Log.w(TAG, "Using default farm/user IDs because session data is unavailable");
            idFarm = 1;
            createdByUserId = 1;
        }
        
        List<Integer> users = new ArrayList<>();
        users.add(createdByUserId);

        // Create the module using the full constructor
        Module module = new Module(
                moduleName,
                location,
                latitude,
                longitude,
                fishSpecies,
                fishQuantity,
                fishAge,
                moduleDimensions,
                idFarm,
                createdByUserId,
                users
        );

        RegisterModuleRequest registerModuleRequest = new RegisterModuleRequest();

        registerModuleRequest.registerModuleRequest(module, new OnApiRequestCallback<RegisterModuleResponse, Throwable>() {
            @Override
            public void onSuccess(RegisterModuleResponse response) {
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);
                btnRegisterModulo.setEnabled(true);
                
                // Handle success response
                List<RegisterModuleResponse.Data> dataListModule = response.getResponseModuleData();
                if (dataListModule != null && !dataListModule.isEmpty()) {
                    Module registeredModule = dataListModule.get(0).moduleData;
                    User sensorUser = dataListModule.get(0).sensorUser;
                    
                    // Log successful registration
                    Log.d(TAG, "Module registered successfully: " + registeredModule.getName());
                    Log.d(TAG, "Sensor user created: " + sensorUser.getEmail());
                    
                    // Save sensor user information if needed
                    // sessionManager.saveSensorUser(sensorUser);
                    
                    // Show success message
                    Snackbar.make(
                            requireView(), 
                            "Módulo registrado correctamente: " + registeredModule.getName(), 
                            Snackbar.LENGTH_LONG
                    ).show();
                    
                    // Navigate to module list
                    closeFragment();
                } else {
                    // Handle empty response data case
                    Log.w(TAG, "Response data is empty or null");
                    Snackbar.make(
                            requireView(),
                            "Error: Respuesta vacía del servidor",
                            Snackbar.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFail(Throwable error) {
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);
                btnRegisterModulo.setEnabled(true);
                
                // Log error
                Log.e(TAG, "Error registering module: " + error.getMessage(), error);
                
                // Show error message
                Snackbar.make(
                        requireView(),
                        "Error al registrar módulo: " + error.getMessage(),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    private void closeFragment() {
        ListModulesFragment listModulesFragment = new ListModulesFragment();
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fragmentContainer, listModulesFragment)
                .addToBackStack(null)
                .commit();
    }
}
