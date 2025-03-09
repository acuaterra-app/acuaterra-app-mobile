package com.example.monitoreoacua.views.farms;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.request.ListFarmsRequest;
import com.example.monitoreoacua.service.response.ListFarmResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFarmsActivity extends AppCompatActivity {

    private TextView textViewFarms;
    private Button btnLoadFarms;

    private static final String TAG = "YourClassName";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_farms);

        textViewFarms = findViewById(R.id.textViewFarms);
        btnLoadFarms = findViewById(R.id.btnLoadFarms);
        btnLoadFarms.setOnClickListener(v -> fetchFarms());
    }

    /**
     * Fetches farm data from the API and updates the UI.
     */
    private void fetchFarms() {

        ApiFarmsService apiFarmsService = ApiClient.getClient().create(ApiFarmsService.class);
        ListFarmsRequest listFarmsRequest = new ListFarmsRequest();

        apiFarmsService.getFarms(listFarmsRequest.getAuthToken()).enqueue(new Callback<ListFarmResponse>() {
            // Error
            @Override
            public void onResponse(@NonNull Call<ListFarmResponse> call, @NonNull Response<ListFarmResponse> response) {
                final String TAG = "MyAppTag";
                Log.d(TAG, "On response" + response);

                if(response.isSuccessful()){
                    ListFarmResponse listFarmResponse = response.body();
                    Farm farm = listFarmResponse.getFirstFarm();

                    String message = listFarmResponse.getMessage();

                    textViewFarms.setText(message);
                }
            }

            @Override
            public void onFailure(Call<ListFarmResponse> call, Throwable t) {
                Toast.makeText(ListFarmsActivity.this, "Connection error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}