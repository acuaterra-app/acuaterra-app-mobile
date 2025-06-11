package com.example.monitoreoacua.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.util.Log;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.views.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Service that handles incoming Firebase Cloud Messaging messages.
 * Uses FireBaseNotificationManager to delegate processing to appropriate handlers.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgServiceTag";
    private static final String DEFAULT_CHANNEL_ID = "default_channel";
    private static final String DEFAULT_CHANNEL_NAME = "Default Channel";

    @Override
    public void onNewToken(@NonNull String token) {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        createNotificationChannel();
        Notification notification = createNotificationFromRemoteMessage(remoteMessage);
        
        // Procesar notificación con el manager para que use los handlers específicos
        FireBaseNotificationManager.getInstance().processNotification(this, notification);
        
        // También mostrar la notificación push tradicional
        displayNotification(notification);
    }

    private Notification createNotificationFromRemoteMessage(@NonNull RemoteMessage remoteMessage) {

        String title = "";
        String message = "";
        String notificationId = null;
        String state = null;
        String dateHour = null;
        Map<String, Object> metaData = null;

        if(remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }

        if (!remoteMessage.getData().isEmpty()) {
            notificationId = remoteMessage.getData().get("id");
            state = remoteMessage.getData().get("state");
            dateHour = remoteMessage.getData().get("dateHour");

            // Extract the metaData object specifically
            String metaDataJson = remoteMessage.getData().get("metaData");
            if (metaDataJson != null) {
                try {
                    // Parse the metaData JSON string into a Map
                    metaData = new Gson().fromJson(metaDataJson, new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType());
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing metaData: " + e.getMessage());
                    metaData = new HashMap<>();
                }
            } else {
                metaData = new HashMap<>();
            }
        }

        int id = notificationId != null ? Integer.parseInt(notificationId) : 0;

        Notification.NotificationData notificationData = new Notification.NotificationData(
                id, state, metaData, dateHour);
        Log.d(TAG, "Notification data: " + notificationData.getMetaData());
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

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notification", notification);

        int notificationId = (int) System.currentTimeMillis() + notification.getData().getId();

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.logoacua)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getMessage())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager androidNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        androidNotificationManager.notify(notificationId, notificationBuilder.build());
    }
}
