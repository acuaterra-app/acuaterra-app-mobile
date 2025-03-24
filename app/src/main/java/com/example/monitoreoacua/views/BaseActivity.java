package com.example.monitoreoacua.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
/**
 * BaseActivity provides common functionality for activities in the application.
 * It handles setting up the shared UI elements like the navigation bar and title,
 * and provides methods for child activities to customize these elements and load fragments.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected TextView textViewActivityTitle;
    protected AppCompatImageButton navHome, navSettings, navProfile, navCloseSesion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        
        // Set up system UI for edge-to-edge display
        setupEdgeToEdgeDisplay();
        
        // Initialize views
        textViewActivityTitle = findViewById(R.id.textViewActivityTitle);
        
        // Set up navigation buttons
        setupNavigationButtons();
        
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
     * Sets up navigation buttons with default behavior.
     * Child activities can override this method to customize navigation behavior.
     */
    protected void setupNavigationButtons() {
        navHome = findViewById(R.id.navHome);
        navSettings = findViewById(R.id.navSettings);
        navProfile = findViewById(R.id.navProfile);
        navCloseSesion = findViewById(R.id.navCloseSesion);
        
        // Set default click listeners
        navHome.setOnClickListener(v -> navigateToHome());
        navSettings.setOnClickListener(v -> navigateToSettings());
        navProfile.setOnClickListener(v -> navigateToProfile());
        navCloseSesion.setOnClickListener(v -> logout());
    }
    
    /**
     * Default navigation to Home/Farms screen.
     * Child activities can override this method to customize behavior.
     */
    protected void navigateToHome() {
        if (!this.getClass().getSimpleName().equals("ListFarmsActivity")) {
            Intent intent = new Intent(this, ListFarmsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
    /**
     * Default navigation to Settings screen.
     * Child activities can override this method to customize behavior.
     */
    protected void navigateToSettings() {
        // This would typically navigate to a Settings or Users Activity
        // For now, just show a toast message
        Toast.makeText(this, "Navigate to Users/Settings (not implemented)", Toast.LENGTH_SHORT).show();
    }

    /**
     * Default navigation to Profile/Support screen.
     * Child activities can override this method to customize behavior.
     */
    protected void navigateToProfile() {
        // This would typically navigate to a Profile or Support Activity
        // For now, just show a toast message
        Toast.makeText(this, "Navigate to Support (not implemented)", Toast.LENGTH_SHORT).show();
    }

    /**
     * Default logout behavior.
     * Child activities can override this method to customize behavior.
     */
    protected void logout() {
        // This would typically handle logout and navigate to login screen
        // For now, just show a toast message
        Toast.makeText(this, "Logging out (not implemented)", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets the title of the activity.
     * @param title The title to display
     */
    protected void setActivityTitle(String title) {
        if (textViewActivityTitle != null) {
            textViewActivityTitle.setText(title);
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
