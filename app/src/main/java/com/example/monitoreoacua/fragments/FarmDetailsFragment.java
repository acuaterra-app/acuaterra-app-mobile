package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.interfaces.OnFragmentInteractionListener;
import com.example.monitoreoacua.business.models.Farm;

/**
 * Fragment that displays details of a farm
 */
public class FarmDetailsFragment extends Fragment {
    private static final String EXTRA_FARM = "extra_farm";

    private Farm farm;
    private OnFragmentInteractionListener mListener;

    private TextView tvFarmName;
    private TextView tvFarmAddress;
    private TextView tvFarmCoordinates;
    private TextView tvCreatedAt;
    private Button btnModules;
    private ImageView ivFarmImage;

    public FarmDetailsFragment() {
        // Required empty public constructor
    }


    public static FarmDetailsFragment newInstance(Farm farm) {
        FarmDetailsFragment fragment = new FarmDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FARM, farm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                farm = getArguments().getParcelable(EXTRA_FARM, Farm.class);
            } else {
                farm = getArguments().getParcelable(EXTRA_FARM);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_farm_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        tvFarmName = view.findViewById(R.id.textViewFarmName);
        tvFarmAddress = view.findViewById(R.id.textViewFarmAddress);
        tvFarmCoordinates = view.findViewById(R.id.textViewFarmCoordinates);
        tvCreatedAt = view.findViewById(R.id.textViewCreatedAt);
        btnModules = view.findViewById(R.id.buttonModules);
        ivFarmImage = view.findViewById(R.id.imageViewFarm);
        
        displayFarmDetails();
        
        setupClickListeners();
    }

    private void displayFarmDetails() {
        Log.e("FarmDetailsFragment", "Displaying farm details: " + farm);
        if (farm != null) {
            tvFarmName.setText(farm.getName());
            tvFarmAddress.setText(farm.getAddress());
            tvFarmCoordinates.setText("Lat: " + farm.getLatitude() + ", Lng: " + farm.getLongitude());
            tvCreatedAt.setText(farm.getCreatedAt() != null ? farm.getCreatedAt().toString() : "--");
            
            // TODO: When image functionality is implemented in the Farm model, update this code
            ivFarmImage.setImageResource(R.drawable.farmperfil);
        }
    }

    private void setupClickListeners() {
        btnModules.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onViewFarmModules(farm);
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

