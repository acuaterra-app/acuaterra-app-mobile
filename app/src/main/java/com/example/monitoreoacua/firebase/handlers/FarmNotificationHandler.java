package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Map;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.request.BaseRequest;
import com.example.monitoreoacua.service.response.FarmResponse;
import com.example.monitoreoacua.views.farms.farm.FarmDetailsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Notification handler for farm-related notifications.
 * This class handles fetching farm details from the API and opening the FarmDetailsActivity.
 */
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
            // Extract the farm ID from the notification data
            String farmId = data.get("farmId");
            
            if (farmId == null || farmId.isEmpty()) {
                Log.e(TAG, "Farm ID is missing in the notification payload");
                return;
            }
            
            Log.d(TAG, "Fetching details for farm ID: " + farmId);
            
            // Get the auth token for API call
            String token = getAuthToken();
            
            // Make API call to fetch farm details
            fetchFarmDetails(context, farmId, token);
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling farm notification", e);
        }
    }
    
    /**
     * Fetches farm details from the API using the farm ID.
     * 
     * @param farmId The ID of the farm to fetch
     * @param token The authentication token for the API call
     */
    private void fetchFarmDetails(Context context, String farmId, String token) {
        ApiFarmsService farmsService = ApiClient.getClient().create(ApiFarmsService.class);
        
        try {
            int farmIdInt = Integer.parseInt(farmId);
            Call<FarmResponse> call = farmsService.getFarmById(token, farmIdInt);
            
            call.enqueue(new Callback<FarmResponse>() {
                @Override
                public void onResponse(@NonNull Call<FarmResponse> call, @NonNull Response<FarmResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Farm farm = response.body().getFarm();
                        if (farm != null) {
                            openFarmDetailsActivity(context, farm);
                        } else {
                            Log.e(TAG, "Farm data is null in the response");
                        }
                    } else {
                        Log.e(TAG, "Error fetching farm details: " + response.code());
                    }
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
    
    /**
     * Opens the FarmDetailsActivity with the farm details.
     * 
     * @param farm The farm details to display
     */
    private void openFarmDetailsActivity(Context context, Farm farm) {
        Intent intent = new Intent(context, FarmDetailsActivity.class);
        intent.putExtra("farm", farm);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        Log.d(TAG, "Opening FarmDetailsActivity for farm ID: " + farm.getId());
    }
    
    /**
     * Gets the authentication token for API calls.
     * 
     * @return The authentication token
     */
    private String getAuthToken() {
        BaseRequest baseRequest = new BaseRequest();
        return baseRequest.getAuthToken();
    }
}

