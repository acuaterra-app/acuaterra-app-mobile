package com.example.monitoreoacua.views.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.monitoreoacua.fragments.LogoutFragment;
import com.example.monitoreoacua.fragments.LogoutFragment.OnLogoutInteractionListener;
import com.example.monitoreoacua.views.BaseActivity;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.login.LoginActivity;

/**
 * Activity for handling user logout functionality.
 * Extends BaseActivity to maintain app navigation structure and
 * implements OnLogoutInteractionListener to handle fragment interactions.
 */
public class LogoutActivity extends BaseActivity implements OnLogoutInteractionListener {

    private static final String TAG = "LogoutActivity";
    private static final String PREFS_NAME = "user_prefs";
    private static final String TOKEN_KEY = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected String getActivityTitle() {
        return "Cerrar Sesión";
    }


    @Override
    protected void loadInitialFragment() {
        loadFragment(LogoutFragment.newInstance(), false);
    }


    @Override
    public void onLogoutCancelled() {
        Log.d(TAG, "Logout cancelled by user");
        finish();
    }


    @Override
    public void logout() {
        // Already in LogoutActivity, just reload the initial fragment
        loadInitialFragment();
    }

    @Override
    public void navigateToHome() {
        Intent intent = new Intent(this, ListFarmsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish(); // Close the LogoutActivity after navigation
    }

    @Override
    public void navigateToProfile() {
        Intent intent = new Intent(this, SupportActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish(); // Close the LogoutActivity after navigation
    }

    @Override
    public void navigateToSettings() {
        super.navigateToSettings(); // This will call the implementation in BaseActivity to load UserFragment
        finish(); // Close the LogoutActivity after navigation
    }
}

