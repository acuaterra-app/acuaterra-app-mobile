package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Map;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
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
    public void handle(Context context, Notification notification) {
        try {
            String farmId = String.valueOf(notification.getData().getMetaData().get("farmId"));
            
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
        new GetFarmRequest().getFarmById(new OnApiRequestCallback<Farm, Throwable>() {
            @Override
            public void onSuccess(Farm farm) {
                openFarmDetailsActivity(context, farm);
            }

            @Override
            public void onFail(Throwable t) {
                Log.e(TAG, "API call failed", t);
            }
        }, Integer.parseInt(farmId));
    }

    private void openFarmDetailsActivity(Context context, Farm farm) {
        Intent intent = FarmDetailsActivity.createIntent(context, farm);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        Log.d(TAG, "Opening FarmDetailsFragmentActivity for farm ID: " + farm.getId());
    }
}

