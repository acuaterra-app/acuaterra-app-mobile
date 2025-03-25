package com.example.monitoreoacua.utils;

import android.app.Application;
import android.content.Context;
import com.example.monitoreoacua.firebase.InAppNotificationHelper;

/**
 * Class to provide static access to the application context.
 * This allows non-Activity classes to access the application context.
 */
public class ApplicationContextProvider extends Application {
    
    private static Context applicationContext;
    
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        // Initialize the InAppNotificationHelper to track app lifecycle
        InAppNotificationHelper.init(this);
    }
    
    /**
     * Returns the application context.
     * @return Application context
     */
    public static Context getContext() {
        return applicationContext;
    }
}

