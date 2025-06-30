package com.example.monitoreoacua.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to translate common toast messages to Spanish
 */
public class ToastTranslator {
    
    private static final Map<String, String> translations = new HashMap<>();
    
    static {
        // Login messages
        translations.put("Please complete all fields", "Por favor complete todos los campos");
        translations.put("Please enter a valid email", "Por favor ingrese un email válido");
        translations.put("Access blocked due to multiple failed attempts", "Acceso bloqueado por múltiples intentos fallidos");
        translations.put("Failed to get device token. Please try again.", "Error al obtener token del dispositivo. Intente nuevamente.");
        translations.put("Authentication error: Invalid response", "Error de autenticación: Respuesta inválida");
        translations.put("Error processing login data", "Error procesando datos de inicio de sesión");
        translations.put("Error connecting to server:", "Error conectando al servidor:");
        
        // Connection errors
        translations.put("Error de conexión:", "Error de conexión:");
        translations.put("Error with response", "Error con la respuesta");
        translations.put("Network error", "Error de red");
        translations.put("Connection timeout", "Tiempo de conexión agotado");
        
        // Success messages
        translations.put("Welcome,", "Bienvenido,");
        translations.put("Successfully registered", "Registrado exitosamente");
        translations.put("Successfully updated", "Actualizado exitosamente");
        translations.put("Successfully deleted", "Eliminado exitosamente");
        translations.put("Operation completed successfully", "Operación completada exitosamente");
        
        // Form validation
        translations.put("Invalid email format", "Formato de email inválido");
        translations.put("Password too short", "Contraseña muy corta");
        translations.put("Fields cannot be empty", "Los campos no pueden estar vacíos");
        translations.put("Invalid data", "Datos inválidos");
        
        // Permission messages
        translations.put("Permission denied", "Permiso denegado");
        translations.put("Permission required", "Permiso requerido");
        
        // Generic messages
        translations.put("Loading...", "Cargando...");
        translations.put("No data available", "No hay datos disponibles");
        translations.put("Try again", "Inténtelo nuevamente");
        translations.put("Something went wrong", "Algo salió mal");
        translations.put("Invalid credentials", "Credenciales inválidas");
        
        // Module-specific messages
        translations.put("Module updated successfully", "Módulo actualizado exitosamente");
        translations.put("Module created successfully", "Módulo creado exitosamente");
        translations.put("Error updating module", "Error actualizando módulo");
        translations.put("Error creating module", "Error creando módulo");
        
        // User management
        translations.put("User registered successfully", "Usuario registrado exitosamente");
        translations.put("User updated successfully", "Usuario actualizado exitosamente");
        translations.put("Error registering user", "Error registrando usuario");
        translations.put("Error updating user", "Error actualizando usuario");
        
        // Monitor assignment
        translations.put("Monitors assigned successfully", "Monitores asignados exitosamente");
        translations.put("Monitors unassigned successfully", "Monitores desasignados exitosamente");
        translations.put("Error assigning monitors", "Error asignando monitores");
        translations.put("Error unassigning monitors", "Error desasignando monitores");
        translations.put("Select at least one monitor", "Seleccione al menos un monitor");
        translations.put("No monitors to unassign", "No hay monitores para desasignar");
    }
    
    /**
     * Translates a message to Spanish if a translation exists
     * @param message The original message
     * @return The translated message or the original if no translation exists
     */
    public static String translate(String message) {
        if (message == null) return null;
        
        // Check for exact match first
        String translation = translations.get(message);
        if (translation != null) {
            return translation;
        }
        
        // Check for partial matches for dynamic messages
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            if (message.contains(entry.getKey())) {
                return message.replace(entry.getKey(), entry.getValue());
            }
        }
        
        return message; // Return original if no translation found
    }
    
    /**
     * Translates welcome messages with username
     */
    public static String translateWelcome(String userName) {
        return "Bienvenido, " + userName;
    }
    
    /**
     * Translates error messages with attempt count
     */
    public static String translateInvalidCredentials(int attempt, int maxAttempts) {
        return "Credenciales inválidas. Intento " + attempt + " de " + maxAttempts;
    }
}
