package com.example.monitoreoacua.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.request.RegisterModuleRequest;
import com.example.monitoreoacua.service.response.RegisterModuleResponse;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterModuleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterModuleFragment extends Fragment {

    private EditText etModuleName, etLocation, etFishType, etFishCount, etGrowthTime, etFishQuantity, etFishAge, etVolumeUnit;
    private MaterialButton btnRegisterModulo, btnCancelar;

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

        etModuleName = view.findViewById(R.id.etModuleName);
        etLocation = view.findViewById(R.id.etLocation);
        etFishType = view.findViewById(R.id.etFishType); // Latitude
        etFishCount = view.findViewById(R.id.etFishCount); // Longitude
        etGrowthTime = view.findViewById(R.id.etGrowthTime); // Fish species
        etFishQuantity = view.findViewById(R.id.etFishQuantity); // Quantity
        etFishAge = view.findViewById(R.id.etFishAge); // Age
        etVolumeUnit = view.findViewById(R.id.etVolumeUnit); // Dimensions

        btnRegisterModulo = view.findViewById(R.id.btnRegisterModulo);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        return view;
    }

    private void registerModule() {
        String moduleName = Objects.requireNonNull(etModuleName.getText()).toString();
        String location = Objects.requireNonNull(etLocation.getText()).toString();
        String latitude = Objects.requireNonNull(etFishType.getText()).toString();
        String longitude = Objects.requireNonNull(etFishCount.getText()).toString();
        String fishSpecies = Objects.requireNonNull(etGrowthTime.getText()).toString();
        String fishQuantity = Objects.requireNonNull(etFishQuantity.getText()).toString();
        String fishAge = Objects.requireNonNull(etFishAge.getText()).toString();
        String moduleDimensions = Objects.requireNonNull(etVolumeUnit.getText()).toString();

        // TODO: Get these values from your business logic
        List<Integer> users = new ArrayList<>();

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
                // Handle success response
                List<RegisterModuleResponse.Data> dataListModule = response.getResponseModuleData();
                if (dataListModule != null && !dataListModule.isEmpty()) {
                    Module registeredModule = dataListModule.get(0).moduleData;
                    User sensorUser = dataListModule.get(0).sensorUser;
                    Toast.makeText(getContext(), "Module registered: " + registeredModule.getName(), Toast.LENGTH_SHORT).show();
                    closeFragment();
                }
            }

            @Override
            public void onFail(Throwable error) {
                // Handle error
                Toast.makeText(getContext(), "Error registering module: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
