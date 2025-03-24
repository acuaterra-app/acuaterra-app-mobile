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
        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage);
            return;
        }

        // Check if message contains notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            showDefaultNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Handle data messages received from Firebase
     *
     * @param remoteMessage The message received from Firebase
     */
    private void handleDataMessage(RemoteMessage remoteMessage) {
        Log.e(TAG, "handleDataMessage: " + remoteMessage.getData());
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
                Log.d(TAG, "handler" + handler);
                // Process notification with the handler
                handler.handle(getApplicationContext(), data);

                // Use specific notification handler based on type
                String title = remoteMessage.getNotification() != null ?
                        remoteMessage.getNotification().getTitle() : getString(R.string.app_name);
                String message = remoteMessage.getNotification() != null ?
                        remoteMessage.getNotification().getBody() : "New notification";

                if ("farm".equals(notificationType) && data.containsKey("farmId")) {
                    showFarmNotification(title, message, data.get("farmId"));
                } else {
                    showDefaultNotification(title, message);
                }
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
     * @param title   The notification title
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

    /**
     * Shows a notification specific to farm events that will open FarmDetailsActivity when clicked
     *
     * @param title   The notification title
     * @param message The notification message
     * @param farmId  The ID of the farm to open when the notification is clicked
     */
    private void showFarmNotification(String title, String message, String farmId) {
        // Log detailed information for debugging
        Log.d(TAG, "showFarmNotification: INICIO - Creando notificación para granja con ID: " + farmId);
        Log.d(TAG, "showFarmNotification: Título: " + title);
        Log.d(TAG, "showFarmNotification: Mensaje: " + message);

        // Validate farmId before proceeding
        if (farmId == null || farmId.trim().isEmpty()) {
            Log.e(TAG, "showFarmNotification: Error - farmId es nulo o vacío");
            showDefaultNotification(title, message);
            return;
        }

        // Clean farmId
        String cleanFarmId = farmId.trim();
        Log.d(TAG, "showFarmNotification: ID limpio: " + cleanFarmId);

        // Generate a unique request code based on farmId hash
        int requestCode = Math.abs(cleanFarmId.hashCode());
        Log.d(TAG, "showFarmNotification: Request code generado: " + requestCode);

        try {
            // Create explicit intent directly to FarmDetailsActivity (using class reference)
            Log.d(TAG, "showFarmNotification: Creando Intent explícito para FarmDetailsActivity");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Specify the component explicitly for reliable navigation
            intent.setClassName(getPackageName(),
                    "com.example.monitoreoacua.views.farms.farm.FarmDetailsActivity");

            // Set flags for proper activity launching
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Log.d(TAG, "showFarmNotification: Flags de Intent configurados");

            // Pass farmId as extra
            intent.putExtra("farmId", cleanFarmId);
            Log.d(TAG, "showFarmNotification: Añadido farmId: " + cleanFarmId + " al Intent");

            // Add debug information
            intent.putExtra("notificationSource", "firebase_direct");
            intent.putExtra("notificationTimestamp", System.currentTimeMillis());

            // Create PendingIntent with appropriate flags based on Android version
            PendingIntent pendingIntent;
            int pendingIntentFlags;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // For Android 12 (API 31) and above
                pendingIntentFlags = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
                Log.d(TAG, "showFarmNotification: Usando flags para Android 12+: FLAG_IMMUTABLE | FLAG_UPDATE_CURRENT");
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // For Android 6-11
                pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
                Log.d(TAG, "showFarmNotification: Usando flags para Android 6-11: FLAG_UPDATE_CURRENT");
            } else {
                // For older versions
                pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
                Log.d(TAG, "showFarmNotification: Usando flags para Android <6: FLAG_UPDATE_CURRENT");
            }

            pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    requestCode,
                    intent,
                    pendingIntentFlags
            );

            Log.d(TAG, "showFarmNotification: PendingIntent creado correctamente");

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

            // Use farmId as part of notification ID to ensure different notifications for different farms
            int notificationId = requestCode;

            // Log final notification details
            Log.d(TAG, "showFarmNotification: Mostrando notificación con ID: " + notificationId);
            Log.d(TAG, "showFarmNotification: PendingIntent apunta a FarmDetailsActivity con farmId: " + cleanFarmId);

            // Show the notification
            notificationManager.notify(notificationId, notificationBuilder.build());

            // Verificación final
            Log.d(TAG, "showFarmNotification: Notificación mostrada exitosamente con ID: " + notificationId);
            Log.d(TAG, "showFarmNotification: Resumen - Notificación para farmId: " + cleanFarmId);
            Log.d(TAG, "showFarmNotification: Título: " + title);
            Log.d(TAG, "showFarmNotification: FIN");
        } catch (Exception e) {
            Log.e(TAG, "showFarmNotification: Error al crear o mostrar la notificación", e);
            // Fallback to default notification
            showDefaultNotification(title, message);
        }
    }

    /**
     * Shows a default notification as a fallback when a farm-specific notification can't be created
     * or when other error conditions are encountered
     *
     * @param title            The notification title
     * @param message          The notification message
     * @param errorDescription A description of the error (optional)
     */
    private void showDefaultNotification(String title, String message, String errorDescription) {
        Log.d(TAG, "showDefaultNotification: Mostrando notificación por defecto");
        if (errorDescription != null) {
            Log.w(TAG, "showDefaultNotification: Razón: " + errorDescription);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = PendingIntent.FLAG_ONE_SHOT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                flags
        );

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

        // Use a random ID based on current time to avoid notification overrides
        int notificationId = (int) System.currentTimeMillis() % 10000;
        notificationManager.notify(notificationId, notificationBuilder.build());

        Log.d(TAG, "showDefaultNotification: Notificación mostrada correctamente con ID: " + notificationId);
    }

}
