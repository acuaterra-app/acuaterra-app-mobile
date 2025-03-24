package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.response.FarmResponse;
import com.example.monitoreoacua.views.farms.farm.FarmDetailsActivity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Notification handler for farm-related notifications.
 * Implements the NotificationHandler interface.
 */
public class FarmNotificationHandler implements NotificationHandler {
    private static final String TAG = "FarmNotificationHandler";
    private static final String NOTIFICATION_TYPE_FARM = "farm";

    @Override
    public boolean handleNotification(Context context, Map<String, String> data) {
        // Extract farm ID from the data payload
        String farmId = data.get("farmId");
        if (farmId == null) {
            Log.e(TAG, "Farm ID is missing in notification data");
            return false;
        }

        // Get authorization token for API request
        String token = getAuthToken();
        if (token == null) {
            Log.e(TAG, "Failed to get auth token");
            return false;
        }

        // Create API service
        ApiFarmsService service = ApiClient.createService(ApiFarmsService.class);

        // Make API call to get farm details
        Call<FarmResponse> call = service.getFarmById("Bearer " + token, Integer.parseInt(farmId));
        call.enqueue(new Callback<FarmResponse>() {
            @Override
            public void onResponse(Call<FarmResponse> call, Response<FarmResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FarmResponse farmResponse = response.body();
                    Farm farm = farmResponse.getFarm();
                    
                    if (farm != null) {
                        // Create intent to open FarmDetailsActivity
                        Intent intent = new Intent(context, FarmDetailsActivity.class);
                        intent.putExtra("farm", farm);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    } else {
                        Log.e(TAG, "Farm not found in response");
                        showToast(context, "Error: No se encontró información de la granja");
                    }
                } else {
                    Log.e(TAG, "Error response: " + response.code());
                    showToast(context, "Error al obtener detalles de la granja");
                }
            }

            @Override
            public void onFailure(Call<FarmResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                showToast(context, "Error de conexión al obtener detalles de la granja");
            }
        });

        return true;
    }

    @Override
    public boolean supportsNotificationType(String notificationType) {
        return NOTIFICATION_TYPE_FARM.equals(notificationType);
    }

    private String getAuthToken() {
        try {
            return com.example.monitoreoacua.service.request.BaseRequest.getToken();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get auth token", e);
            return null;
        }
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

