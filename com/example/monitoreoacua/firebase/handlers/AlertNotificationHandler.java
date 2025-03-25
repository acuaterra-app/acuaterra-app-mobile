package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.monitoreoacua.AlertDetailActivity;
import com.example.monitoreoacua.business.models.Notification;

/**
 * Otro ejemplo de handler personalizado para notificaciones de tipo alerta
 */
public class AlertNotificationHandler implements NotificationHandler {
    
    private static final String TAG = "AlertNotificationHandler";
    
    @Override
    public void handleNotification(Context context, Notification notification) {
        Log.d(TAG, "Procesando notificación de alerta: " + notification.getId());
        
        try {
            // Obtener datos específicos de la notificación
            String alertType = notification.getMetaData().optString("alertType", "");
            String entityId = notification.getMetaData().optString("entityId", "");
            String severity = notification.getMetaData().optString("severity", "medium");
            
            // Lanzar la actividad apropiada basada en la severidad
            if ("high".equals(severity)) {
                launchCriticalAlertActivity(context, notification, entityId, alertType);
            } else {
                launchNormalAlertActivity(context, notification, entityId, alertType);
            }
            
            // Registrar la notificación como leída en la base de datos local
            updateNotificationStatus(notification.getId());
            
        } catch (Exception e) {
            Log.e(TAG, "Error al procesar notificación de alerta", e);
        }
    }
    
    /**
     * Lanza la actividad para alertas críticas
     */
    private void launchCriticalAlertActivity(Context context, Notification notification, 
                                             String entityId, String alertType) {
        Intent intent = new Intent(context, AlertDetailActivity.class);
        intent.putExtra("notification_id", notification.getId());
        intent.putExtra("entity_id", entityId);
        intent.putExtra("alert_type", alertType);
        intent.putExtra("is_critical", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        context.startActivity(intent);
    }
    
    /**
     * Lanza la actividad para alertas normales
     */
    private void launchNormalAlertActivity(Context context, Notification notification,
                                          String entityId, String alertType) {
        Intent intent = new Intent(context, AlertDetailActivity.class);
        intent.putExtra("notification_id", notification.getId());
        intent.putExtra("entity_id", entityId);
        intent.putExtra("alert_type", alertType);
        intent.putExtra("is_critical", false);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        context.startActivity(intent);
    }
    
    /**
     * Actualiza el estado de la notificación a "leído" en la base de datos local
     */
    private void updateNotificationStatus(int notificationId) {
        // Aquí iría el código para actualizar la notificación en la base de datos local
        Log.d(TAG, "Marcando notificación " + notificationId + " como leída");
    }
}

