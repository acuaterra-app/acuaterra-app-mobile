package com.example.monitoreoacua.firebase.handlers;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory pattern implementation for creating appropriate notification handlers.
 * This class is implemented as a singleton to ensure only one instance exists.
 */
public class NotificationHandlerFactory {
    
    // Singleton instance
    private static NotificationHandlerFactory instance;
    
    // List of available notification handlers
    private final List<NotificationHandler> handlers;
    
    /**
     * Private constructor to prevent instantiation outside this class.
     * Initializes the list of available notification handlers.
     */
    private NotificationHandlerFactory() {
        handlers = new ArrayList<>();
        // Register all notification handlers here
        handlers.add(new FarmNotificationHandler());
        // Add more handlers as needed
    }
    
    /**
     * Get the singleton instance of the factory.
     * 
     * @return The singleton instance of NotificationHandlerFactory
     */
    public static synchronized NotificationHandlerFactory getInstance() {
        if (instance == null) {
            instance = new NotificationHandlerFactory();
        }
        return instance;
    }
    
    /**
     * Register a new notification handler with the factory.
     * 
     * @param handler The notification handler to register
     */
    public void registerHandler(NotificationHandler handler) {
        if (handler != null && !handlers.contains(handler)) {
            handlers.add(handler);
        }
    }
    
    /**
     * Find and return an appropriate handler for the given notification type.
     * 
     * @param notificationType The type of notification to handle
     * @return A notification handler that can handle the given type, or null if none is found
     */
    public NotificationHandler getHandler(String notificationType) {
        for (NotificationHandler handler : handlers) {
            if (handler.canHandle(notificationType)) {
                return handler;
            }
        }
        return null;
    }
    
    /**
     * Clear all registered handlers.
     * Primarily used for testing purposes.
     */
    public void clearHandlers() {
        handlers.clear();
    }
}

