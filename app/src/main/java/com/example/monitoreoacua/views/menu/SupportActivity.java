package com.example.monitoreoacua.views.menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.monitoreoacua.fragments.SupportFragment;
import com.example.monitoreoacua.views.BaseActivity;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.menu.LogoutActivity;

/**
 * Activity for displaying the support options.
 * Extends BaseActivity to use common functionality.
 */
public class SupportActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActivityTitle() {
        return "Soporte";
    }

    @Override
    protected void loadInitialFragment() {
        SupportFragment supportFragment = SupportFragment.newInstance();
        loadFragment(supportFragment, false);
    }

    @Override
    public void navigateToProfile() {
    }

    @Override
    public void navigateToHome() {
        Intent intent = new Intent(this, ListFarmsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void logout() {
        Intent intent = new Intent(this, LogoutActivity.class);
        startActivity(intent);
    }
}
