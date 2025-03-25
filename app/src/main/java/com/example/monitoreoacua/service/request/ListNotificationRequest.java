package com.example.monitoreoacua.service.request;

import android.util.Log;
import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiNotificationsService;
import com.example.monitoreoacua.service.response.ListNotificationResponse;

public class ListNotificationRequest extends BaseRequest{

    private static final String TAG = "ListNotificationRequest";
    private static final int NOTIFICATION_LIMIT = 10;

    public ListNotificationRequest()
    {
        super();
        setRequiresAuthentication(true);
    }

    /**
     * Interface for notification callback events
     */
    public interface NotificationCallback {
        void onNotificationCountUpdated(int count);
        void onError(String error);
    }

    /**
     * Fetches unread notifications from the API and returns the count through the callback
     * Uses a robust approach with multiple fallbacks to ensure
     * the notification count is always calculated correctly.
     * 
     * @param callback The callback to handle the notification count
     */
    public void fetchNotifications(NotificationCallback callback, int page, String state, int limit)  {
        ApiNotificationsService notificationsService = ApiClient.getClient().create(ApiNotificationsService.class);
        
        Log.d(TAG, "Fetching unread notifications...");

        notificationsService.getNotifications(
                this.getAuthToken(),
                page,
                state,
                limit
        ).enqueue(new Callback<ListNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListNotificationResponse> call, @NonNull Response<ListNotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ListNotificationResponse apiResponse = response.body();
                    
                    // Log the response for debugging
                    Log.d(TAG, "Received notification response successfully");
                    
                    try {
                        int unreadNotificationsCount = 0;
                        
                        // Method 1: Try to get count from meta.pagination.total
                        if (apiResponse.getMeta() != null) {
                            unreadNotificationsCount = apiResponse.getMeta().getTotalItems();
                        }

                        Log.d(TAG, "Received notification response" + unreadNotificationsCount);
                        
                        callback.onNotificationCountUpdated(unreadNotificationsCount);
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Exception while processing notification response: " + e.getMessage(), e);
                        callback.onError("Error processing notifications: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to fetch notifications: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListNotificationResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error when fetching notifications: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
