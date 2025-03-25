package com.example.monitoreoacua;

import android.app.Application;
import android.util.Log;

import com.example.monitoreoacua.firebase.NotificationManager;
import com.example.monitoreoacua.firebase.handlers.NotificationHandlerFactory;
import com.example.monitoreoacua.firebase.handlers.AlertNotificationHandler;
import com.example.monitoreoacua.firebase.handlers.CustomNotificationHandler;
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
        
        notificationManager.setContext(getApplicationContext());
        
    }
}

