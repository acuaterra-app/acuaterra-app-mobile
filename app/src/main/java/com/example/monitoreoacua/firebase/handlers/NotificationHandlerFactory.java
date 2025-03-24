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
    
    private final List<NotificationHandler> handlers;

    private NotificationHandlerFactory() {
        handlers = new ArrayList<>();
        handlers.add(new FarmNotificationHandler());
    }
    
    public static synchronized NotificationHandlerFactory getInstance() {
        if (instance == null) {
            instance = new NotificationHandlerFactory();
        }
        return instance;
    }

    public void registerHandler(NotificationHandler handler) {
        if (handler != null && !handlers.contains(handler)) {
            handlers.add(handler);
        }
    }

    public NotificationHandler getHandler(String notificationType) {
        for (NotificationHandler handler : handlers) {
            if (handler.canHandle(notificationType)) {
                return handler;
            }
        }
        return null;
    }

    public void clearHandlers() {
        handlers.clear();
    }
}

