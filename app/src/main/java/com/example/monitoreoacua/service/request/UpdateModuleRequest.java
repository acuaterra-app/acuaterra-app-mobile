package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.response.RegisterModuleResponse;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Clase para realizar solicitudes de actualización de módulos al API.
 * Utiliza el endpoint PUT /api/v2/owner/modules/{id} definido en ApiModulesService.
 */
public class UpdateModuleRequest extends BaseRequest {
    private static final String TAG = "UpdateModuleRequest";

    /**
     * Actualiza un módulo existente con los nuevos datos proporcionados.
     *
     * @param moduleId ID del módulo a actualizar
     * @param module Objeto Module con los datos actualizados
     * @param callback Callback para manejar la respuesta o errores
     */
    public void updateModule(int moduleId, Module module, OnApiRequestCallback<RegisterModuleResponse, Throwable> callback) {
        Log.d(TAG, "updateModule: " + moduleId);
        Log.d(TAG, "UpdateModuleRequest - Realizando llamada API para actualizar módulo ID: " + moduleId);
        
        ApiModulesService apiModulesService = ApiClient.getClient().create(ApiModulesService.class);

        // Obtener el token de autorización
        String authToken = getAuthToken();
        Log.d(TAG, "Parámetros de llamada API - token: " + (authToken != null ? "token válido" : "null") + ", id: " + moduleId);
        
        // Realizar la llamada al endpoint de actualización
        apiModulesService.updateModule(authToken, moduleId, module).enqueue(new Callback<RegisterModuleResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterModuleResponse> call, @NonNull Response<RegisterModuleResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    String errorMessage = "Error actualizando módulo: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            errorMessage += " - " + errorBody;
                        } catch (Exception e) {
                            Log.e(TAG, "Error leyendo error body", e);
                        }
                    }
                    Log.e(TAG, errorMessage);
                    callback.onFail(new Exception(errorMessage));
                    return;
                }
                
                RegisterModuleResponse updateResponse = response.body();
                if (updateResponse == null) {
                    String errorMessage = "Los datos del módulo actualizado son nulos en la respuesta";
                    Log.e(TAG, errorMessage);
                    callback.onFail(new Exception(errorMessage));
                    return;
                }
                
                Log.d(TAG, "Módulo actualizado exitosamente");
                callback.onSuccess(updateResponse);
            }

            @Override
            public void onFailure(@NonNull Call<RegisterModuleResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de red al actualizar módulo: " + t.getMessage(), t);
                callback.onFail(t);
            }
        });
    }
}

