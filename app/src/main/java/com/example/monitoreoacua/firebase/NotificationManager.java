package com.example.monitoreoacua.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.firebase.handlers.NotificationHandler;
import com.example.monitoreoacua.firebase.handlers.NotificationHandlerFactory;
import com.example.monitoreoacua.views.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
     * Parse raw notification data into a Notification object
     * 
     * @param data A map containing the raw notification data
     * @return A Notification object, or null if parsing failed
     */
    public Notification parseNotification(Map<String, String> data) {
        if (data == null || data.isEmpty()) {
            Log.w(TAG, "Cannot parse notification: data is null or empty");
            return null;
        }

        try {
            // Get notification data payload
            String notificationDataJson = data.get("data");
            if (notificationDataJson == null || notificationDataJson.isEmpty()) {
                // If no data field, try to build from individual fields
                return createNotificationFromFields(data);
            }

            // Parse notification data
            JsonObject jsonObject = JsonParser.parseString(notificationDataJson).getAsJsonObject();
            
            // Extract metadata
            JsonObject metaDataJson = jsonObject.getAsJsonObject("metaData");
            Map<String, String> metaData = new HashMap<>();
            if (metaDataJson != null) {
                for (String key : metaDataJson.keySet()) {
                    metaData.put(key, metaDataJson.get(key).getAsString());
                }
            }

            // Create notification data object
            int id = jsonObject.has("id") ? jsonObject.get("id").getAsInt() : 0;
            String state = jsonObject.has("state") ? jsonObject.get("state").getAsString() : "unread";
            String dateHour = jsonObject.has("dateHour") ? jsonObject.get("dateHour").getAsString() : "";
            
            Notification.NotificationData notificationData = new Notification.NotificationData(
                    id, state, metaData, dateHour);

            // Get or generate title and message
            String title = data.get("title");
            String message = data.get("body");
            
            // If title and message are not in the main data, try to generate from metadata
            if ((title == null || title.isEmpty()) && metaData.containsKey("messageType")) {
                title = generateTitleFromType(metaData.get("messageType"));
            }
            
            if (message == null || message.isEmpty()) {
                message = generateMessageFromType(metaData);
            }

            // Create and return the notification object
            return new Notification(title, message, notificationData);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing notification data", e);
            return createNotificationFromFields(data);
        }
    }

    /**
     * Create a notification from individual fields when structured data is not available
     */
    private Notification createNotificationFromFields(Map<String, String> data) {
        try {
            String title = data.get("title");
            String message = data.get("body");
            
            // Extract type for metadata
            String type = data.get("type");
            if (type == null || type.isEmpty()) {
                if (data.containsKey("farmId")) {
                    type = "farm";
                }
            }
            
            // Create metadata map
            Map<String, String> metaData = new HashMap<>();
            metaData.put("type", type != null ? type : "");
            
            // Add other relevant fields to metadata
            for (String key : data.keySet()) {
                if (!key.equals("title") && !key.equals("body") && !key.equals("type")) {
                    metaData.put(key, data.get(key));
                }
            }
            
            // Create notification data
            int id = 0;
            if (data.containsKey("id")) {
                try {
                    id = Integer.parseInt(data.get("id"));
                } catch (NumberFormatException e) {
                    // Ignore parsing error
                }
            }
            
            Notification.NotificationData notificationData = 
                    new Notification.NotificationData(id, "unread", metaData, "");
            
            return new Notification(title, message, notificationData);
        } catch (Exception e) {
            Log.e(TAG, "Error creating notification from fields", e);
            return null;
        }
    }
    
    /**
     * Generate a title based on the notification type
     */
    private String generateTitleFromType(String messageType) {
        if (messageType == null) {
            return "Nueva notificación";
        }
        
        switch (messageType.toLowerCase()) {
            case "info":
                return "Información";
            case "warning":
                return "Advertencia";
            case "alert":
                return "Alerta";
            case "error":
                return "Error";
            default:
                return "Nueva notificación";
        }
    }
    
    /**
     * Generate a message based on the notification metadata
     */
    private String generateMessageFromType(Map<String, String> metaData) {
        if (metaData == null || metaData.isEmpty()) {
            return "Tienes una nueva notificación.";
        }
        
        String type = metaData.get("type");
        if ("farm".equals(type) && metaData.containsKey("farmId")) {
            return "Hay una actualización para la granja #" + metaData.get("farmId");
        }
        
        return "Tienes una nueva notificación.";
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

