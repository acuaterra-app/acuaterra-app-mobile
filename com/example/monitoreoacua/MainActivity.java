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
        
        // Inicialización del sistema de notificaciones
        initializeNotificationSystem();
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
    
    // Más código de la actividad...
}

