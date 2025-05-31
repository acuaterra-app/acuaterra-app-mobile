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
import com.example.monitoreoacua.business.models.auth.AuthUser;
import com.example.monitoreoacua.business.utils.RolePermissionHelper;
import com.example.monitoreoacua.utils.SessionManager;

/**
 * NavigationBarFragment handles the bottom navigation bar functionality.
 * It provides an interface for communication with the hosting activity and
 * manages navigation button clicks.
 */
public class NavigationBarFragment extends Fragment {

    private AppCompatImageButton navHome, navSettings, navProfile, navCloseSesion;
    private NavigationBarListener navigationListener;


    public interface NavigationBarListener {

        void navigateToHome();


        void navigateToSettings();


        void navigateToProfile();

        void navigateToMonitorProfile();
        
        void navigateToSupport();

        void logout();
    }


    public static NavigationBarFragment newInstance() {
        return new NavigationBarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeNavigationButtons(view);
        
        // Set up click listeners for navigation buttons
        setupNavigationClickListeners();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        
        try {
            navigationListener = (NavigationBarListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + 
                    " must implement NavigationBarListener");
        }
    }


    private void initializeNavigationButtons(View view) {
        navHome = view.findViewById(R.id.navHome);
        navSettings = view.findViewById(R.id.navSettings);
        navProfile = view.findViewById(R.id.navProfile);
        navCloseSesion = view.findViewById(R.id.navCloseSesion);
    }

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
                navigationListener.navigateToSupport();
            }
        });

        navCloseSesion.setOnClickListener(v -> {
            if (navigationListener != null) {
                navigationListener.logout();
            }
        });
    }


    public void setActiveNavigationItem(int navigationItem) {
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

