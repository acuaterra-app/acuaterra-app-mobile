package com.example.monitoreoacua;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.monitoreoacua.firebase.NotificationManager;
import com.example.monitoreoacua.firebase.handlers.NotificationHandler;
import com.example.monitoreoacua.firebase.handlers.NotificationHandlerFactory;
import com.example.monitoreoacua.business.models.Notification;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 1. Inicialización del sistema de notificaciones
        initializeNotificationSystem();
        
        // Verificamos si la actividad se inició desde una notificación
        handleNotificationIfNeeded(getIntent().getExtras());
    }
    
    /**
     * Inicializa el sistema de notificaciones configurando el NotificationManager
     * y registrando los handlers necesarios
     */
    private void initializeNotificationSystem() {
        // Inicializar el NotificationManager
        notificationManager = NotificationManager.getInstance();
        
        // Configurar el contexto para el NotificationManager
        notificationManager.setContext(this);
        
        // Registrar handlers adicionales si es necesario
        NotificationHandlerFactory.getInstance().registerHandler("custom_type", new CustomNotificationHandler());
        
        Log.d(TAG, "Sistema de notificaciones inicializado correctamente");
    }
    
    /**
     * Maneja una notificación si la actividad se inició desde una notificación
     * @param extras Datos recibidos en el intent
     */
    private void handleNotificationIfNeeded(Bundle extras) {
        if (extras != null && extras.containsKey("notification_data")) {
            String notificationData = extras.getString("notification_data");
            Log.d(TAG, "Notificación recibida a través del intent: " + notificationData);
            
            // 2. Manejo de la notificación recibida
            Notification notification = notificationManager.parseNotification(notificationData);
            if (notification != null) {
                // Procesar la notificación
                notificationManager.processNotification(notification, true);
            }
        }
    }
    
    // Más código de la actividad...
}

