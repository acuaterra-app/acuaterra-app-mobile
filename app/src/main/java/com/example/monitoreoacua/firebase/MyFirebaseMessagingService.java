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
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

/**
 * Service that handles incoming Firebase Cloud Messaging messages.
 * Uses NotificationHandlerFactory to delegate processing to appropriate handlers.
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

        // Create notification channel for Android Oreo and above
        createNotificationChannel();

        // Handle notification payload (if present)
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            
            // Always show system notification regardless of app state
            showNotification(title, body, remoteMessage.getData());
            
            // Also show in-app notification if app is in foreground
            if (InAppNotificationHelper.isAppInForeground()) {
                InAppNotificationHelper.showInAppNotification(title, body, remoteMessage.getData());
            }
        }
        
        // Handle data payload (if present)
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message Data: " + remoteMessage.getData());
            
            // If there's no notification payload, generate one from data
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                
                if (title != null && body != null) {
                    // Always show system notification regardless of app state
                    showNotification(title, body, remoteMessage.getData());
                    
                    // Also show in-app notification if app is in foreground
                    if (InAppNotificationHelper.isAppInForeground()) {
                        InAppNotificationHelper.showInAppNotification(title, body, remoteMessage.getData());
                    }
                }
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
     */
    private void showNotification(String title, String body, Map<String, String> data) {
        Intent intent = com.example.monitoreoacua.firebase.NotificationManager.getInstance().createNotificationIntent(this, data);
        
        int notificationId = 0;
        try {
            if (data.containsKey("notification_id")) {
                notificationId = Integer.parseInt(data.get("notification_id"));
            } else {
                notificationId = (int) System.currentTimeMillis();
            }
        } catch (Exception e) {
            notificationId = (int) System.currentTimeMillis();
        }
        
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
}
