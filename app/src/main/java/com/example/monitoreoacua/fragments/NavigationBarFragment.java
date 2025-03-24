package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.example.monitoreoacua.R;

/**
 * NavigationBarFragment handles the bottom navigation bar functionality.
 * It provides an interface for communication with the hosting activity and
 * manages navigation button clicks.
 */
public class NavigationBarFragment extends Fragment {

    private AppCompatImageButton navHome, navSettings, navProfile, navCloseSesion;
    private NavigationBarListener navigationListener;

    /**
     * Interface for communication with the hosting activity.
     * The hosting activity must implement these methods to handle navigation events.
     */
    public interface NavigationBarListener {
        /**
         * Called when the home navigation button is clicked.
         */
        void navigateToHome();

        /**
         * Called when the settings navigation button is clicked.
         */
        void navigateToSettings();

        /**
         * Called when the profile/support navigation button is clicked.
         */
        void navigateToProfile();

        /**
         * Called when the logout button is clicked.
         */
        void logout();
    }

    /**
     * Factory method to create a new instance of NavigationBarFragment.
     * @return A new instance of NavigationBarFragment
     */
    public static NavigationBarFragment newInstance() {
        return new NavigationBarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize navigation buttons
        initializeNavigationButtons(view);
        
        // Set up click listeners for navigation buttons
        setupNavigationClickListeners();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        
        // Verify that the host activity implements the navigation listener interface
        try {
            navigationListener = (NavigationBarListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + 
                    " must implement NavigationBarListener");
        }
    }

    /**
     * Initializes the navigation buttons from the view.
     * @param view The view containing the navigation buttons
     */
    private void initializeNavigationButtons(View view) {
        navHome = view.findViewById(R.id.navHome);
        navSettings = view.findViewById(R.id.navSettings);
        navProfile = view.findViewById(R.id.navProfile);
        navCloseSesion = view.findViewById(R.id.navCloseSesion);
    }

    /**
     * Sets up click listeners for the navigation buttons.
     * Each button will call the corresponding method on the navigation listener.
     */
    private void setupNavigationClickListeners() {
        navHome.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.navigateToHome();
            }
        });

        navSettings.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.navigateToSettings();
            }
        });

        navProfile.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.navigateToProfile();
            }
        });

        navCloseSesion.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.logout();
            }
        });
    }

    /**
     * Public method to set active navigation item. This can be used by the hosting
     * activity to highlight the current section in the navigation bar.
     * @param navigationItem The ID of the navigation item to highlight
     */
    public void setActiveNavigationItem(int navigationItem) {
        // Reset all buttons to default state
        navHome.setSelected(false);
        navSettings.setSelected(false);
        navProfile.setSelected(false);
        navCloseSesion.setSelected(false);

        // Set the active button using if-else statements instead of switch
        if (navigationItem == R.id.navHome) {
            navHome.setSelected(true);
        } else if (navigationItem == R.id.navSettings) {
            navSettings.setSelected(true);
        } else if (navigationItem == R.id.navProfile) {
            navProfile.setSelected(true);
        } else if (navigationItem == R.id.navCloseSesion) {
            navCloseSesion.setSelected(true);
        }
    }

    /**
     * Sets a custom click listener for a specific navigation button.
     * This allows the hosting activity to override the default behavior for a button.
     * @param buttonId The ID of the button (R.id.navHome, R.id.navSettings, etc.)
     * @param listener The custom click listener to set
     */
    public void setCustomClickListener(int buttonId, View.OnClickListener listener) {
        AppCompatImageButton button = null;
        
        // Use if-else statements instead of switch
        if (buttonId == R.id.navHome) {
            button = navHome;
        } else if (buttonId == R.id.navSettings) {
            button = navSettings;
        } else if (buttonId == R.id.navProfile) {
            button = navProfile;
        } else if (buttonId == R.id.navCloseSesion) {
            button = navCloseSesion;
        }
        
        if (button != null && listener != null) {
            button.setOnClickListener(listener);
        }
    }
}

