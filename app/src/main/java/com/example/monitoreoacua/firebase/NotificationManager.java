package com.example.monitoreoacua.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.monitoreoacua.firebase.handlers.NotificationHandler;
import com.example.monitoreoacua.firebase.handlers.NotificationHandlerFactory;
import com.example.monitoreoacua.views.MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Central manager for handling notifications throughout the application.
 * This class centralizes notification handling, intent parsing, and routing.
 */
public class NotificationManager {

    private static final String TAG = "NotificationManager";
    
    // Singleton instance
    private static NotificationManager instance;
    
    private NotificationManager() {
        // Private constructor to prevent direct instantiation
    }
    
    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }
    
    /**
     * Process a notification intent received by an activity
     * 
     * @param context The activity context
     * @param intent The intent received by the activity
     * @return true if the intent was a notification intent and was processed, false otherwise
     */
    public boolean processNotificationIntent(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return false;
        }
        
        logIntentDetails(intent);
        
        // Check if this is a notification intent
        if (hasNotificationData(intent)) {
            // Extract notification data
            Map<String, String> notificationData = extractNotificationData(intent);
            
            // Delegate to appropriate handler based on notification type
            return routeNotification(context, notificationData);
        }
        
        return false;
    }
    
    /**
     * Check if an intent contains notification data
     */
    public boolean hasNotificationData(Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return false;
        }
        
        // Check for common notification fields
        return intent.hasExtra("type") || intent.hasExtra("farmId") || 
               intent.hasExtra("notificationType") || intent.hasExtra("title") || 
               intent.hasExtra("body");
    }
    
    /**
     * Extract notification data from an intent
     */
    public Map<String, String> extractNotificationData(Intent intent) {
        Map<String, String> data = new HashMap<>();
        
        if (intent == null || intent.getExtras() == null) {
            return data;
        }
        
        Bundle extras = intent.getExtras();
        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            if (value != null) {
                data.put(key, value.toString());
            }
        }
        
        return data;
    }
    
    /**
     * Route notification to appropriate handler based on notification type
     */
    public boolean routeNotification(Context context, Map<String, String> data) {
        String notificationType = data.get("type");
        
        // If type is not specified, try to derive it from other fields
        if (notificationType == null || notificationType.isEmpty()) {
            if (data.containsKey("farmId")) {
                notificationType = "farm";
            } else if (data.containsKey("notificationType")) {
                notificationType = data.get("notificationType");
            }
        }
        
        if (notificationType != null && !notificationType.isEmpty()) {
            Log.d(TAG, "Routing notification of type: " + notificationType);
            
            // Get the appropriate handler for this notification type
            NotificationHandler handler = NotificationHandlerFactory.getInstance().getHandler(notificationType);
            
            if (handler != null) {
                // Delegate handling to the appropriate handler
                handler.handle(context, data);
                return true;
            } else {
                Log.w(TAG, "No handler found for notification type: " + notificationType);
            }
        }
        
        return false;
    }
    
    /**
     * Create an intent for showing a notification in an activity
     */
    public Intent createNotificationIntent(Context context, Map<String, String> data) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Add all notification data to the intent
        if (data != null && !data.isEmpty()) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        
        return intent;
    }
    
    /**
     * Log details about an intent for debugging purposes
     */
    private void logIntentDetails(Intent intent) {
        if (intent == null) {
            Log.d(TAG, "logIntentDetails: Intent is null");
            return;
        }
        
        Log.d(TAG, "Intent details:");
        Log.d(TAG, "  Action: " + intent.getAction());
        Log.d(TAG, "  Data URI: " + (intent.getData() != null ? intent.getData().toString() : "null"));
        
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(TAG, "  Extras:");
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d(TAG, "    " + key + ": " + (value != null ? value.toString() : "null"));
            }
        } else {
            Log.d(TAG, "  No extras in the intent");
        }
    }
}

