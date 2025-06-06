package com.example.monitoreoacua.firebase;

import android.content.Context;
import android.util.Log;

import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.firebase.handlers.NotificationHandler;
import com.example.monitoreoacua.firebase.handlers.NotificationHandlerFactory;

import java.util.Map;

public class FireBaseNotificationManager {

    private static final String TAG = "FireBaseNotificationManager";
    public static final String ACTION_NOTIFICATION_CLICKED = "com.example.monitoreoacua.NOTIFICATION_CLICKED";
    public static final String EXTRA_SHOULD_ROUTE = "should_route";
    
    // Singleton instance
    private static FireBaseNotificationManager instance;

    public FireBaseNotificationManager() {
        // Private constructor to prevent direct instantiation
    }
    
    public static synchronized FireBaseNotificationManager getInstance() {
        if (instance == null) {
            instance = new FireBaseNotificationManager();
        }
        return instance;
    }

    public void processNotification(Context context,Notification notification) {
        NotificationHandlerFactory notificationHandlerFactory = NotificationHandlerFactory.getInstance();

        NotificationHandler notificationHandler = notificationHandlerFactory.getHandler(
                String.valueOf(notification.getData().getMetaData().get("type"))
        );

        if(notificationHandler == null) {
            Log.e(TAG, "No handler found for notification type: " + String.valueOf(notification.getData().getMetaData().get("type")));
            return;
        }
        notificationHandler.handle(context, notification);
    }
}

