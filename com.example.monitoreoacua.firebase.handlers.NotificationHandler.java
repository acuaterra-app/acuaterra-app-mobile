package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;

import java.util.Map;

/**
 * Interface for notification handlers.
 * Each implementation of this interface should handle a specific type of notification.
 */
public interface NotificationHandler {
    
    /**
     * Handles a notification with the given data payload.
     * 
     * @param context The application context
     * @param data The notification data payload
     * @return true if the notification was handled successfully, false otherwise
     */
    boolean handleNotification(Context context, Map<String, String> data);
    
    /**
     * Checks if this handler supports the given notification type.
     * 
     * @param notificationType The type of notification to check
     * @return true if this handler supports the notification type, false otherwise
     */
    boolean supportsNotificationType(String notificationType);
}

