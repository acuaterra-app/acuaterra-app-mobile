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
 * Handler for power/energy related alerts.
 * Handles notifications for power outages, battery low, power restored, etc.
 */
public class PowerAlertNotificationHandler implements NotificationHandler {
    private static final String TAG = "PowerAlertNotificationHandler";
    private static final String NOTIFICATION_TYPE = "power_alert";

    @Override
    public boolean canHandle(String notificationType) {
        return NOTIFICATION_TYPE.equals(notificationType);
    }

    @Override
    public void handle(Context context, Notification notification) {
        try {
            Log.d(TAG, "Processing power alert notification");
            
            Map<String, Object> metaData = notification.getData().getMetaData();
            String moduleId = getStringValue(metaData, "moduleId");
            String moduleName = getStringValue(metaData, "moduleName");
            String eventType = getStringValue(metaData, "eventType");
            String severity = getStringValue(metaData, "severity");
            
            // Build detailed alert message
            StringBuilder alertMessage = new StringBuilder();
            
            // Add severity emoji
            String severityEmoji = getSeverityEmoji(severity);
            alertMessage.append(severityEmoji).append(" ").append(notification.getTitle()).append(" ").append(severityEmoji).append("\n\n");
            
            // Add module info
            if (moduleName != null && !moduleName.isEmpty()) {
                alertMessage.append("Módulo: ").append(moduleName).append("\n");
            }
            
            // Add event type
            String eventTypeDisplay = getEventTypeDisplay(eventType);
            alertMessage.append("Tipo de Evento: ").append(eventTypeDisplay).append("\n");
            
            // Add severity level
            String severityDisplay = getSeverityDisplay(severity);
            alertMessage.append("Severidad: ").append(severityDisplay).append("\n");
            
            // Add battery level if available
            if (metaData.containsKey("metadata")) {
                Map<String, Object> additionalMetadata = (Map<String, Object>) metaData.get("metadata");
                if (additionalMetadata != null) {
                    String batteryLevel = getStringValue(additionalMetadata, "batteryLevel");
                    String estimatedDuration = getStringValue(additionalMetadata, "estimatedDuration");
                    
                    if (batteryLevel != null && !batteryLevel.isEmpty() && !"null".equals(batteryLevel)) {
                        alertMessage.append("Nivel de Batería: ").append(batteryLevel).append("%\n");
                    }
                    
                    if (estimatedDuration != null && !estimatedDuration.isEmpty() && !"null".equals(estimatedDuration)) {
                        alertMessage.append("Duración Estimada: ").append(estimatedDuration).append("\n");
                    }
                }
            }
            
            // Add action recommendation based on event type
            String actionRecommendation = getActionRecommendation(eventType);
            if (actionRecommendation != null && !actionRecommendation.isEmpty()) {
                alertMessage.append("\n📋 Acción Recomendada:\n").append(actionRecommendation);
            }
            
            // Show toast with alert details
            Toast.makeText(context, alertMessage.toString(), Toast.LENGTH_LONG).show();
            
            // Navigate to module if moduleId is available
            if (moduleId != null && !moduleId.isEmpty()) {
                try {
                    Log.d(TAG, "Fetching details for module ID: " + moduleId);
                    fetchModuleDetails(context, moduleId);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid module ID format: " + moduleId, e);
                }
            } else {
                Log.w(TAG, "No module ID provided in power alert notification");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling power alert notification", e);
            Toast.makeText(context, "Error procesando alerta de energía: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getStringValue(Map<String, Object> map, String key) {
        if (map != null && map.containsKey(key)) {
            Object value = map.get(key);
            return value != null ? String.valueOf(value) : "";
        }
        return "";
    }

    private String getSeverityEmoji(String severity) {
        if (severity == null) return "⚠️";
        
        switch (severity.toLowerCase()) {
            case "critical":
                return "🚨";
            case "error":
                return "🔴";
            case "warning":
                return "⚠️";
            case "info":
                return "📘";
            default:
                return "⚠️";
        }
    }

    private String getSeverityDisplay(String severity) {
        if (severity == null) return "Desconocida";
        
        switch (severity.toLowerCase()) {
            case "critical":
                return "Crítica";
            case "error":
                return "Error";
            case "warning":
                return "Advertencia";
            case "info":
                return "Información";
            default:
                return severity;
        }
    }

    private String getEventTypeDisplay(String eventType) {
        if (eventType == null) return "Evento Desconocido";
        
        switch (eventType.toLowerCase()) {
            case "outage":
                return "Interrupción Eléctrica";
            case "restored":
                return "Energía Restaurada";
            case "low_battery":
                return "Batería Baja";
            case "voltage_drop":
                return "Caída de Voltaje";
            case "power_fluctuation":
                return "Fluctuación de Energía";
            default:
                return eventType;
        }
    }

    private String getActionRecommendation(String eventType) {
        if (eventType == null) return null;
        
        switch (eventType.toLowerCase()) {
            case "outage":
                return "• Verificar estado de sistemas críticos\n• Monitorear nivel de batería de respaldo\n• Contactar al proveedor eléctrico si es necesario";
            case "low_battery":
                return "• Restaurar energía eléctrica principal\n• Verificar sistema de carga de baterías\n• Preparar generador de emergencia si está disponible";
            case "voltage_drop":
            case "power_fluctuation":
                return "• Revisar conexiones eléctricas\n• Verificar estabilidad del suministro\n• Considerar usar regulador de voltaje";
            case "restored":
                return "• Verificar que todos los sistemas estén funcionando correctamente\n• Comprobar integridad de los datos durante la interrupción";
            default:
                return "• Revisar el estado del módulo\n• Contactar soporte técnico si persiste";
        }
    }

    private void fetchModuleDetails(Context context, String moduleId) {
        if (context == null) {
            Log.e(TAG, "Context is null");
            return;
        }

        try {
            Log.d(TAG, "Attempting to parse module ID: " + moduleId);
            String cleanModuleId = moduleId.contains(".") ? moduleId.split("\\.")[0] : moduleId;
            Log.d(TAG, "Cleaned module ID: " + cleanModuleId);
            int moduleIdInt = Integer.parseInt(cleanModuleId);
            
            if (moduleIdInt <= 0) {
                Log.e(TAG, "Invalid module ID: " + moduleId);
                Toast.makeText(context, 
                    "ID de módulo inválido", 
                    Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d(TAG, "Requesting module details from API for module ID: " + moduleIdInt);

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
                    Log.e(TAG, "Failed to fetch module details for module ID: " + moduleIdInt, t);
                    Toast.makeText(context, 
                        "Error al cargar los detalles del módulo: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            }, moduleIdInt);
            
            Log.d(TAG, "API request sent for module ID: " + moduleIdInt);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid module ID format: " + moduleId, e);
            Toast.makeText(context, 
                "Formato de ID de módulo inválido", 
                Toast.LENGTH_SHORT).show();
        }
    }

    private void openModuleActivity(Context context, Module module) {
        if (context == null || module == null) {
            Log.e(TAG, "Context or module is null");
            return;
        }
        
        try {
            int moduleId = module.getId();
            Log.d(TAG, "Preparing to open module activity for module ID: " + moduleId);
            
            if (moduleId <= 0) {
                Log.e(TAG, "Invalid module ID for navigation: " + moduleId);
                Toast.makeText(context, 
                    "No se puede navegar: ID de módulo inválido", 
                    Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(context, ViewModuleActivity.class);
            intent.putExtra(ViewModuleActivity.ARG_MODULE_ID, moduleId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            Log.d(TAG, "Starting ViewModuleActivity with module ID: " + moduleId + 
                       ", module name: " + module.getName());
            context.startActivity(intent);
            Log.d(TAG, "ViewModuleActivity started successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error opening module activity", e);
            Toast.makeText(context, 
                "Error al abrir detalles del módulo: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }
}

