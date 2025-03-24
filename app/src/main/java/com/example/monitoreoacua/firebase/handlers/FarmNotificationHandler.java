package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Map;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.request.GetFarmRequest;
import com.example.monitoreoacua.service.response.FarmResponse;
import com.example.monitoreoacua.views.farms.FarmDetailsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmNotificationHandler implements NotificationHandler {

    private static final String TAG = "FarmNotificationHandler";
    private static final String NOTIFICATION_TYPE = "farm";

    public FarmNotificationHandler() {
        // Empty constructor
    }

    @Override
    public boolean canHandle(String notificationType) {
        return NOTIFICATION_TYPE.equals(notificationType);
    }

    @Override
    public void handle(Context context, Map<String, String> data) {
        try {
            String farmId = data.get("farmId");
            
            if (farmId == null || farmId.isEmpty()) {
                Log.e(TAG, "Farm ID is missing in the notification payload");
                return;
            }
            
            Log.d(TAG, "Fetching details for farm ID: " + farmId);

            
            fetchFarmDetails(context, farmId);
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling farm notification", e);
        }
    }

    private void fetchFarmDetails(Context context, String farmId) {
        ApiFarmsService farmsService = ApiClient.getClient().create(ApiFarmsService.class);
        GetFarmRequest getFarmRequest = new GetFarmRequest();

        try {
            int farmIdInt = Integer.parseInt(farmId);
            Call<FarmResponse> call = farmsService.getFarmById(getFarmRequest.getAuthToken(), farmIdInt);
            
            call.enqueue(new Callback<FarmResponse>() {
                @Override
                public void onResponse(@NonNull Call<FarmResponse> call, @NonNull Response<FarmResponse> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Log.e(TAG, "Error fetching farm details: " + response);
                        return;
                    }

                    Farm farm = response.body().getFarm();
                    if (farm == null) {
                        Log.e(TAG, "Farm data is null in the response");
                        return;
                    }

                    openFarmDetailsActivity(context, farm);
                }
    
                @Override
                public void onFailure(@NonNull Call<FarmResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "API call failed", t);
                }
            });
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid farm ID format: " + farmId, e);
        }
    }

    private void openFarmDetailsActivity(Context context, Farm farm) {
        Intent intent = FarmDetailsActivity.createIntent(context, farm);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        Log.d(TAG, "Opening FarmDetailsFragmentActivity for farm ID: " + farm.getId());
    }
}

