package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.request.GetModuleRequest;
import com.example.monitoreoacua.views.farms.farm.modules.ViewModuleActivity;

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
            
            // Always use error type for sensor alerts to get red colors
            String messageType = "error";
            
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
            
            // Navigate to module details screen
            if (moduleId != null && !moduleId.isEmpty()) {
                try {
                    Log.d(TAG, "Fetching details for module ID: " + moduleId);
                    fetchModuleDetails(context, moduleId);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid module ID format: " + moduleId, e);
                }
            }
            
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
    
    /**
     * Fetch module details from API using the module ID
     */
    private void fetchModuleDetails(Context context, String moduleId) {
        if (context == null) {
            Log.e(TAG, "Context is null");
            return;
        }

        try {
            int moduleIdInt = Integer.parseInt(moduleId);
            if (moduleIdInt <= 0) {
                Log.e(TAG, "Invalid module ID: " + moduleId);
                Toast.makeText(context, 
                    "ID de módulo inválido", 
                    Toast.LENGTH_SHORT).show();
                return;
            }

            new GetModuleRequest().getModuleById(new OnApiRequestCallback<Module, Throwable>() {
                @Override
                public void onSuccess(Module module) {
                    if (module != null) {
                        openModuleActivity(context, module);
                    } else {
                        Log.e(TAG, "Module data is null");
                        Toast.makeText(context, 
                            "No se pudo cargar los detalles del módulo", 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFail(Throwable t) {
                    Log.e(TAG, "Failed to fetch module details", t);
                    Toast.makeText(context, 
                        "Error al cargar los detalles del módulo", 
                        Toast.LENGTH_SHORT).show();
                }
            }, moduleIdInt);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid module ID format: " + moduleId, e);
            Toast.makeText(context, 
                "Formato de ID de módulo inválido", 
                Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Open the module details activity
     */
    private void openModuleActivity(Context context, Module module) {
        if (context == null || module == null) {
            Log.e(TAG, "Context or module is null");
            return;
        }
        
        Intent intent = new Intent(context, ViewModuleActivity.class);
        intent.putExtra(ViewModuleActivity.ARG_MODULE_ID, module.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        Log.d(TAG, "Opening ViewModuleActivity for module ID: " + module.getId());
    }
}
