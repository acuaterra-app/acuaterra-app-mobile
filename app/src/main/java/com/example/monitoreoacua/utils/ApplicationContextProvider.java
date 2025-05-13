package com.example.monitoreoacua.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class ApplicationContextProvider extends Application {
    
    private static final String TAG = "AppContextProvider";
    private static Context applicationContext;
    
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        Log.i(TAG, "onCreate: Contexto de aplicación inicializado correctamente");
    }
    
    /**
     * Returns the application context.
     * @return Application context
     */
    public static Context getContext() {
        if (applicationContext == null) {
            Log.e(TAG, "getContext: Contexto de aplicación es NULL. ¿Está registrado el ApplicationContextProvider en AndroidManifest.xml?");
        }
        return applicationContext;
    }
}

