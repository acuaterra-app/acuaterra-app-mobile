package com.example.monitoreoacua;

import android.app.Application;
import android.util.Log;

import com.example.monitoreoacua.firebase.NotificationManager;
import com.example.monitoreoacua.firebase.handlers.NotificationHandlerFactory;

/**
 * Clase principal de la aplicación para inicializar componentes globales
 */
public class MainApplication extends Application {

    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Inicializar el sistema de notificaciones a nivel de aplicación
        initializeNotificationSystem();
    }

    /**
     * Inicializa el sistema de notificaciones
     * Esto es útil cuando queremos que el sistema esté disponible para toda la aplicación
     */
    private void initializeNotificationSystem() {
        // Inicializar el NotificationManager
        NotificationManager notificationManager = NotificationManager.getInstance();
        
        // Establecer el contexto de la aplicación
        notificationManager.setContext(getApplicationContext());
        
        // Registrar los manejadores personalizados disponibles en la aplicación
        registerNotificationHandlers();
        
        Log.d(TAG, "Sistema de notificaciones inicializado a nivel de aplicación");
    }
    
    /**
     * Registra todos los manejadores de notificaciones personalizados
     */
    private void registerNotificationHandlers() {
        NotificationHandlerFactory factory = NotificationHandlerFactory.getInstance();
        
        // Registrar handlers adicionales para tipos de notificaciones específicos
        factory.registerHandler("custom_type", new CustomNotificationHandler());
        factory.registerHandler("alert_type", new AlertNotificationHandler());
        
        Log.d(TAG, "Manejadores de notificaciones registrados correctamente");
    }
}

