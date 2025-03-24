package com.example.monitoreoacua.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.monitoreoacua.fragments.NavigationBarFragment;
import com.example.monitoreoacua.fragments.NavigationBarFragment.NavigationBarListener;
import com.example.monitoreoacua.fragments.TopBarFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;
import com.example.monitoreoacua.views.menu.LogoutActivity;
/**
 * BaseActivity provides common functionality for activities in the application.
 * It handles setting up the shared UI elements like the navigation bar and title,
 * and provides methods for child activities to customize these elements and load fragments.
 */
public abstract class BaseActivity extends AppCompatActivity implements NavigationBarListener {

    protected TopBarFragment topBarFragment;
    protected NavigationBarFragment navigationBarFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        
        setupEdgeToEdgeDisplay();
        
        loadTopBarFragment();
        loadNavigationBarFragment();
        
        setActivityTitle(getActivityTitle());
        
        if (savedInstanceState == null) {
            loadInitialFragment();
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
        Intent intent = new Intent(this, com.example.monitoreoacua.views.menu.LogoutActivity.class);
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
