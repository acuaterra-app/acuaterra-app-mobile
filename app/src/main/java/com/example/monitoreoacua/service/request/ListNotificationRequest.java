package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiNotificationsService;
import com.example.monitoreoacua.service.response.ListNotificationResponse;

public class ListNotificationRequest extends BaseRequest {

    private static final String TAG = "ListNotificationRequest";
    public ListNotificationRequest() {
        super();
        setRequiresAuthentication(true);
    }

    public void fetchTotalNotifications(OnApiRequestCallback<Integer, String> callback, int page, String state, int limit) {
         fetchNotifications(new OnApiRequestCallback<ListNotificationResponse, Throwable>() {
            @Override
            public void onSuccess(ListNotificationResponse apiResponse) {
                // Log the response for debugging
                Log.d(TAG, "Received notification response successfully");

                    int unreadNotificationsCount = 0;

                    if (apiResponse.getMeta() != null) {
                        unreadNotificationsCount = apiResponse.getMeta().getTotalItems();
                    }

                    Log.d(TAG, "Received notification response" + unreadNotificationsCount);

                    callback.onSuccess(unreadNotificationsCount);

            }

             @Override
             public void onFail(Throwable error) {
                 Log.e(TAG, "Error fetching notifications: " + error);
             }
        }, page, state, limit);
    }

    public void fetchNotifications(OnApiRequestCallback<ListNotificationResponse, Throwable> callback, int page, String state, int limit) {
        ApiNotificationsService notificationsService = ApiClient.getClient().create(ApiNotificationsService.class);

        Log.d(TAG, "Fetching unread notifications...");
        notificationsService.getNotifications(
                getAuthToken(),
                page,
                state,
                limit
        ).enqueue(new Callback<ListNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListNotificationResponse> call, @NonNull Response<ListNotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ListNotificationResponse apiResponse = response.body();
                    callback.onSuccess(apiResponse);
                } else {
                    callback.onFail(new Throwable("Api is not successful "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListNotificationResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error when fetching notifications: " + t.getMessage(), t);
                callback.onFail(t);
            }
        });
    }
}
