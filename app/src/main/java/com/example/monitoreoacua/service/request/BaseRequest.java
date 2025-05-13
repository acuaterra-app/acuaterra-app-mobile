package com.example.monitoreoacua.service.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.monitoreoacua.utils.ApplicationContextProvider;

/**
 * Base class for all request objects in the application.
 * Contains a flag to determine if the request requires authentication.
 */
public class BaseRequest {

    private boolean requiresAuthentication;
    private static final String PREF_NAME = "user_prefs";

    public BaseRequest() {
        this.requiresAuthentication = false;
    }

    public boolean isRequiresAuthentication() {
        return requiresAuthentication;
    }

    public void setRequiresAuthentication(boolean requiresAuthentication) {
        this.requiresAuthentication = requiresAuthentication;
    }

    private static final String TAG = "BaseRequest";

    public String getAuthToken() {
        Log.d(TAG, "getAuthToken: Intentando obtener token de autenticación");
        
        Context context = ApplicationContextProvider.getContext();
        if (context == null) {
            Log.e(TAG, "getAuthToken: Error crítico - Contexto es NULL. ApplicationContextProvider no inicializado correctamente");
            return null;
        }
        
        Log.d(TAG, "getAuthToken: Contexto obtenido correctamente, buscando token en SharedPreferences");
        
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", null);
            
            if (token == null) {
                Log.e(TAG, "getAuthToken: No se encontró token en SharedPreferences");
                return null;
            } else if (token.isEmpty()) {
                Log.e(TAG, "getAuthToken: Token encontrado pero está vacío");
                return token;
            } else {
                int tokenLength = token.length();
                String tokenPreview = tokenLength > 15 
                    ? token.substring(0, 7) + "..." + token.substring(tokenLength - 7) 
                    : token;
                Log.d(TAG, "getAuthToken: Token recuperado con éxito (longitud: " + tokenLength + "): " + tokenPreview);
                return token;
            }
        } catch (Exception e) {
            Log.e(TAG, "getAuthToken: Error al acceder a SharedPreferences", e);
            return null;
        }
    }
}