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
import com.example.monitoreoacua.firebase.handlers.NotificationHandler;
import com.example.monitoreoacua.firebase.handlers.NotificationHandlerFactory;
import com.example.monitoreoacua.views.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
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
        super.onNewToken(token);
        Log.d(TAG, "Refreshed FCM token: " + token);
        // TODO: Send the token to your backend server if needed
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage);
        }

        // Check if message contains notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            // If we only receive a notification without data, we can show a default notification
            if (remoteMessage.getData().size() == 0) {
                showDefaultNotification(remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody());
            }
        }
    }

    /**
     * Handle data messages received from Firebase
     * @param remoteMessage The message received from Firebase
     */
    private void handleDataMessage(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        
        // Extract notification type
        String notificationType = data.get("type");
        if (notificationType == null) {
            Log.e(TAG, "Notification type not specified in message data");
            showDefaultNotification(
                    remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : getString(R.string.app_name),
                    remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "New notification"
            );
            return;
        }
        
        try {
            // Get appropriate handler from factory
            NotificationHandler handler = NotificationHandlerFactory.getInstance().getHandler(notificationType);
            
            if (handler != null && handler.canHandle(notificationType)) {
                // Process notification with the handler
                handler.handle(getApplicationContext(), data);
            } else {
                Log.d(TAG, "No handler found for notification type: " + notificationType);
                showDefaultNotification(
                        remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : getString(R.string.app_name),
                        remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "New notification"
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling notification", e);
            showDefaultNotification(
                    remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : getString(R.string.app_name),
                    remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "New notification"
            );
        }
    }

    /**
     * Shows a default notification when a specific handler is not available
     * or when an error occurs during handling
     * 
     * @param title The notification title
     * @param message The notification message
     */
    private void showDefaultNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                        .setSmallIcon(R.drawable.icononotificaciones)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_ID,
                    DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}

