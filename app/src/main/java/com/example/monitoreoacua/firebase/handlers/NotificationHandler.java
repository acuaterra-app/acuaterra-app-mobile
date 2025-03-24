package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;
import java.util.Map;

/**
 * Interface for handling different types of push notifications
 */
public interface NotificationHandler {
    
    /**
     * Determines if this handler can process the given notification type
     * 
     * @param notificationType the type of notification received
     * @return true if this handler supports the notification type
     */
    boolean canHandle(String notificationType);
    
    /**
     * Processes a notification and performs the necessary actions
     * 
     * @param context Android context
     * @param data notification data payload
     */
    void handle(Context context, Map<String, String> data);
}

