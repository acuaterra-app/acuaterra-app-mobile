# Guía de implementación del sistema de notificaciones

Esta guía explica cómo utilizar el sistema de notificaciones refactorizado en la aplicación.

## 1. Inicialización del sistema de notificaciones

El sistema de notificaciones debe inicializarse al inicio de la aplicación. Tienes dos opciones:

### Opción 1: Inicialización en la clase Application

```java
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Inicializar NotificationManager
        NotificationManager notificationManager = NotificationManager.getInstance();
        notificationManager.setContext(getApplicationContext());
        
        // Registrar handlers personalizados
        NotificationHandlerFactory factory = NotificationHandlerFactory.getInstance();
        factory.registerHandler("custom_type", new CustomNotificationHandler());
    }
}
```

### Opción 2: Inicialización en MainActivity

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inicializar NotificationManager
        NotificationManager notificationManager = NotificationManager.getInstance();
        notificationManager.setContext(this);
        
        // Verificar si se inició desde una notificación
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("notification_data")) {
            String notificationData = getIntent().getStringExtra("notification_data");
            Notification notification = notificationManager.parseNotification(notificationData);
            notificationManager.processNotification(notification, true);
        }
    }
}
```

## 2. Manejo de notificaciones recibidas

El sistema maneja automáticamente las notificaciones recibidas a través de `MyFirebaseMessagingService`. Este servicio:

1. Recibe notificaciones de Firebase
2. Parsea los datos a un objeto `Notification`
3. Muestra la notificación en la UI si la app está en primer plano o crea una notificación del sistema si está en segundo plano
4. Delega el manejo de la notificación a un handler específico cuando el usuario interactúa con ella

## 3. Creación de un handler para un tipo específico de notificación

Para manejar un nuevo tipo de notificación, crea una clase que implemente la interfaz `NotificationHandler`:

```java
public class MyCustomNotificationHandler implements NotificationHandler {
    
    @Override
    public void handleNotification(Context context, Notification notification) {
        // Obtener datos específicos de la notificación
        String customId = notification.getMetaData().optString("customId", "");
        
        // Implementar la lógica específica para este tipo de notificación
        Intent intent = new Intent(context, MySpecificActivity.class);
        intent.putExtra("notification_id", notification.getId());
        intent.putExtra("custom_id", customId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        context.startActivity(intent);
    }
}
```

Luego, registra el handler en el `NotificationHandlerFactory`:

```java
NotificationHandlerFactory.getInstance().registerHandler("my_type", new MyCustomNotificationHandler());
```

## Estructura de datos de notificación

La estructura de datos esperada para las notificaciones es:

```json
{
  "data": {
    "id": 19,
    "state": "unread",
    "metaData

