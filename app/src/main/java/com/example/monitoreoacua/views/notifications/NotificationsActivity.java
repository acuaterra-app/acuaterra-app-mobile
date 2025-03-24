package com.example.monitoreoacua.views.notifications;

import android.os.Bundle;

import com.example.monitoreoacua.fragments.ListNotificationsFragment;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Activity for displaying the list of notifications
 */
public class NotificationsActivity extends BaseActivity {

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
}

