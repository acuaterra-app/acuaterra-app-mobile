package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.request.ListFarmsRequest;
import com.example.monitoreoacua.service.response.ListFarmResponse;
import com.example.monitoreoacua.views.farms.FarmAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment for displaying and managing the list of farms
 */
public class ListFarmsFragment extends Fragment {

    private TextView textViewFarms;
    private RecyclerView recyclerViewFarms;
    private FarmAdapter farmAdapter;
    private EditText editTextSearchFarm;
    private AppCompatImageButton buttonSortByDate;
    private List<Farm> farmsList = new ArrayList<>();
    private boolean isAscending = true;
    private OnFarmSelectedListener farmSelectedListener;

    private static final String TAG = "ListFarmsFragment";

    /**
     * Interface for communication with host activity when a farm is selected
     */
    public interface OnFarmSelectedListener {
        void onFarmSelected(Farm farm);
    }

    public ListFarmsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     */
    public static ListFarmsFragment newInstance() {
        return new ListFarmsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            farmSelectedListener = (OnFarmSelectedListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "Activity must implement OnFarmSelectedListener", e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_farms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        textViewFarms = view.findViewById(R.id.textViewFarms);
        recyclerViewFarms = view.findViewById(R.id.recyclerViewFarms);
        editTextSearchFarm = view.findViewById(R.id.editTextSearchFarm);
        buttonSortByDate = view.findViewById(R.id.buttonSortByDate);

        // RecyclerView setup
        recyclerViewFarms.setLayoutManager(new LinearLayoutManager(getContext()));
        farmAdapter = new FarmAdapter();
        recyclerViewFarms.setAdapter(farmAdapter);

        // Fetch farm data from the API
        fetchFarms();

        // Listener for clicking on a farm
        farmAdapter.setOnFarmClickListener(farm -> {
            if (farmSelectedListener != null) {
                farmSelectedListener.onFarmSelected(farm);
            } else {
                Toast.makeText(getContext(), "Error: La actividad no implementa OnFarmSelectedListener", Toast.LENGTH_LONG).show();
            }
        });

        // Listener for the search bar
        editTextSearchFarm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFarmsByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Listener for the sorting button
        buttonSortByDate.setOnClickListener(v -> sortFarmsByDate());
    }

    private void fetchFarms() {
        ApiFarmsService apiFarmsService = ApiClient.getClient().create(ApiFarmsService.class);
        ListFarmsRequest listFarmsRequest = new ListFarmsRequest();

        apiFarmsService.getFarms(listFarmsRequest.getAuthToken()).enqueue(new Callback<ListFarmResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListFarmResponse> call, @NonNull Response<ListFarmResponse> response) {
                Log.d(TAG, "On response: " + response);
                
                if (isAdded()) { // Check if fragment is still attached to activity
                    textViewFarms.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        ListFarmResponse listFarmResponse = response.body();
                        List<Farm> farms = listFarmResponse.getAllFarms();

                        if (farms != null && !farms.isEmpty()) {
                            farmsList = new ArrayList<>(farms);
                            farmAdapter.setFarmList(farmsList);
                        } else {
                            Toast.makeText(getContext(), "No se encontraron granjas", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error en la respuesta del servidor", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListFarmResponse> call, @NonNull Throwable t) {
                if (isAdded()) { // Check if fragment is still attached to activity
                    Toast.makeText(getContext(), "Error de conexión: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void filterFarmsByName(String query) {
        List<Farm> filteredFarms = new ArrayList<>();
        for (Farm farm : farmsList) {
            if (farm.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredFarms.add(farm);
            }
        }
        farmAdapter.setFarmList(filteredFarms);
    }

    private void sortFarmsByDate() {
        if (farmsList.isEmpty()) return;

        Collections.sort(farmsList, (f1, f2) -> isAscending
                ? f1.getCreatedAt().compareTo(f2.getCreatedAt())
                : f2.getCreatedAt().compareTo(f1.getCreatedAt()));

        isAscending = !isAscending;
        farmAdapter.setFarmList(farmsList);
    }
}

