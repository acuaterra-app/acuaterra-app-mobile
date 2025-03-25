package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.CustomActivity;

/**
 * 3. Implementación de un handler personalizado para un tipo específico de notificación
 */
public class CustomNotificationHandler implements NotificationHandler {
    
    private static final String TAG = "CustomNotificationHandler";
    
    @Override
    public void handleNotification(Context context, Notification notification) {
        Log.d(TAG, "Manejando notificación personalizada: " + notification.getId());
        
        // Verificar datos específicos en la metadata de la notificación
        String customId = notification.getMetaData().optString("customId", "");
        String messageType = notification.getMetaData().optString("messageType", "");
        
        // Realizar acciones específicas basadas en el tipo de mensaje
        if ("warning".equals(messageType)) {
            handleWarningNotification(context, notification, customId);
        } else if ("info".equals(messageType)) {
            handleInfoNotification(context, notification, customId);
        } else {
            // Manejar caso por defecto
            handleDefaultNotification(context, notification);
        }
    }
    
    /**
     * Maneja notificaciones de tipo advertencia
     */
    private void handleWarningNotification(Context context, Notification notification, String customId) {
        Log.d(TAG, "Manejando notificación de advertencia con ID: " + customId);
        
        // Crear intent para abrir la actividad específica
        Intent intent = new Intent(context, CustomActivity.class);
        intent.putExtra("notification_id", notification.getId());
        intent.putExtra("custom_id", customId);
        intent.putExtra("message_type", "warning");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        // Iniciar la actividad
        context.startActivity(intent);
    }
    
    /**
     * Maneja notificaciones de tipo información
     */
    private void handleInfoNotification(Context context, Notification notification, String customId) {
        Log.d(TAG, "Manejando notificación de información con ID: " + customId);
        
        // Aquí podrías implementar lógica específica para notificaciones de tipo información
        // Por ejemplo, actualizar datos locales, mostrar un diálogo específico, etc.
        
        // Ejemplo: Iniciar actividad con datos específicos
        Intent intent = new Intent(context, CustomActivity.class);
        intent.putExtra("notification_id", notification.getId());
        intent.putExtra("custom_id", customId);
        intent.putExtra("message_type", "info");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        context.startActivity(intent);
    }
    
    /**
     * Maneja otros tipos de notificaciones personalizadas
     */
    private void handleDefaultNotification(Context context, Notification notification) {
        Log.d(TAG, "Manejando notificación personalizada por defecto: " + notification.getId());
        
        // Implementar manejo por defecto para este tipo de notificación
        Intent intent = new Intent(context, CustomActivity.class);
        intent.putExtra("notification_id", notification.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        context.startActivity(intent);
    }
}

