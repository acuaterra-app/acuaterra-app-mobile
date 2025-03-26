package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiNotificationsService;
import com.example.monitoreoacua.service.response.MarkNotificationAsReadResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarkNotificationAsReadRequest extends BaseRequest {

    private static final String TAG = "ListFarmRequestTag";

    public MarkNotificationAsReadRequest() {
        super();
        setRequiresAuthentication(true);
    }

    public void markNotificationAsRead(OnApiRequestCallback<MarkNotificationAsReadResponse, Throwable> callback, int notificationId) {
        ApiNotificationsService notificationsService = ApiClient.getClient().create(ApiNotificationsService.class);
        notificationsService.markNotificationAsRead(getAuthToken(), notificationId).enqueue(new Callback<MarkNotificationAsReadResponse>() {
            @Override
            public void onResponse(@NonNull Call<MarkNotificationAsReadResponse> call, @NonNull Response<MarkNotificationAsReadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MarkNotificationAsReadResponse apiResponse = response.body();
                    callback.onSuccess(apiResponse);
                } else {
                    Log.e(TAG, "Network error when fetching notifications:" + response);
                    callback.onFail(new Throwable("Api is not successful "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarkNotificationAsReadResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error when fetching notifications: " + t.getMessage(), t);
                callback.onFail(t);
            }
        });
    }
}
