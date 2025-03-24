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


    protected void fetchNotifications() {
        ApiNotificationsService apiNotificationsService = ApiClient.getClient().create(ApiNotificationsService.class);
        ListNotificationRequest listNotificationRequest = new ListNotificationRequest();

        apiNotificationsService.getNotifications(listNotificationRequest.getAuthToken()).enqueue(new Callback<ListNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListNotificationResponse> call, @NonNull Response<ListNotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ListNotificationResponse apiResponse = response.body();
                    List<Notification> notifications = apiResponse.getAllNotification() ;
                    if (notifications != null) {
                        // Count unread notifications
                        int count = 0;
                        for (Notification item : notifications) {
                            if (Objects.equals(item.getData().getState(), "unread")) {
                                count++;
                            }
                        }

                        // Update unread count and UI
                        unreadNotificationsCount = count;
                        updateNotificationBadge();

                        Log.d(TAG, "Fetched notifications: " + count + " unread");
                    }
                } else {
                    Log.e(TAG, "Failed to fetch notifications: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ListNotificationResponse> call, Throwable t) {

            }
        });
    }
    
    /**
     * Updates the notification badge in the TopBarFragment
     */
    protected void updateNotificationBadge() {
        if (topBarFragment != null) {
            runOnUiThread(() -> {
                topBarFragment.updateNotificationBadge(unreadNotificationsCount);
            });
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
