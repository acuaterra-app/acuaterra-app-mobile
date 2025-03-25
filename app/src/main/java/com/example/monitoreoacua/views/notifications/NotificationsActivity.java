package com.example.monitoreoacua.views.notifications;

import android.os.Bundle;
import android.util.Log;

import com.example.monitoreoacua.fragments.ListNotificationsFragment;
import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Activity for displaying the list of notifications
 */
public class NotificationsActivity extends BaseActivity implements ListNotificationsFragment.OnNotificationSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActivityTitle() {
        return "Notifications";
    }

    @Override
    protected void loadInitialFragment() {
        loadFragment(new ListNotificationsFragment(), false);
    }

    @Override
    public void onNotificationSelected(Notification notification) {
        // Handle the notification selection
        Log.d("NotificationsActivity", "Notification selected: " + notification.toString());
        // Add additional handling as needed
    }
}
