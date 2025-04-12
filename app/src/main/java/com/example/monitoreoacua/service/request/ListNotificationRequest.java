package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiNotificationsService;
import com.example.monitoreoacua.service.response.ListNotificationResponse;

public class ListNotificationRequest extends BaseRequest {

    private static final String TAG = "ListNotificationRequest";
    public ListNotificationRequest() {
        super();
        setRequiresAuthentication(true);
    }

    public void fetchTotalNotifications(OnApiRequestCallback<Integer, String> callback, int page, String state, int limit) {
         fetchNotifications(new OnApiRequestCallback<ListNotificationResponse, Throwable>() {
            @Override
            public void onSuccess(ListNotificationResponse apiResponse) {
                // Log the response for debugging
                Log.d(TAG, "Received notification response successfully");

                    int unreadNotificationsCount = 0;

                    if (apiResponse.getMeta() != null) {
                        unreadNotificationsCount = apiResponse.getMeta().getTotalItems();
                    }

                    Log.d(TAG, "Received notification response" + unreadNotificationsCount);

                    callback.onSuccess(unreadNotificationsCount);

            }

             @Override
             public void onFail(Throwable error) {
                 Log.e(TAG, "Error fetching notifications: " + error);
             }
        }, page, state, limit);
    }

    public void fetchNotifications(OnApiRequestCallback<ListNotificationResponse, Throwable> callback, int page, String state, int limit) {
        ApiNotificationsService notificationsService = ApiClient.getClient().create(ApiNotificationsService.class);

        // Verificar el token de autorización
        String authToken = getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            Log.e(TAG, "Token de autorización inválido o vacío");
            callback.onFail(new Throwable("Token de autorización inválido. Por favor inicie sesión nuevamente."));
            return;
        }

        Log.d(TAG, "Fetching notifications - page: " + page + ", state: " + state + ", limit: " + limit);
        Log.d(TAG, "URL de la petición: /api/v2/shared/notifications con parámetros page=" + page + 
                ", state=" + state + ", limit=" + limit);
        Log.d(TAG, "Token de autorización (primeros 10 caracteres): " + 
                (authToken.length() > 10 ? authToken.substring(0, 10) + "..." : authToken));

        notificationsService.getNotifications(
                authToken,
                page,
                state,
                limit
        ).enqueue(new Callback<ListNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListNotificationResponse> call, @NonNull Response<ListNotificationResponse> response) {
                Log.d(TAG, "Respuesta recibida - Código: " + response.code() + 
                        ", URL: " + call.request().url());
                
                if (response.isSuccessful() && response.body() != null) {
                    ListNotificationResponse apiResponse = response.body();
                    Log.d(TAG, "Respuesta exitosa. Datos obtenidos: " + 
                            (apiResponse.getAllNotification() != null ? 
                             apiResponse.getAllNotification().size() : "0") + " notificaciones");
                    callback.onSuccess(apiResponse);
                } else {
                    // Manejo específico de códigos de error HTTP
                    String errorMsg;
                    switch (response.code()) {
                        case 400:
                            errorMsg = "Solicitud incorrecta. Verifique los parámetros.";
                            Log.e(TAG, "Error 400: Parámetros incorrectos en la solicitud");
                            break;
                        case 401:
                            errorMsg = "No autorizado. Por favor inicie sesión nuevamente.";
                            Log.e(TAG, "Error 401: Token de autorización inválido");
                            break;
                        case 403:
                            errorMsg = "Acceso denegado. No tiene permisos para esta operación.";
                            Log.e(TAG, "Error 403: Permisos insuficientes");
                            break;
                        case 404:
                            errorMsg = "Recurso no encontrado. El endpoint de notificaciones no existe.";
                            Log.e(TAG, "Error 404: Endpoint no encontrado");
                            break;
                        case 500:
                            errorMsg = "Error interno del servidor.";
                            Log.e(TAG, "Error 500: Error interno del servidor");
                            break;
                        default:
                            errorMsg = "Error en la respuesta: código " + response.code();
                            Log.e(TAG, "Error " + response.code() + ": " + response.message());
                            break;
                    }
                    
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Cuerpo del error: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al leer el cuerpo del error", e);
                    }
                    
                    callback.onFail(new Throwable("Error al obtener notificaciones: " + errorMsg));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListNotificationResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de red al obtener notificaciones: " + t.getMessage(), t);
                
                // Mensajes de error más descriptivos según el tipo de error
                String errorMsg;
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "No se puede conectar al servidor. Verifique su conexión a internet.";
                    Log.e(TAG, "Error de host desconocido: No hay conexión a internet", t);
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Tiempo de espera agotado. El servidor tardó demasiado en responder.";
                    Log.e(TAG, "Error de timeout: El servidor tardó demasiado en responder", t);
                } else if (t instanceof javax.net.ssl.SSLHandshakeException) {
                    errorMsg = "Error de seguridad. Problema con el certificado del servidor.";
                    Log.e(TAG, "Error de SSL: Problema con el certificado del servidor", t);
                } else {
                    errorMsg = t.getMessage();
                    Log.e(TAG, "Error de conexión genérico", t);
                }
                
                callback.onFail(new Throwable("Error de conexión: " + errorMsg));
            }
        });
    }
}
