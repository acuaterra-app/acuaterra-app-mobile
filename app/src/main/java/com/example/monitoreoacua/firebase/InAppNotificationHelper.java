package com.example.monitoreoacua.firebase;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Helper class to track application foreground/background state.
 * All popup functionality has been removed.
 */
public class InAppNotificationHelper {

    private static WeakReference<Activity> currentActivity;
    private static boolean isAppInForeground = false;

    /**
     * Initialize the helper with application lifecycle callbacks to track foreground state
     */
    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            private int activityReferences = 0;

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activityReferences == 0) {
                    // App went to foreground
                    isAppInForeground = true;
                }
                activityReferences++;
                currentActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityReferences--;
                if (activityReferences == 0) {
                    // App went to background
                    isAppInForeground = false;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (currentActivity != null && currentActivity.get() == activity) {
                    currentActivity = null;
                }
            }
        });
    }

    /**
     * Check if the app is currently in the foreground
     */
    public static boolean isAppInForeground() {
        return isAppInForeground;
    }
}
