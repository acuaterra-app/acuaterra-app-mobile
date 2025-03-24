package com.example.monitoreoacua.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.firebase.NotificationManager;
import com.example.monitoreoacua.fragments.NavigationBarFragment;
import com.example.monitoreoacua.fragments.NavigationBarFragment.NavigationBarListener;
import com.example.monitoreoacua.fragments.TopBarFragment;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiNotificationsService;
import com.example.monitoreoacua.service.request.ListNotificationRequest;
import com.example.monitoreoacua.service.response.ListNotificationResponse;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.menu.LogoutActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;

/**
 * BaseActivity provides common functionality for activities in the application.
 * It handles setting up the shared UI elements like the navigation bar and title,
 * notification processing, and provides methods for child activities to customize
 * these elements and load fragments.
 */
public abstract class BaseActivity extends AppCompatActivity implements NavigationBarListener {

    private static final String TAG = "BaseActivity";
    
    protected TopBarFragment topBarFragment;
    protected NavigationBarFragment navigationBarFragment;
    protected int unreadNotificationsCount = 0;
    protected ApiNotificationsService notificationsService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + getClass().getSimpleName());
        setContentView(R.layout.activity_base);
        
        setupEdgeToEdgeDisplay();
        
        loadTopBarFragment();
        loadNavigationBarFragment();
        
        // Initialize notification service
        notificationsService = ApiClient.getClient().create(ApiNotificationsService.class);
        
        setActivityTitle(getActivityTitle());
        
        if (savedInstanceState == null) {
            loadInitialFragment();
        }
        
        // Process any notification intents that started this activity
        processNotificationIntent(getIntent());
        
        // Fetch notifications when activity is created
        fetchNotifications();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        // Process any notification intent that arrived while the activity was already running
        processNotificationIntent(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + getClass().getSimpleName());
        
        // Fetch notifications when activity resumes to ensure the badge is always up-to-date
        fetchNotifications();
    }
    
    /**
     * Process the notification intent that may have started this activity
     */
    protected void processNotificationIntent(Intent intent) {
        if (intent != null) {
            boolean wasNotification = NotificationManager.getInstance().processNotificationIntent(this, intent);
            if (wasNotification) {
                Log.d(TAG, "Processed notification intent");
                // Fetch notifications when a new notification is received
                fetchNotifications();
            }
        }
    }


    /**
     * Fetches unread notifications from the API and updates the notification badge
     * with the count. Uses a robust approach with multiple fallbacks to ensure
     * the notification badge is always displayed correctly.
     */
    protected void fetchNotifications() {
        if (notificationsService == null) {
            notificationsService = ApiClient.getClient().create(ApiNotificationsService.class);
        }
        
        ListNotificationRequest listNotificationRequest = new ListNotificationRequest();
        String authToken = listNotificationRequest.getAuthToken();
        
        // Use a reasonable limit to avoid excessive data transfer
        final int NOTIFICATION_LIMIT = 50;
        
        Log.d(TAG, "Fetching unread notifications...");

        // Use the enhanced method with more parameters for better control
        notificationsService.getNotificationsByState(
                authToken, 
                1, 
                "unread", 
                null,  // No search filter
                NOTIFICATION_LIMIT  // Limit the result set
        ).enqueue(new Callback<ListNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListNotificationResponse> call, @NonNull Response<ListNotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ListNotificationResponse apiResponse = response.body();
                    int previousCount = unreadNotificationsCount; // Store the previous count for comparison
                    
                    // Log the response for debugging
                    Log.d(TAG, "Received notification response successfully");
                    
                    try {
                        boolean countUpdated = false;
                        
                        // Method 1: Try to get count from meta.pagination.total
                        if (apiResponse.getMeta() != null && apiResponse.getMeta().getPagination() != null) {
                            // Since total is a primitive int, we don't need to check for null
                            int count = apiResponse.getMeta().getPagination().getTotal();
                            unreadNotificationsCount = count;
                            countUpdated = true;
                            Log.d(TAG, "Method 1: Got unread count from meta.pagination.total: " + count);
                        }
                        
                        // Method 2: If meta/pagination is missing, count the notifications in the data array
                        if (!countUpdated && apiResponse.getData() != null) {
                            List<Notification> notifications = apiResponse.getData();
                            unreadNotificationsCount = notifications.size();
                            countUpdated = true;
                            Log.d(TAG, "Method 2: Counted unread notifications from data: " + unreadNotificationsCount);
                            
                            // If we get less than the limit, we can trust this count
                            // Otherwise, we should indicate that there might be more
                            if (notifications.size() >= NOTIFICATION_LIMIT) {
                                Log.w(TAG, "Notification count might be incomplete (reached limit)");
                            }
                        }
                        
                        // Method 3: If both methods failed, keep the previous count but log a warning
                        if (!countUpdated) {
                            Log.w(TAG, "Could not determine unread notification count, keeping previous count: " + previousCount);
                        }
                        
                        // Log if the count changed
                        if (previousCount != unreadNotificationsCount) {
                            Log.d(TAG, "Notification count changed: " + previousCount + " -> " + unreadNotificationsCount);
                        }
                        
                        // Always update the badge to ensure UI is consistent
                        updateNotificationBadge();
                        
                    } catch (Exception e) {
                        // Catch any unexpected exceptions when processing the response
                        Log.e(TAG, "Exception while processing notification response: " + e.getMessage(), e);
                        // Keep the previous count to avoid resetting the badge incorrectly
                    }
                } else {
                    int statusCode = response.code();
                    String errorMessage = response.message();
                    Log.e(TAG, "Failed to fetch notifications. Status code: " + statusCode + ", Message: " + errorMessage);
                    
                    // If error response body exists, log it for debugging
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                    
                    // Despite the error, we should still update the badge with the last known count
                    // This prevents the badge from disappearing due to temporary network issues
                    updateNotificationBadge();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListNotificationResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error when fetching notifications: " + t.getMessage(), t);
                
                // Despite the network failure, we should still update the badge with the last known count
                // to prevent the badge from disappearing due to temporary network issues
                updateNotificationBadge();
            }
        });
    }
    
    /**
     * Updates the notification badge in the TopBarFragment
     */
    protected void updateNotificationBadge() {
        if (topBarFragment != null) {
            runOnUiThread(() -> {
                try {
                    // Ensure notification count is not negative
                    int badgeCount = Math.max(0, unreadNotificationsCount);
                    Log.d(TAG, "Updating notification badge with count: " + badgeCount);
                    topBarFragment.updateNotificationBadge(badgeCount);
                } catch (Exception e) {
                    Log.e(TAG, "Error updating notification badge: " + e.getMessage(), e);
                }
            });
        } else {
            Log.w(TAG, "Cannot update notification badge: topBarFragment is null");
        }
    }

    private void setupEdgeToEdgeDisplay() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    
    protected void loadNavigationBarFragment() {
        navigationBarFragment = NavigationBarFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navBarContainer, navigationBarFragment)
                .commit();
    }
    
    protected void loadTopBarFragment() {
        topBarFragment = TopBarFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.topBarContainer, topBarFragment)
                .commit();
    }

    @Override
    public void navigateToHome() {
        if (this.getClass().getSimpleName().equals("ListFarmsActivity")) {
            // If already in ListFarmsActivity, reload the initial fragment (list of farms)
            loadInitialFragment();
        } else {
            // Navigate to the ListFarmsActivity
            Intent intent = new Intent(this, ListFarmsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void navigateToSettings() {
        // This would typically navigate to a Settings or Users Activity
        // For now, just show a toast message
        Toast.makeText(this, "Navigate to Users/Settings (not implemented)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToProfile() {
        if (this.getClass().getSimpleName().equals("SupportActivity")) {
            // If already in SupportActivity, reload the initial fragment
            loadInitialFragment();
        } else {
            // Navigate to the SupportActivity
            Intent intent = new Intent(this, SupportActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void logout() {
        // Navigate to LogoutActivity when the logout button is pressed
        Intent intent = new Intent(this, LogoutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void setActivityTitle(String title) {
        if (topBarFragment != null) {
            topBarFragment.setTitle(title);
        }
    }

    protected void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        
        transaction.commit();
    }

    protected void loadInitialFragment() {
        // Base implementation does nothing
        // Child activities should override this
    }

    protected abstract String getActivityTitle();
}
