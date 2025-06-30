package com.example.monitoreoacua.utils;

import android.util.Log;
import android.widget.EditText;

public class ApiErrorUtils {

    private static final String TAG = "ApiErrorUtils";

    /**
     * Parsea el mensaje de error de la API para usuarios.
     */
    public static String parseUserApiErrorMessage(Throwable error, boolean isUpdate) {
        String errorMessage = error.getMessage() != null ? error.getMessage().toLowerCase() : "";
        
        if (errorMessage.contains("dni")) {
            return "❌ DNI: El DNI ingresado no es válido o ya está registrado";
        } else if (errorMessage.contains("email")) {
            return "❌ Email: El email ingresado no es válido o ya está en uso";
        } else if (errorMessage.contains("contact") || errorMessage.contains("contacto")) {
            return "❌ Contacto: Formato de contacto inválido (ejemplo: +593 98-765-4321)";
        } else if (errorMessage.contains("name") || errorMessage.contains("nombre")) {
            return "❌ Nombre: El nombre ingresado no es válido o es muy corto";
        } else if (errorMessage.contains("address") || errorMessage.contains("direccion")) {
            return "❌ Dirección: La dirección ingresada no es válida";
        } else if (errorMessage.contains("duplicate") || errorMessage.contains("duplicado")) {
            return "❌ Ya existe un usuario con estos datos. Verifique el DNI y email";
        } else if (errorMessage.contains("required") || errorMessage.contains("requerido")) {
            return "❌ Todos los campos son obligatorios. Complete la información faltante";
        } else {
            return "❌ Error al " + (isUpdate ? "actualizar" : "registrar") + " usuario: " + 
                   (error.getMessage() != null ? error.getMessage() : "Verifique todos los campos e intente nuevamente");
        }
    }

    /**
     * Parsea el mensaje de error de la API para módulos.
     */
    public static String parseModuleApiErrorMessage(Throwable error) {
        String errorMessage = error.getMessage() != null ? error.getMessage().toLowerCase() : "";
        
        if (errorMessage.contains("fish age") || errorMessage.contains("edad")) {
            return "❌ Edad de peces: Debe ser un número entero positivo (ejemplo: 6 para 6 meses)";
        } else if (errorMessage.contains("volume_unit") || errorMessage.contains("dimension")) {
            return "❌ Dimensiones: Use el formato anchoxlargoxalto (ejemplo: 2x3x1.5)";
        } else if (errorMessage.contains("quantity") || errorMessage.contains("cantidad")) {
            return "❌ Cantidad de peces: Debe ser un número entero positivo";
        } else if (errorMessage.contains("location") || errorMessage.contains("ubicacion")) {
            return "❌ Ubicación: Ingrese una ubicación válida";
        } else if (errorMessage.contains("longitude") || errorMessage.contains("latitude") || errorMessage.contains("coordenada")) {
            return "❌ Coordenadas: Deben estar en formato decimal válido (lat: 4.610, lon: -74.082)";
        } else if (errorMessage.contains("name") || errorMessage.contains("nombre")) {
            return "❌ Nombre del módulo: No puede estar vacío y debe ser único";
        } else if (errorMessage.contains("fish") || errorMessage.contains("especie")) {
            return "❌ Especie de pez: Ingrese una especie válida";
        } else if (errorMessage.contains("farm") || errorMessage.contains("granja")) {
            return "❌ Error de granja: Verifique que la granja sea válida";
        } else if (errorMessage.contains("duplicate") || errorMessage.contains("duplicado")) {
            return "❌ Ya existe un módulo con estos datos. Verifique el nombre y ubicación";
        } else {
            return "❌ Error al registrar módulo: " + (error.getMessage() != null ? error.getMessage() : "Verifique todos los campos e intente nuevamente");
        }
    }

    /**
     * Resalta el campo que tiene el error según el mensaje de la API (para usuarios).
     */
    public static void highlightUserErrorField(Throwable error, EditText dniEditText, EditText emailEditText, 
                                              EditText contactEditText, EditText nameEditText, EditText addressEditText) {
        String errorMessage = error.getMessage() != null ? error.getMessage().toLowerCase() : "";
        
        if (errorMessage.contains("dni")) {
            dniEditText.setError("El DNI ingresado no es válido o ya está registrado");
            dniEditText.requestFocus();
        } else if (errorMessage.contains("email")) {
            emailEditText.setError("El email ingresado no es válido o ya está en uso");
            emailEditText.requestFocus();
        } else if (errorMessage.contains("contact") || errorMessage.contains("contacto")) {
            contactEditText.setError("Formato de contacto inválido (ejemplo: +593 98-765-4321)");
            contactEditText.requestFocus();
        } else if (errorMessage.contains("name") || errorMessage.contains("nombre")) {
            nameEditText.setError("El nombre no es válido o es muy corto");
            nameEditText.requestFocus();
        } else if (errorMessage.contains("address") || errorMessage.contains("direccion")) {
            addressEditText.setError("La dirección no es válida");
            addressEditText.requestFocus();
        }
    }

    /**
     * Resalta el campo que tiene el error según el mensaje de la API (para módulos).
     */
    public static void highlightModuleErrorField(Throwable error, EditText etFishAge, EditText etVolumeUnit, 
                                                EditText etFishQuantity, EditText etLocation, EditText etLongitude, 
                                                EditText etLatitude, EditText etModuleName, EditText etFishType) {
        String errorMessage = error.getMessage() != null ? error.getMessage().toLowerCase() : "";
        
        if (errorMessage.contains("fish age") || errorMessage.contains("edad")) {
            etFishAge.setError("Ingrese solo el número de meses (ejemplo: 6)");
            etFishAge.requestFocus();
        } else if (errorMessage.contains("volume_unit") || errorMessage.contains("dimension")) {
            etVolumeUnit.setError("Formato: anchoxlargoxalto (ejemplo: 2x3x1.5)");
            etVolumeUnit.requestFocus();
        } else if (errorMessage.contains("quantity") || errorMessage.contains("cantidad")) {
            etFishQuantity.setError("Debe ser un número entero positivo");
            etFishQuantity.requestFocus();
        } else if (errorMessage.contains("location") || errorMessage.contains("ubicacion")) {
            etLocation.setError("Ingrese una ubicación válida");
            etLocation.requestFocus();
        } else if (errorMessage.contains("longitude")) {
            etLongitude.setError("Formato inválido (ejemplo: -74.082)");
            etLongitude.requestFocus();
        } else if (errorMessage.contains("latitude")) {
            etLatitude.setError("Formato inválido (ejemplo: 4.610)");
            etLatitude.requestFocus();
        } else if (errorMessage.contains("name") || errorMessage.contains("nombre")) {
            etModuleName.setError("El nombre no puede estar vacío y debe ser único");
            etModuleName.requestFocus();
        } else if (errorMessage.contains("fish") || errorMessage.contains("especie")) {
            etFishType.setError("Ingrese una especie de pez válida");
            etFishType.requestFocus();
        }
    }
}

