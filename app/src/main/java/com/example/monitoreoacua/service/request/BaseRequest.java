package com.example.monitoreoacua.service.request;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.monitoreoacua.utils.ApplicationContextProvider;

/**
 * Base class for all request objects in the application.
 * Contains a flag to determine if the request requires authentication.
 */
public class BaseRequest {
    
    private boolean requiresAuthentication;
    private static final String PREF_NAME = "user_prefs";

    public BaseRequest() {
        this.requiresAuthentication = false;
    }

    public boolean isRequiresAuthentication() {
        return requiresAuthentication;
    }

    public void setRequiresAuthentication(boolean requiresAuthentication) {
        this.requiresAuthentication = requiresAuthentication;
    }

    public  String getAuthToken() {
        Context context = ApplicationContextProvider.getContext();
        if (context == null) {
            return null;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }
}

