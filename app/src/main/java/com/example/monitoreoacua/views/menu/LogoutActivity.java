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
        // BaseActivity already handles setting the content view and loading fragments
    }

    /**
     * Returns the title for this activity.
     * @return The title for the activity
     */
    @Override
    protected String getActivityTitle() {
        return "Cerrar Sesión";
    }

    /**
     * Loads the initial fragment for the activity.
     * In this case, loads the LogoutFragment.
     */
    @Override
    protected void loadInitialFragment() {
        loadFragment(LogoutFragment.newInstance(), false);
    }

    /**
     * Handles the case when the user cancels the logout action.
     * Implemented from OnLogoutInteractionListener.
     */
    @Override
    public void onLogoutCancelled() {
        Log.d(TAG, "Logout cancelled by user");
        finish();
    }

    /**
     * Override the default logout behavior in BaseActivity.
     * This method will be called when the user clicks the logout button in the navigation bar.
     * We redirect to this activity itself if we're not already in it.
     */
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

