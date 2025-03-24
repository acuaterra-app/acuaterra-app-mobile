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
        
        // Set up system UI for edge-to-edge display
        setupEdgeToEdgeDisplay();
        
        // Load top bar and navigation bar fragments
        loadTopBarFragment();
        loadNavigationBarFragment();
        
        // Set the title for this activity
        setActivityTitle(getActivityTitle());
        
        // Load the initial fragment if needed
        if (savedInstanceState == null) {
            loadInitialFragment();
        }
    }
    
    /**
     * Sets up edge-to-edge display to handle cutouts, notches, and system navigation bars.
     */
    private void setupEdgeToEdgeDisplay() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    
    /**
     * Loads the NavigationBarFragment into the navigation bar container.
     */
    protected void loadNavigationBarFragment() {
        navigationBarFragment = NavigationBarFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navBarContainer, navigationBarFragment)
                .commit();
    }
    
    /**
     * Loads the TopBarFragment into the top bar container.
     */
    protected void loadTopBarFragment() {
        topBarFragment = TopBarFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.topBarContainer, topBarFragment)
                .commit();
    }
    
    /**
     * Implementation of NavigationBarListener interface.
     * Default navigation to Home/Farms screen.
     * Child activities can override this method to customize behavior.
     */
    @Override
    public void navigateToHome() {
        if (!this.getClass().getSimpleName().equals("ListFarmsActivity")) {
            Intent intent = new Intent(this, ListFarmsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
    /**
     * Implementation of NavigationBarListener interface.
     * Default navigation to Settings screen.
     * Child activities can override this method to customize behavior.
     */
    @Override
    public void navigateToSettings() {
        // This would typically navigate to a Settings or Users Activity
        // For now, just show a toast message
        Toast.makeText(this, "Navigate to Users/Settings (not implemented)", Toast.LENGTH_SHORT).show();
    }

    /**
     * Implementation of NavigationBarListener interface.
     * Default navigation to Profile/Support screen.
     * Child activities can override this method to customize behavior.
     */
    @Override
    public void navigateToProfile() {
        // This would typically navigate to a Profile or Support Activity
        // For now, just show a toast message
        Toast.makeText(this, "Navigate to Support (not implemented)", Toast.LENGTH_SHORT).show();
    }

    /**
     * Implementation of NavigationBarListener interface.
     * Default logout behavior.
     * Child activities can override this method to customize behavior.
     */
    @Override
    public void logout() {
        // This would typically handle logout and navigate to login screen
        // For now, just show a toast message
        Toast.makeText(this, "Logging out (not implemented)", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets the title of the activity.
     * @param title The title to display
     */
    protected void setActivityTitle(String title) {
        if (topBarFragment != null) {
            topBarFragment.setTitle(title);
        }
    }

    /**
     * Loads a fragment into the container.
     * @param fragment The fragment to load
     * @param addToBackStack Whether to add the transaction to the back stack
     */
    protected void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        
        transaction.commit();
    }

    /**
     * Loads the initial fragment for the activity.
     * Child activities should override this method to load their default fragment.
     */
    protected void loadInitialFragment() {
        // Base implementation does nothing
        // Child activities should override this
    }

    /**
     * Gets the title for this activity.
     * Child activities must implement this method to provide their specific title.
     * @return The title for the activity
     */
    protected abstract String getActivityTitle();
}
