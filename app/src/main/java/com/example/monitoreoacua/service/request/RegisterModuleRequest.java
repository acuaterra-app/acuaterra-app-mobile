package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.response.ApiError;
import com.example.monitoreoacua.service.response.RegisterModuleResponse;
import com.example.monitoreoacua.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.monitoreoacua.business.models.Module;

public class RegisterModuleRequest extends BaseRequest {

    private static final String TAG = "RegisterModuleRequest";

    public RegisterModuleRequest(){
        super();
        setRequiresAuthentication(true);
    }

    public void registerModuleRequest(Module module, OnApiRequestCallback<RegisterModuleResponse, Throwable> callback) {
        ApiModulesService apiModulesService = ApiClient.getClient().create(ApiModulesService.class);

        String token = getAuthToken();
        Log.d("RegisterModules", "Token recibido: " + token);

        apiModulesService.createModule(token, module).enqueue(new Callback<RegisterModuleResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterModuleResponse> call,@NonNull Response<RegisterModuleResponse> response) {
                if (response.isSuccessful()) {
                    RegisterModuleResponse registerModuleResponse = response.body();
                    callback.onSuccess(registerModuleResponse);
                } else {
                    ApiError apiError = ErrorUtils.parseError(response);
                    Log.e(TAG, "═══════════════════════════════════════════════════════════════");
                    Log.e(TAG, "❌ ERROR EN EL REGISTRO DE MÓDULO - CÓDIGO: " + response.code());
                    Log.e(TAG, "═══════════════════════════════════════════════════════════════");
                    
                    // Log del mensaje principal de error
                    if (apiError.getMessage() != null) {
                        Log.e(TAG, "📋 Mensaje principal: " + apiError.getMessage());
                    }
                    
                    // Log detallado de cada campo con error
                    StringBuilder detailedErrorMessage = new StringBuilder();
                    detailedErrorMessage.append("Error en el registro - Detalles:\n");
                    
                    if (apiError.getErrors() != null && !apiError.getErrors().isEmpty()) {
                        Log.e(TAG, "\n🔍 ERRORES POR CAMPO:");
                        Log.e(TAG, "───────────────────────────────────────────────────────────────");
                        
                        for (ApiError.ErrorDetail errorDetail : apiError.getErrors()) {
                            String fieldName = errorDetail.getPath() != null ? errorDetail.getPath() : "Campo desconocido";
                            String errorMessage = errorDetail.getMsg() != null ? errorDetail.getMsg() : "Error no especificado";
                            String fieldValue = errorDetail.getValue() != null ? errorDetail.getValue() : "N/A";
                            String fieldLocation = errorDetail.getLocation() != null ? errorDetail.getLocation() : "N/A";
                            
                            Log.e(TAG, "\n🏷️  CAMPO: " + fieldName.toUpperCase());
                            Log.e(TAG, "   ❌ Error: " + errorMessage);
                            Log.e(TAG, "   📝 Valor enviado: " + fieldValue);
                            Log.e(TAG, "   📍 Ubicación: " + fieldLocation);
                            Log.e(TAG, "   💡 Solución: " + getFieldSolution(fieldName, errorMessage));
                            
                            // Añadir al mensaje de error detallado
                            detailedErrorMessage.append("\n• ").append(getFormFieldDisplayName(fieldName))
                                .append(": ").append(errorMessage)
                                .append("\n  💡 ").append(getFieldSolution(fieldName, errorMessage));
                        }
                        
                        Log.e(TAG, "───────────────────────────────────────────────────────────────");
                    } else {
                        Log.e(TAG, "⚠️ No se encontraron detalles específicos del error");
                        detailedErrorMessage.append("\nError general: ").append(response.message());
                    }
                    
                    Log.e(TAG, "\n📊 INFORMACIÓN TÉCNICA:");
                    Log.e(TAG, "   • Código HTTP: " + response.code());
                    Log.e(TAG, "   • Mensaje HTTP: " + response.message());
                    Log.e(TAG, "   • URL: " + response.raw().request().url());
                    Log.e(TAG, "═══════════════════════════════════════════════════════════════\n");
                    
                    callback.onFail(new Throwable(detailedErrorMessage.toString()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterModuleResponse> call,@NonNull Throwable t) {
                Log.d(TAG, "Error with response: " + t);
                callback.onFail(t);
            }
        });
    }
    
    /**
     * Obtiene el nombre amigable del campo para mostrar al usuario
     */
    private String getFormFieldDisplayName(String fieldName) {
        switch (fieldName.toLowerCase()) {
            case "name":
                return "Nombre del módulo";
            case "location":
                return "Ubicación";
            case "latitude":
                return "Latitud";
            case "longitude":
                return "Longitud";
            case "fish_species":
            case "fishspecies":
                return "Especie de pez";
            case "fish_quantity":
            case "fishquantity":
                return "Cantidad de peces";
            case "fish_age":
            case "fishage":
                return "Edad de los peces";
            case "module_dimensions":
            case "moduledimensions":
            case "volume_unit":
                return "Dimensiones del módulo";
            case "id_farm":
            case "idfarm":
                return "ID de la granja";
            case "users":
                return "Usuarios asignados";
            default:
                return fieldName;
        }
    }
    
    /**
     * Obtiene una solución específica para cada tipo de error de campo
     */
    private String getFieldSolution(String fieldName, String errorMessage) {
        String field = fieldName.toLowerCase();
        String error = errorMessage.toLowerCase();
        
        switch (field) {
            case "name":
                if (error.contains("required") || error.contains("requerido") || error.contains("empty")) {
                    return "Ingrese un nombre único para el módulo (ej: 'Estanque Principal A')";
                } else if (error.contains("duplicate") || error.contains("exists") || error.contains("único")) {
                    return "Este nombre ya existe. Pruebe con: 'Módulo-[fecha]' o 'Estanque-[número]'";
                }
                break;
                
            case "location":
                return "Ingrese una descripción de ubicación (ej: 'Sector Norte - Estanque 1')";
                
            case "latitude":
                if (error.contains("format") || error.contains("formato") || error.contains("invalid")) {
                    return "Use formato decimal: 4.610847 (sin símbolos, solo números y punto)";
                } else if (error.contains("range") || error.contains("rango")) {
                    return "La latitud debe estar entre -90 y 90 grados";
                }
                return "Ejemplo de latitud válida: 4.610847";
                
            case "longitude":
                if (error.contains("format") || error.contains("formato") || error.contains("invalid")) {
                    return "Use formato decimal: -74.082031 (sin símbolos, solo números y punto)";
                } else if (error.contains("range") || error.contains("rango")) {
                    return "La longitud debe estar entre -180 y 180 grados";
                }
                return "Ejemplo de longitud válida: -74.082031";
                
            case "fish_species":
            case "fishspecies":
                return "Ingrese una especie válida (ej: 'Tilapia', 'Trucha', 'Cachama')";
                
            case "fish_quantity":
            case "fishquantity":
                if (error.contains("integer") || error.contains("number") || error.contains("número")) {
                    return "Ingrese solo números enteros sin decimales (ej: 100, 250, 500)";
                } else if (error.contains("positive") || error.contains("mayor")) {
                    return "La cantidad debe ser mayor a cero (ej: 50)";
                }
                return "Ingrese un número entero positivo (ej: 100)";
                
            case "fish_age":
            case "fishage":
                if (error.contains("integer") || error.contains("number") || error.contains("número")) {
                    return "Ingrese solo el número de meses sin texto (ej: 6, 12, 18)";
                } else if (error.contains("positive") || error.contains("mayor")) {
                    return "La edad debe ser mayor a cero meses";
                }
                return "Ingrese solo números que representen meses (ej: 6 para 6 meses)";
                
            case "module_dimensions":
            case "moduledimensions":
            case "volume_unit":
                if (error.contains("format") || error.contains("formato") || error.contains("pattern")) {
                    return "Use el formato: anchoxlargoxalto (ej: 2x3x1.5 o 10x8x2)";
                }
                return "Formato correcto: anchoxlargoxalto (ej: 2x3x1.5)";
                
            case "id_farm":
            case "idfarm":
                return "Verifique que la granja seleccionada sea válida";
                
            case "users":
                return "Verifique que los usuarios asignados sean válidos";
                
            default:
                if (error.contains("required") || error.contains("requerido")) {
                    return "Este campo es obligatorio";
                } else if (error.contains("format") || error.contains("formato")) {
                    return "Verifique el formato del campo";
                } else if (error.contains("invalid") || error.contains("inválido")) {
                    return "El valor ingresado no es válido";
                }
                return "Verifique el valor ingresado según las especificaciones";
        }
        
        return "Verifique el formato y contenido del campo";
    }
}
