package com.example.monitoreoacua.views.farms;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.request.ListFarmsRequest;
import com.example.monitoreoacua.service.response.ListFarmResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFarmsActivity extends AppCompatActivity {

    private TextView textViewFarms;
    private RecyclerView recyclerViewFarms;
    private FarmAdapter farmAdapter;
    private EditText editTextSearchFarm;
    private Button buttonSortByDate;
    private List<Farm> farmsList = new ArrayList<>();
    private boolean isAscending = true; // Flag to toggle sorting order

    private static final String TAG = "ListFarmsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_farms);

        textViewFarms = findViewById(R.id.textViewFarms);
        recyclerViewFarms = findViewById(R.id.recyclerViewFarms);
        editTextSearchFarm = findViewById(R.id.editTextSearchFarm);
        buttonSortByDate = findViewById(R.id.buttonSortByDate);

        // Setup RecyclerView
        recyclerViewFarms.setLayoutManager(new LinearLayoutManager(this));
        farmAdapter = new FarmAdapter();
        recyclerViewFarms.setAdapter(farmAdapter);

        // Fetch farms data from API
        fetchFarms();

        // Search bar event listener
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

        // Sort button event listener
        buttonSortByDate.setOnClickListener(v -> {
            sortFarmsByDate();
        });
    }

    /**
     * Fetches farm data from the API and updates the UI.
     */
    private void fetchFarms() {
        ApiFarmsService apiFarmsService = ApiClient.getClient().create(ApiFarmsService.class);
        ListFarmsRequest listFarmsRequest = new ListFarmsRequest();

        apiFarmsService.getFarms(listFarmsRequest.getAuthToken()).enqueue(new Callback<ListFarmResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListFarmResponse> call, @NonNull Response<ListFarmResponse> response) {
                Log.d(TAG, "On response: " + response);
                textViewFarms.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    ListFarmResponse listFarmResponse = response.body();
                    List<Farm> farms = listFarmResponse.getAllFarms();

                    if (farms != null && !farms.isEmpty()) {
                        farmsList = new ArrayList<>(farms);
                        farmAdapter.setFarmList(farmsList);
                    } else {
                        Toast.makeText(ListFarmsActivity.this, "No se encontraron granjas", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListFarmResponse> call, @NonNull Throwable t) {
                Toast.makeText(ListFarmsActivity.this, "Connection error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Filters the list of farms based on the entered search term.
     * @param query The search term entered by the user.
     */
    private void filterFarmsByName(String query) {
        List<Farm> filteredFarms = new ArrayList<>();
        for (Farm farm : farmsList) {
            if (farm.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredFarms.add(farm);
            }
        }
        farmAdapter.setFarmList(filteredFarms);
    }

    /**
     * Sorts the list of farms by creation date.
     * Alternates between ascending and descending order.
     */
    private void sortFarmsByDate() {
        if (farmsList.isEmpty()) return;

        Collections.sort(farmsList, new Comparator<Farm>() {
            @Override
            public int compare(Farm f1, Farm f2) {
                return isAscending
                        ? f1.getCreatedAt().compareTo(f2.getCreatedAt())
                        : f2.getCreatedAt().compareTo(f1.getCreatedAt());
            }
        });

        // Toggle sorting order
        isAscending = !isAscending;

        // Update RecyclerView
        farmAdapter.setFarmList(farmsList);
    }
}
