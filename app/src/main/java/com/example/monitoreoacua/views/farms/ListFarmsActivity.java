package com.example.monitoreoacua.views.farms;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFarmsActivity extends AppCompatActivity {

    private TextView textViewFarms;
    private RecyclerView recyclerViewFarms;
    private FarmAdapter farmAdapter;

    private static final String TAG = "YourClassName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_farms);
        textViewFarms = findViewById(R.id.textViewFarms);
        recyclerViewFarms = findViewById(R.id.recyclerViewFarms);
        
        // Setup RecyclerView
        recyclerViewFarms.setLayoutManager(new LinearLayoutManager(this));
        farmAdapter = new FarmAdapter();
        recyclerViewFarms.setAdapter(farmAdapter);

        fetchFarms();
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
                final String TAG = "MyAppTag";
                Log.d(TAG, "On response" + response);
                textViewFarms.setVisibility(View.GONE);

                if(response.isSuccessful()){
                    ListFarmResponse listFarmResponse = response.body();
                    List<Farm> farms = listFarmResponse.getAllFarms();

                    String message = listFarmResponse.getMessage();
                    
                    if (farms != null && !farms.isEmpty()) {
                        // Update the RecyclerView with the farm data
                        farmAdapter.setFarmList(farms);
                        
                        // Set up item click listener if needed
                        farmAdapter.setOnFarmClickListener(farm -> {
                            Toast.makeText(ListFarmsActivity.this, 
                                    "Seleccionaste: " + farm.getName(), 
                                    Toast.LENGTH_SHORT).show();
                            // You can add more actions here when a farm is clicked
                        });
                    } else {
                        Toast.makeText(ListFarmsActivity.this, "No se encontraron granjas", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListFarmResponse> call, Throwable t) {
                Toast.makeText(ListFarmsActivity.this, "Connection error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}