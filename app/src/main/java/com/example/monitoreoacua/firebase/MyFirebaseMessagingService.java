package com.example.monitoreoacua.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Notification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Service that handles incoming Firebase Cloud Messaging messages.
 * Uses FireBaseNotificationManager to delegate processing to appropriate handlers.
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
        super.onMessageReceived(remoteMessage);
        createNotificationChannel();

        Notification notification = createNotificationFromRemoteMessage(remoteMessage);

        displayNotification(notification);
    }

    private Notification createNotificationFromRemoteMessage(@NonNull RemoteMessage remoteMessage) {
        String title = null;
        String message = null;
        String notificationId = null;
        String state = null;
        String dateHour = null;
        Map<String, String> data = new HashMap<>();

        if (!remoteMessage.getData().isEmpty()) {
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
            notificationId = remoteMessage.getData().get("id");
            state = remoteMessage.getData().get("state");
            dateHour = remoteMessage.getData().get("dateHour");

            // Copy any remaining data fields
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                String key = entry.getKey();
                if (!key.equals("title") && !key.equals("message") &&
                        !key.equals("id") && !key.equals("state") && !key.equals("dateHour")) {
                    data.put(key, entry.getValue());
                }
            }
        }

        int id = notificationId != null ? Integer.parseInt(notificationId) : 0;

        Notification.NotificationData notificationData = new Notification.NotificationData(
                id, state, data, dateHour);

        return new Notification(title, message, notificationData);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    DEFAULT_CHANNEL_ID,
                    DEFAULT_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("Notification Channel");
            channel.enableLights(true);
            channel.enableVibration(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    private void displayNotification(Notification notification) {

        Intent intent = new Intent(this, com.example.monitoreoacua.views.MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notification", notification);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build the notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification) // Make sure you have this icon in your drawable resources
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getMessage())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager androidNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId =  notification.getData().getId();


        androidNotificationManager.notify(notificationId, notificationBuilder.build());
    }
}
