package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.monitoreoacua.business.models.Notification;

import java.util.Map;

/**
 * Handler for sensor alert notifications
 * Processes notifications of type "sensor_alert" and displays appropriate UI
 */
public class SensorAlertNotificationHandler implements NotificationHandler {
    private static final String TAG = "SensorAlertNotificationHandler";
    private static final String NOTIFICATION_TYPE = "sensor_alert";

    @Override
    public boolean canHandle(String notificationType) {
        return NOTIFICATION_TYPE.equals(notificationType);
    }

    @Override
    public void handle(Context context, Notification notification) {
        try {
            Log.d(TAG, "Processing sensor alert notification");
            
            // Extract data from notification
            Map<String, Object> metaData = notification.getData().getMetaData();
            String moduleId = getStringValue(metaData, "moduleId");
            String moduleName = getStringValue(metaData, "moduleName");
            String sensorType = getStringValue(metaData, "sensorType");
            String value = getStringValue(metaData, "value");
            String messageType = getStringValue(metaData, "messageType");
            
            // Build alert message
            StringBuilder alertMessage = new StringBuilder();
            alertMessage.append("⚠️ ").append(notification.getTitle()).append(" ⚠️\n\n");
            alertMessage.append("Módulo: ").append(moduleName);
            alertMessage.append("\nTipo de Sensor: ").append(getSensorTypeDisplay(sensorType));
            
            // Add value with units
            String units = getSensorUnits(sensorType);
            alertMessage.append("\nValor: ").append(value).append(units);
            
            // Add message type if available
            if (messageType != null && !messageType.isEmpty()) {
                alertMessage.append("\nTipo de Alerta: ").append(messageType.toUpperCase());
            }
            
            // Check if threshold info is available
            if (metaData.containsKey("threshold")) {
                Map<String, Object> thresholdData = (Map<String, Object>) metaData.get("threshold");
                if (thresholdData != null) {
                    String min = getStringValue(thresholdData, "min");
                    String max = getStringValue(thresholdData, "max");
                    if (min != null && max != null) {
                        alertMessage.append("\nRango permitido: ")
                            .append(min).append(units)
                            .append(" - ")
                            .append(max).append(units);
                    }
                }
            }
            
            // Show toast notification
            Toast.makeText(context, alertMessage.toString(), Toast.LENGTH_LONG).show();
            
            // TODO: Navigate to sensor details or specific alert screen if needed
            // For example:
            // Intent intent = new Intent(context, SensorAlertDetailActivity.class);
            // intent.putExtra("moduleId", moduleId);
            // intent.putExtra("sensorType", sensorType);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // context.startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling sensor alert notification", e);
        }
    }
    
    /**
     * Safely extract String value from a Map
     */
    private String getStringValue(Map<String, Object> map, String key) {
        if (map != null && map.containsKey(key)) {
            Object value = map.get(key);
            return value != null ? String.valueOf(value) : "";
        }
        return "";
    }
    
    /**
     * Get user-friendly sensor type display name
     */
    private String getSensorTypeDisplay(String sensorType) {
        if (sensorType == null) return "";
        
        switch (sensorType.toLowerCase()) {
            case "temperature":
                return "Temperatura";
            case "ph":
                return "pH";
            case "oxygen":
                return "Oxígeno";
            case "turbidity":
                return "Turbidez";
            default:
                return sensorType;
        }
    }
    
    /**
     * Get sensor units based on sensor type
     */
    private String getSensorUnits(String sensorType) {
        if (sensorType == null) return "";
        
        switch (sensorType.toLowerCase()) {
            case "temperature":
                return "°C";
            case "humidity":
                return "%";
            case "ph":
                return "";
            case "turbidity":
                return "NTU";
            case "oxygen":
                return "mg/L";
            case "proximity":
                return "cm";
            default:
                return "";
        }
    }
}

