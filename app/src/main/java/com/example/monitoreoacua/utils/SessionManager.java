package com.example.monitoreoacua.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

/**
 * Session Manager for handling user session data securely
 * 
 * This class is responsible for saving, retrieving, and managing session data
 * including user ID, farm ID, authentication tokens, user type, and login state.
 * It uses EncryptedSharedPreferences when available for enhanced security.
 */
public class SessionManager {
    // Logging tag
    private static final String TAG = "SessionManager";
    
    // SharedPreferences file name and mode
    private static final String PREF_NAME = "AcuaterraSecurePrefs";
    private static final int PRIVATE_MODE = Context.MODE_PRIVATE;
    
    // SharedPreferences keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_TYPE = "userType";  // "owner" or "worker"
    private static final String KEY_FARM_ID = "farmId";
    private static final String KEY_FARM_NAME = "farmName";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_TOKEN_EXPIRY = "tokenExpiry";
    private static final String KEY_LAST_LOGIN = "lastLogin";
    
    // User types
    public static final String USER_TYPE_OWNER = "owner";
    public static final String USER_TYPE_WORKER = "worker";
    
    // SharedPreferences instance
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private final Context context;
    
    /**
     * Constructor - attempts to use EncryptedSharedPreferences if available,
     * falls back to regular SharedPreferences if encryption is not available
     * 
     * @param context Application context
     */
    public SessionManager(@NonNull Context context) {
        this.context = context;
        
        // Try to use EncryptedSharedPreferences for enhanced security
        SharedPreferences securePrefs = null;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            
            securePrefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            Log.d(TAG, "Using EncryptedSharedPreferences for enhanced security");
        } catch (GeneralSecurityException | IOException e) {
            Log.w(TAG, "Failed to create EncryptedSharedPreferences, using regular SharedPreferences: " + e.getMessage());
        }
        
        // Fall back to regular SharedPreferences if encryption failed
        if (securePrefs != null) {
            preferences = securePrefs;
        } else {
            preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        }
        
