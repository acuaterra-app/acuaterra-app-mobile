package com.example.monitoreoacua.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.monitoreoacua.business.models.auth.AuthToken;
import com.example.monitoreoacua.business.models.auth.AuthUser;
import com.google.gson.Gson;

/**
 * Singleton class for managing user session.
 * Handles storing and retrieving user information and authentication tokens.
 */
public class SessionManager {
    // Constants
    private static final String PREF_NAME = "AcuaterraSession";
    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    
    // Singleton instance
    private static SessionManager instance;
    
    // Shared preferences and editor
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    
    // Context
    private Context context;
    
    // Gson for serialization/deserialization
    private Gson gson;
    
    /**
     * Private constructor for Singleton pattern
     * @param context Application context
     */
    private SessionManager(Context context) {
        this.context = context.getApplicationContext(); // Use application context to prevent leaks
        sharedPreferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }
    
    /**
     * Get the singleton instance of SessionManager
     * @param context Context to initialize if needed
     * @return SessionManager instance
     */
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }
    
    /**
     * Save user login session
     * @param user Authenticated user
     * @param token Authentication token
     */
    public void createLoginSession(AuthUser user, AuthToken token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        
        // Store user and token as JSON strings
        editor.putString(KEY_USER, gson.toJson(user));
        editor.putString(KEY_TOKEN, gson.toJson(token));
        
        // Commit changes
        editor.apply();
    }
    
    /**
     * Get stored user
     * @return AuthUser object or null if not found
     */
    public AuthUser getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, AuthUser.class);
        }
        return null;
    }
    
    /**
     * Get stored authentication token
     * @return AuthToken object or null if not found
     */
    public AuthToken getToken() {
        String tokenJson = sharedPreferences.getString(KEY_TOKEN, null);
        if (tokenJson != null) {
            return gson.fromJson(tokenJson, AuthToken.class);
        }
        return null;
    }
    
    /**
     * Get token string with "Bearer " prefix for API calls
     * @return Formatted token string or null
     */
    public String getFormattedToken() {
        AuthToken token = getToken();
        if (token != null) {
            return "Bearer " + token.getToken();
        }
        return null;
    }
    
    /**
     * Check if user is logged in
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Clear session data
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
    
    /**
     * Update user information
     * @param user Updated user object
     */
    public void updateUser(AuthUser user) {
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }
    
    /**
     * Update token
     * @param token Updated token object
     */
    public void updateToken(AuthToken token) {
        editor.putString(KEY_TOKEN, gson.toJson(token));
        editor.apply();
    }
}

