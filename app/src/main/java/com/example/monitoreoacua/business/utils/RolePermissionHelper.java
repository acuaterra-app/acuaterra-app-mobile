package com.example.monitoreoacua.business.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.monitoreoacua.business.models.auth.AuthUser;

/**
 * Helper class to manage role-based permissions.
 * Provides methods to check if a user has permission for specific features
 * and display appropriate restriction messages.
 */
public class RolePermissionHelper {

    // Role constants
    public static final String ROLE_MONITOR = "monitor";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_OWNER = "owner";

    /**
     * Checks if the user has the monitor role.
     *
     * @param user The authenticated user
     * @return true if the user has the monitor role, false otherwise
     */
    public static boolean isMonitor(AuthUser user) {
        if (user == null) {
            return false;
        }
        return ROLE_MONITOR.equalsIgnoreCase(user.getRole());
    }

    /**
     * Checks if the user has permission to access a specific feature.
     * Monitor users have restricted access to certain features.
     *
     * @param user The authenticated user
     * @param featureKey The key identifying the feature to check
     * @return true if the user has permission, false otherwise
     */
    public static boolean hasPermission(AuthUser user, String featureKey) {
        if (user == null) {
            return false;
        }

        // If user is not a monitor, they have full access
        if (!isMonitor(user)) {
            return true;
        }

        // Define which features a monitor can access
        // Add more feature keys as needed
        switch (featureKey) {
            case "view_dashboard":
            case "view_readings":
            case "view_reports":
                return true;
            case "manage_users":
            case "edit_modules":
            case "delete_data":
            case "manage_settings":
                return false;
            default:
                return false;
        }
    }

    /**
     * Shows a restriction message when a user doesn't have permission to access a feature.
     *
     * @param context The context to display the toast message
     */
    public static void showRestrictionMessage(Context context) {
        Toast.makeText(
                context,
                "Esta función no está habilitada para su rol de monitor",
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * Convenient method to check permission and show restriction message if needed.
     *
     * @param context The context to display the toast message
     * @param user The authenticated user
     * @param featureKey The key identifying the feature to check
     * @return true if the user has permission, false otherwise
     */
    public static boolean checkPermissionAndNotify(Context context, AuthUser user, String featureKey) {
        boolean hasPermission = hasPermission(user, featureKey);
        if (!hasPermission) {
            showRestrictionMessage(context);
        }
        return hasPermission;
    }
}