        editor = preferences.edit();
    }
    
    /**
     * Create login session with all user data
     * 
     * @param userId User ID
     * @param userName User name
     * @param userEmail User email
     * @param userType User type (owner/worker)
     * @param farmId Farm ID
     * @param farmName Farm name
     * @param authToken Authentication token
     * @param refreshToken Refresh token
     * @param tokenExpiry Token expiry timestamp
     */
    public void createLoginSession(int userId, String userName, String userEmail, 
                                  String userType, int farmId, String farmName, 
                                  String authToken, String refreshToken, long tokenExpiry) {
        // Validate user type
        if (!USER_TYPE_OWNER.equals(userType) && !USER_TYPE_WORKER.equals(userType)) {
            Log.w(TAG, "Invalid user type: " + userType + ". Defaulting to worker.");
            userType = USER_TYPE_WORKER;
        }
        
        // Store session data
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName != null ? userName : "");
        editor.putString(KEY_USER_EMAIL, userEmail != null ? userEmail : "");
        editor.putString(KEY_USER_TYPE, userType);
        editor.putInt(KEY_FARM_ID, farmId);
        editor.putString(KEY_FARM_NAME, farmName != null ? farmName : "");
        editor.putString(KEY_AUTH_TOKEN, authToken != null ? authToken : "");
        editor.putString(KEY_REFRESH_TOKEN, refreshToken != null ? refreshToken : "");
        editor.putLong(KEY_TOKEN_EXPIRY, tokenExpiry);
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis());
        
        // Commit changes to storage
        editor.apply();
        
        Log.d(TAG, "User login session created: " + userName + " (ID: " + userId + 
              ", Type: " + userType + ", Farm: " + farmName + ")");
    }
    
    /**
     * Check if user is logged in
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Check if user is a farm owner
     * 
     * @return true if user is an owner, false otherwise
     */
    public boolean isOwner() {
        return USER_TYPE_OWNER.equals(getUserType());
    }
    
    /**
     * Check if authentication token is valid (not expired)
     * 
     * @return true if token is valid, false if invalid or expired
     */
    public boolean isTokenValid() {
        if (!isLoggedIn()) {
            return false;
        }
        
        String authToken = getAuthToken();
        long expiry = getTokenExpiry();
        
        // Token is valid if it exists and has not expired
        return !TextUtils.isEmpty(authToken) && expiry > System.currentTimeMillis();
    }
    
    /**
     * Get user ID
     * 
     * @return User ID or -1 if not available
     */
    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }
    
    /**
     * Get user name
     * 
     * @return User name or empty string if not available
     */
    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }
    
    /**
     * Get user email
     * 
     * @return User email or empty string if not available
     */
    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }
    
    /**
     * Get user type (owner/worker)
     * 
     * @return User type or empty string if not available
     */
    public String getUserType() {
        return preferences.getString(KEY_USER_TYPE, "");
    }
    
    /**
     * Get farm ID
     * 
     * @return Farm ID or -1 if not available
     */
    public int getFarmId() {
        return preferences.getInt(KEY_FARM_ID, -1);
    }
    
    /**
     * Get farm name
     * 
     * @return Farm name or empty string if not available
     */
    public String getFarmName() {
        return preferences.getString(KEY_FARM_NAME, "");
    }
    
    /**
     * Get authentication token
     * 
     * @return Authentication token or empty string if not available
     */
    public String getAuthToken() {
        return preferences.getString(KEY_AUTH_TOKEN, "");
    }
    
    /**
     * Get refresh token
     * 
     * @return Refresh token or empty string if not available
     */
    public String getRefreshToken() {
        return preferences.getString(KEY_REFRESH_TOKEN, "");
    }
    
    /**
     * Get token expiry timestamp
     * 
     * @return Token expiry timestamp in milliseconds or 0 if not available
     */
    public long getTokenExpiry() {
        return preferences.getLong(KEY_TOKEN_EXPIRY, 0);
    }
    
    /**
     * Get last login timestamp
     * 
     * @return Last login timestamp in milliseconds or 0 if not available
     */
    public long getLastLogin() {
        return preferences.getLong(KEY_LAST_LOGIN, 0);
    }
    
    /**
     * Update user information
     * 
     * @param userName New user name
     * @param userEmail New user email
     * @param userType New user type (owner/worker)
     */
    public void updateUserInfo(String userName, String userEmail, String userType) {
        // Validate user type
        if (!USER_TYPE_OWNER.equals(userType) && !USER_TYPE_WORKER.equals(userType)) {
            Log.w(TAG, "Invalid user type: " + userType + ". Keeping existing value.");
            userType = getUserType();
        }
        
        editor.putString(KEY_USER_NAME, userName != null ? userName : getUserName());
        editor.putString(KEY_USER_EMAIL, userEmail != null ? userEmail : getUserEmail());
        editor.putString(KEY_USER_TYPE, userType);
        editor.apply();
        
        Log.d(TAG, "User info updated: " + userName + " (" + userEmail + ", Type: " + userType + ")");
    }
    
    /**
     * Update farm information
     * 
     * @param farmId New farm ID
     * @param farmName New farm name
     */
    public void updateFarmInfo(int farmId, String farmName) {
        editor.putInt(KEY_FARM_ID, farmId);
        editor.putString(KEY_FARM_NAME, farmName != null ? farmName : "");
        editor.apply();
        
        Log.d(TAG, "Farm info updated: " + farmName + " (ID: " + farmId + ")");
    }
    
    /**
     * Update authentication tokens
     * 
     * @param authToken New authentication token
     * @param refreshToken New refresh token
     * @param tokenExpiry New token expiry timestamp
     */
    public void updateTokens(String authToken, String refreshToken, long tokenExpiry) {
        editor.putString(KEY_AUTH_TOKEN, authToken != null ? authToken : "");
        editor.putString(KEY_REFRESH_TOKEN, refreshToken != null ? refreshToken : "");
        editor.putLong(KEY_TOKEN_EXPIRY, tokenExpiry);
        editor.apply();
        
        Log.d(TAG, "Auth tokens updated, new expiry: " + new Date(tokenExpiry));
    }
    
    /**
     * Clear session data and log out user
     */
    public void logout() {
        // Log before clearing
        Log.d(TAG, "Logging out user: " + getUserName() + " (ID: " + getUserId() + ")");
        
        // Clear all data from SharedPreferences
        editor.clear();
        editor.apply();
    }
    
    /**
     * Clear specific session data but keep user logged in
     * Useful for refreshing data without logging out
     */
    public void clearSessionDataKeepAuth() {
        // Keep login status and basic user info
        boolean isLoggedIn = isLoggedIn();
        int userId = getUserId();
        String userName = getUserName();
        String userEmail = getUserEmail();
        String userType = getUserType();
        String authToken = getAuthToken();
        String refreshToken = getRefreshToken();
        long tokenExpiry = getTokenExpiry();
        
        // Clear all data
        editor.clear();
        
        // Restore basic user info and auth info
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_USER_TYPE, userType);
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putLong(KEY_TOKEN_EXPIRY, tokenExpiry);
        editor.apply();
        
        Log.d(TAG, "Session data partially cleared but auth retained for: " + userName);
    }
    
    /**
     * Get all session data as a formatted string (for debugging)
     * This method masks sensitive information like tokens
     * 
     * @return String representation of session data with sensitive data masked
     */
    public String getSessionDebugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Session Data (DEBUG):\n");
        sb.append("- Logged in: ").append(isLoggedIn()).append("\n");
        sb.append("- User ID: ").append(getUserId()).append("\n");
        sb.append("- User Name: ").append(getUserName()).append("\n");
        sb.append("- User Email: ").append(getUserEmail()).append("\n");
        sb.append("- User Type: ").append(getUserType()).append("\n");
        sb.append("- Is Owner: ").append(isOwner()).append("\n");
        sb.append("- Farm ID: ").append(getFarmId()).append("\n");
        sb.append("- Farm Name: ").append(getFarmName()).append("\n");
        
        // Mask sensitive data
        String token = getAuthToken();
        String refreshToken = getRefreshToken();
        sb.append("- Auth Token: ");
        if (!TextUtils.isEmpty(token)) {
            sb.append(token.substring(0, Math.min(3, token.length()))).append("...[REDACTED]");
        } else {
            sb.append("Not set");
        }
        sb.append("\n");
        
        sb.append("- Refresh Token: ");
        if (!TextUtils.isEmpty(refreshToken)) {
            sb.append("[REDACTED]");
        } else {
            sb.append("Not set");
        }
        sb.append("\n");
        
        sb.append("- Token Valid: ").append(isTokenValid()).append("\n");
        sb.append("- Token Expiry: ").append(new Date(getTokenExpiry())).append("\n");
        sb.append("- Last Login: ").append(new Date(getLastLogin())).append("\n");
        
        return sb.toString();
    }
}
