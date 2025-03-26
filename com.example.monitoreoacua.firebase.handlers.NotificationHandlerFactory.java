package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NotificationHandlerFactory {
    private static NotificationHandlerFactory instance;
    private final List<NotificationHandler> handlers;

    private NotificationHandlerFactory() {
        handlers = new ArrayList<>();
        // Register all notification handlers here
        handlers.add(new FarmNotificationHandler());
        // Add more handlers as needed
    }


    public static synchronized NotificationHandlerFactory getInstance() {
        if (instance == null) {
            instance = new NotificationHandlerFactory();
        }
        return instance;
    }

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

    public NotificationHandler getHandlerForType(String notificationType) {
        for (NotificationHandler handler : handlers) {
            if (handler.supportsNotificationType(notificationType)) {
                return handler;
            }
        }
        return null;
    }
}

