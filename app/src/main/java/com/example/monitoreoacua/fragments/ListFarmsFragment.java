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
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
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


    public interface OnFarmSelectedListener {
        void onFarmSelected(Farm farm);
    }

    public ListFarmsFragment() {
        // Required empty public constructor
    }


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
        return inflater.inflate(R.layout.fragment_list_farms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewFarms = view.findViewById(R.id.textViewFarms);
        recyclerViewFarms = view.findViewById(R.id.recyclerViewFarms);
        editTextSearchFarm = view.findViewById(R.id.editTextSearchFarm);
        buttonSortByDate = view.findViewById(R.id.buttonSortByDate);

        recyclerViewFarms.setLayoutManager(new LinearLayoutManager(getContext()));
        farmAdapter = new FarmAdapter();
        recyclerViewFarms.setAdapter(farmAdapter);

        fetchFarms();

        farmAdapter.setOnFarmClickListener(farm -> {
            if (farmSelectedListener != null) {
                farmSelectedListener.onFarmSelected(farm);
            } else {
                Toast.makeText(getContext(), "Error: La actividad no implementa OnFarmSelectedListener", Toast.LENGTH_LONG).show();
            }
        });

        editTextSearchFarm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFarmsByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonSortByDate.setOnClickListener(v -> sortFarmsByDate());
    }

    private void fetchFarms() {
        new ListFarmsRequest().fetchFarms(new OnApiRequestCallback<List<Farm>, Throwable>() {
            @Override
            public void onSuccess(List<Farm> data) {
                if (isAdded()) {
                    textViewFarms.setVisibility(View.GONE);
                    farmAdapter.setFarmList(data);
                }
            }
            @Override
            public void onFail(Throwable t) {
                if (isAdded()) {
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

