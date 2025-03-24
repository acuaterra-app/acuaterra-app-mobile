package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Factory class for creating and using notification handlers.
 * Follows the abstract factory pattern to handle different types of notifications.
 */
public class NotificationHandlerFactory {
    private static NotificationHandlerFactory instance;
    private final List<NotificationHandler> handlers;

    private NotificationHandlerFactory() {
        handlers = new ArrayList<>();
        // Register all notification handlers here
        handlers.add(new FarmNotificationHandler());
        // Add more handlers as needed
    }

    /**
     * Get the singleton instance of the factory
     * 
     * @return The singleton instance
     */
    public static synchronized NotificationHandlerFactory getInstance() {
        if (instance == null) {
            instance = new NotificationHandlerFactory();
        }
        return instance;
    }

    /**
     * Handles a notification using the appropriate handler based on notification type.
     * 
     * @param context The application context
     * @param data The notification data payload
     * @return true if the notification was handled, false otherwise
     */
    public boolean handleNotification(Context context, Map<String, String> data) {
        String notificationType = data.get("type");
        if (notificationType == null) {
            return false;
        }

        for (NotificationHandler handler : handlers) {
            if (handler.supportsNotificationType(notificationType)) {
                return handler.handleNotification(context, data);
            }
        }

        return false;
    }

    /**
     * Gets the appropriate notification handler for the given notification type.
     * 
     * @param notificationType The type of notification
     * @return The appropriate notification handler, or null if none is found
     */
    public NotificationHandler getHandlerForType(String notificationType) {
        for (NotificationHandler handler : handlers) {
            if (handler.supportsNotificationType(notificationType)) {
                return handler;
            }
        }
        return null;
    }
}

