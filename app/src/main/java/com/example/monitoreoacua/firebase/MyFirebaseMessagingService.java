package com.example.monitoreoacua.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Notification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * Service that handles incoming Firebase Cloud Messaging messages.
 * Uses NotificationManager to delegate processing to appropriate handlers.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final String DEFAULT_CHANNEL_ID = "default_channel";
    private static final String DEFAULT_CHANNEL_NAME = "Default Channel";

    @Override
    public void onNewToken(@NonNull String token) {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Always call super method first
        super.onMessageReceived(remoteMessage);
        
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification: " + remoteMessage.getData());

        // Create notification channel for Android Oreo and above
        createNotificationChannel();

        // Prepare notification data map from RemoteMessage
        Map<String, String> notificationData = prepareNotificationData(remoteMessage);
        
        // Use NotificationManager to parse the notification
        Notification notification = com.example.monitoreoacua.firebase.NotificationManager.getInstance()
                .parseNotification(notificationData);
        
        if (notification != null) {
            // Process the parsed notification
            processNotification(notification, notificationData);
        } else {
            Log.w(TAG, "Failed to parse notification data");
        }
    }
    
    /**
     * Prepares a unified notification data map from RemoteMessage
     * 
     * @param remoteMessage The incoming FCM message
     * @return A map containing all notification data
     */
    private Map<String, String> prepareNotificationData(RemoteMessage remoteMessage) {
        Map<String, String> notificationData = new HashMap<>(remoteMessage.getData());
        
        // Add notification payload data if available
        if (remoteMessage.getNotification() != null) {
            // Add title and body from notification payload if not already in data
            if (remoteMessage.getNotification().getTitle() != null && 
                !notificationData.containsKey("title")) {
                notificationData.put("title", remoteMessage.getNotification().getTitle());
            }
            
            if (remoteMessage.getNotification().getBody() != null && 
                !notificationData.containsKey("body")) {
                notificationData.put("body", remoteMessage.getNotification().getBody());
            }
        }
        
        return notificationData;
    }
    
    /**
     * Process notification in a consistent way for both foreground and background
     * 
     * @param notification The parsed notification object
     * @param notificationData The raw notification data
     */
    private void processNotification(Notification notification, Map<String, String> notificationData) {
        String title = notification.getTitle();
        String message = notification.getMessage();
        
        if (title != null && message != null) {
            // Always show system notification regardless of app state
            showNotification(title, message, notificationData);
            
            // Popup functionality has been removed - no in-app notifications will be shown
        }
    }
    
    /**
     * Create notification channel for Android Oreo (API 26) and above
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    DEFAULT_CHANNEL_ID,
                    DEFAULT_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            
            channel.setDescription("Canal para notificaciones de la aplicación");
            channel.enableLights(true);
            channel.enableVibration(true);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    /**
     * Show notification with title, body and optional data
     * 
     * This method creates and displays a system notification. When the notification is clicked,
     * it will use the NotificationManager's routing logic to determine how to handle the notification.
     * 
     * The notification intent includes:
     * - ACTION_NOTIFICATION_CLICKED: A custom action to identify notification clicks
     * - All notification data as extras, enabling proper routing by NotificationManager
     * 
     * @param title The notification title
     * @param body The notification body text
     * @param data Map containing all notification data
     */
    private void showNotification(String title, String body, Map<String, String> data) {
        // Use the notification click action defined in NotificationManager
        
        // Create an intent with the notification click action
        Intent intent = com.example.monitoreoacua.firebase.NotificationManager.getInstance().createNotificationIntent(this, data);
        intent.setAction(com.example.monitoreoacua.firebase.NotificationManager.ACTION_NOTIFICATION_CLICKED);
        
        // Add notification data to the intent for proper routing
        for (Map.Entry<String, String> entry : data.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        
        // Add a flag to ensure MainActivity properly processes this notification
        // Add a flag to ensure MainActivity properly processes this notification
        intent.putExtra("notification_source", "system_tray");
        intent.putExtra(com.example.monitoreoacua.firebase.NotificationManager.EXTRA_SHOULD_ROUTE, true);
        // Add flags to handle activity launch modes properly
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        int notificationId = getNotificationId(data);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 
                notificationId, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.logoacua) // Ícono de notificación actualizado
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Alta prioridad
                .setContentIntent(pendingIntent);
        
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }
    
    /**
     * Get notification ID from data or generate a unique one
     * 
     * @param data Notification data map
     * @return Notification ID
     */
    private int getNotificationId(Map<String, String> data) {
        // Try to get ID from data map
        if (data != null) {
            // First check for the structured ID
            if (data.containsKey("id")) {
                try {
                    return Integer.parseInt(data.get("id"));
                } catch (NumberFormatException e) {
                    // Ignore parse error
                }
            }
            
            // Then check for the legacy notification_id
            if (data.containsKey("notification_id")) {
                try {
                    return Integer.parseInt(data.get("notification_id"));
                } catch (NumberFormatException e) {
                    // Ignore parse error
                }
            }
        }
        
        // If no valid ID found, generate one from current timestamp
        return (int) System.currentTimeMillis();
    }
}
