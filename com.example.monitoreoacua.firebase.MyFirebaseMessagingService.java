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
import com.example.monitoreoacua.firebase.handlers.NotificationHandlerFactory;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Service to handle Firebase Cloud Messaging (FCM) messages.
 * This service extends FirebaseMessagingService to handle incoming messages and use 
 * the factory pattern to direct notifications to appropriate handlers.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "AcuaTerraChannel";
    private static final String CHANNEL_NAME = "AcuaTerra Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications from AcuaTerra app";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Check if message contains data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            
            Map<String, String> data = remoteMessage.getData();
            handleDataMessage(data);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // TODO: Send token to your app server if needed
    }

    /**
     * Handle data message by using the appropriate notification handler.
     * If no handler is found or if handling fails, a default notification is shown.
     * 
     * @param data The data payload from the FCM message
     */
    private void handleDataMessage(Map<String, String> data) {
        boolean handled = NotificationHandlerFactory.getInstance().handleNotification(this, data);
        
        if (!handled) {
            // If no handler could process the notification, show a default notification
            sendNotification(
                data.getOrDefault("title", "Nueva notificación"),
                data.getOrDefault("body", "Tienes una nueva notificación")
            );
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param title Title for notification
     * @param messageBody Body of notification
     */
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, ListFarmsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}

