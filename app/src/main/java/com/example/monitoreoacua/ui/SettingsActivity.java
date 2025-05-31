package com.example.monitoreoacua.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.auth.AuthUser;
import com.example.monitoreoacua.business.utils.RolePermissionHelper;
import com.example.monitoreoacua.utils.SessionManager;

/**
 * Example activity showing how to implement role-based permission checking.
 * This demonstrates restricting access to certain features for monitor users.
 */
public class SettingsActivity extends AppCompatActivity {

    private AuthUser currentUser;
    private Button btnManageUsers;
    private Button btnConfigureSystem;
    private Button btnViewReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get the current logged-in user
        SessionManager sessionManager = SessionManager.getInstance(this);
        currentUser = sessionManager.getUser();

        // Initialize UI components
        initializeViews();
        setupListeners();
        
        // Apply permission restrictions
        applyPermissionRestrictions();
    }

    private void initializeViews() {
        btnManageUsers = findViewById(R.id.btn_manage_users);
        btnConfigureSystem = findViewById(R.id.btn_configure_system);
        btnViewReports = findViewById(R.id.btn_view_reports);
    }

    private void setupListeners() {
        btnManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check permission before action
                if (RolePermissionHelper.checkPermissionAndNotify(
                        SettingsActivity.this, currentUser, "manage_users")) {
                    // Permission granted, show success message
                    Toast.makeText(SettingsActivity.this, 
                                  "Accediendo a Administración de Usuarios", 
                                  Toast.LENGTH_SHORT).show();
                }
                // If permission denied, checkPermissionAndNotify will show the restriction message
            }
        });

        btnConfigureSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alternative way: checking permission manually
                if (RolePermissionHelper.hasPermission(currentUser, "manage_settings")) {
                    Toast.makeText(SettingsActivity.this, 
                                  "Accediendo a Configuración del Sistema", 
                                  Toast.LENGTH_SHORT).show();
                } else {
                    RolePermissionHelper.showRestrictionMessage(SettingsActivity.this);
                }
            }
        });

        btnViewReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This function is allowed for monitors, so check is just for demonstration
                if (RolePermissionHelper.checkPermissionAndNotify(
                        SettingsActivity.this, currentUser, "view_reports")) {
                    Toast.makeText(SettingsActivity.this, 
                                  "Accediendo a Reportes", 
                                  Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Apply permission restrictions by hiding or disabling UI elements
     * that monitor users shouldn't access.
     */
    private void applyPermissionRestrictions() {
        // You can optionally hide or disable buttons for restricted features
        if (RolePermissionHelper.isMonitor(currentUser)) {
            // Option 1: Hide buttons for features not available to monitors
            btnManageUsers.setVisibility(View.GONE);
            
            // Option 2: Disable buttons but keep them visible with visual indication
            btnConfigureSystem.setEnabled(false);
            btnConfigureSystem.setAlpha(0.5f); // Make it look disabled
        }
    }
}

