package com.example.monitoreoacua.views.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.monitoreoacua.fragments.LogoutFragment;
import com.example.monitoreoacua.fragments.LogoutFragment.OnLogoutInteractionListener;
import com.example.monitoreoacua.views.BaseActivity;
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
        if (!this.getClass().getSimpleName().equals("LogoutActivity")) {
            Intent intent = new Intent(this, LogoutActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // We're already in LogoutActivity, no need to do anything
            // The fragment is already showing the logout confirmation UI
        }
    }
}

