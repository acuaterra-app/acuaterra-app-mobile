package com.example.monitoreoacua.views.farms;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;//de la nav
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.request.ListFarmsRequest;
import com.example.monitoreoacua.service.response.ListFarmResponse;
import com.example.monitoreoacua.views.menu.ClosesectionActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;
import com.example.monitoreoacua.views.farms.farm.FarmDetailsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFarmsActivity extends AppCompatActivity {

    private TextView textViewFarms;
    private RecyclerView recyclerViewFarms;
    private FarmAdapter farmAdapter;
    private EditText editTextSearchFarm;
    private AppCompatImageButton buttonSortByDate;
    private List<Farm> farmsList = new ArrayList<>();
    private boolean isAscending = true;

    private static final String TAG = "ListFarmsActivity";

    // Declaration of navigation bar elements
    private AppCompatImageButton navHome, navSettings, navProfile, navCloseSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_farms);

        textViewFarms = findViewById(R.id.textViewFarms);
        recyclerViewFarms = findViewById(R.id.recyclerViewFarms);
        editTextSearchFarm = findViewById(R.id.editTextSearchFarm);
        buttonSortByDate = findViewById(R.id.buttonSortByDate);

        // Initialize navigation bar elements
        navHome = findViewById(R.id.navHome);
        // navSettings = findViewById(R.id.navSettings);
        navProfile = findViewById(R.id.navProfile);
        navCloseSesion = findViewById(R.id.navCloseSesion);

        // RecyclerView setup
        recyclerViewFarms.setLayoutManager(new LinearLayoutManager(this));
        farmAdapter = new FarmAdapter();
        recyclerViewFarms.setAdapter(farmAdapter);

        // Fetch farm data from the API
        fetchFarms();

        // Listener for clicking on a farm
        farmAdapter.setOnFarmClickListener(farm -> {
            Intent intent = new Intent(ListFarmsActivity.this, FarmDetailsActivity.class);
            intent.putExtra("farm", farm);
            startActivity(intent);
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

        // Events for the navigation bar
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ListFarmsActivity.this, ListFarmsActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ListFarmsActivity.this, SupportActivity.class);
            startActivity(intent);
        });

        navCloseSesion.setOnClickListener(v -> {
            Intent intent = new Intent(ListFarmsActivity.this, ClosesectionActivity.class);
            startActivity(intent);
        });
    }

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
                } else {
                    Toast.makeText(ListFarmsActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListFarmResponse> call, @NonNull Throwable t) {
                Toast.makeText(ListFarmsActivity.this, "Error de conexión: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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