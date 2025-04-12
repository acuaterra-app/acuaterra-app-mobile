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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fragment that allows users to register a new module.
 * This fragment provides an interface to input information about the module,
 * validates the inputs, and interacts with the API to register the module.
 */
public class RegisterModuleFragment extends Fragment {

    private EditText etModuleName, etLocation, etLatitude, etLongitude, etFishType, etFishQuantity, etFishAge, etVolumeUnit;
    private MaterialButton btnRegisterModulo, btnCancelar;
    private ProgressBar progressBar;
    private static final String TAG = "RegisterModuleFragment";
    private com.example.monitoreoacua.business.models.Farm farm;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_FARM = "farm";

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
     * @param farm Farm object to be passed to the fragment.
     * @return A new instance of fragment RegisterModuleFragment.
     */
    public static RegisterModuleFragment newInstance(String param1, String param2, com.example.monitoreoacua.business.models.Farm farm) {
        RegisterModuleFragment fragment = new RegisterModuleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putParcelable(ARG_FARM, farm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            // Retrieve farm object from arguments
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                farm = getArguments().getParcelable(ARG_FARM, com.example.monitoreoacua.business.models.Farm.class);
            } else {
                farm = getArguments().getParcelable(ARG_FARM);
            }

            // Log farm information if received
            if (farm != null) {
                Log.d(TAG, "Farm received from arguments: ID = " + farm.getId());
            } else {
                Log.w(TAG, "No Farm object found in arguments!");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_module, container, false);

        // Initialize the input fields and buttons
        etModuleName = view.findViewById(R.id.etModuleName);
        etLocation = view.findViewById(R.id.etLocation);
        etLatitude = view.findViewById(R.id.etLatitude);
        etLongitude = view.findViewById(R.id.etLongitude);
        etFishType = view.findViewById(R.id.etFishType);
        etFishQuantity = view.findViewById(R.id.etFishQuantity);
        etFishAge = view.findViewById(R.id.etFishAge);
        etVolumeUnit = view.findViewById(R.id.etVolumeUnit);

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
     * Validates all input fields before submission.
     * Checks if all fields are filled correctly.
     *
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        boolean isValid = true;

        // Validate Module Name
        if (TextUtils.isEmpty(etModuleName.getText())) {
            etModuleName.setError("Module name is required");
            isValid = false;
        }

        // Validate Location
        if (TextUtils.isEmpty(etLocation.getText())) {
            etLocation.setError("Location is required");
            isValid = false;
        }

        // Validate Latitude
        String latitude = etLatitude.getText().toString().trim();
        if (TextUtils.isEmpty(latitude)) {
            etLatitude.setError("Latitude is required");
            isValid = false;
        } else {
            try {
                double lat = Double.parseDouble(latitude);
                if (lat < -90 || lat > 90) {
                    etLatitude.setError("Latitude must be between -90 and 90");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etLatitude.setError("Latitude must be a valid number");
                isValid = false;
            }
        }

        // Validate Longitude
        String longitude = etLongitude.getText().toString().trim();
        if (TextUtils.isEmpty(longitude)) {
            etLongitude.setError("Longitude is required");
            isValid = false;
        } else {
            try {
                double lon = Double.parseDouble(longitude);
                if (lon < -180 || lon > 180) {
                    etLongitude.setError("Longitude must be between -180 and 180");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etLongitude.setError("Longitude must be a valid number");
                isValid = false;
            }
        }

        // Validate Fish Species
        if (TextUtils.isEmpty(etFishType.getText())) {
            etFishType.setError("Fish species is required");
            isValid = false;
        }

        // Validate Fish Quantity
        String quantity = etFishQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantity)) {
            etFishQuantity.setError("Fish quantity is required");
            isValid = false;
        } else {
            try {
                int qty = Integer.parseInt(quantity);
                if (qty <= 0) {
                    etFishQuantity.setError("Quantity must be greater than zero");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etFishQuantity.setError("Quantity must be an integer");
                isValid = false;
            }
        }

        // Validate Fish Age
        if (TextUtils.isEmpty(etFishAge.getText())) {
            etFishAge.setError("Fish age is required");
            isValid = false;
        }

        // Validate Dimensions
        if (TextUtils.isEmpty(etVolumeUnit.getText())) {
            etVolumeUnit.setError("Dimensions are required");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Registers a new module with the API.
     * Sends the module data to the server and handles the response.
     */
    private void registerModule() {
        // Validate farm and session data first
        if (farm == null) {
            Toast.makeText(getContext(), "Error: Farm information is unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        btnRegisterModulo.setEnabled(false);

        String moduleName = Objects.requireNonNull(etModuleName.getText()).toString().trim();
        String location = Objects.requireNonNull(etLocation.getText()).toString().trim();
        String latitude = Objects.requireNonNull(etLatitude.getText()).toString().trim();
        String longitude = Objects.requireNonNull(etLongitude.getText()).toString().trim();
        String fishSpecies = Objects.requireNonNull(etFishType.getText()).toString().trim();
        String fishQuantity = Objects.requireNonNull(etFishQuantity.getText()).toString().trim();
        String fishAge = Objects.requireNonNull(etFishAge.getText()).toString().trim();
        String moduleDimensions = Objects.requireNonNull(etVolumeUnit.getText()).toString().trim();

        // Get user ID and farm ID
        int idFarm = farm.getId();

        Log.d(TAG, "Farm ID: " + idFarm);

        List<Integer> users = new ArrayList<>();
        users.add(58); //58, 59, 60
        users.add(59);

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
                users
        );

        Log.d(TAG, "Attempting to register module: " + module.toString());

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

                    // Show success message
                    Snackbar.make(
                            requireView(),
                            "Module registered successfully: " + registeredModule.getName(),
                            Snackbar.LENGTH_LONG
                    ).show();

                    // Close the fragment after registration
                    closeFragment();
                } else {
                    // Handle empty response data case
                    Log.w(TAG, "Empty or null response data");
                    Snackbar.make(
                            requireView(),
                            "Error: Empty server response",
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
                        "Error registering module: " + error.getMessage(),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });
    }

    /**
     * Closes the current fragment and returns to the previous screen.
     */
    private void closeFragment() {
        RegisterModuleFragment moduleFragment = new RegisterModuleFragment();
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fragmentContainer, moduleFragment)
                .addToBackStack(null)
                .commit();
    }
}