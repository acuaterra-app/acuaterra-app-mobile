package com.example.monitoreoacua.firebase;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.monitoreoacua.firebase.NotificationManager;

import androidx.appcompat.app.AlertDialog;

import com.example.monitoreoacua.R;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Helper class for displaying in-app notifications when the app is in the foreground.
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

    /**
     * Show an in-app notification dialog
     *
     * @param title         The notification title
     * @param body          The notification body
     * @param data          The notification data payload
     */
    public static void showInAppNotification(String title, String body, Map<String, String> data) {
        // Extract messageType from data if it exists, default to "info"
        String messageType = data.containsKey("messageType") ? data.get("messageType") : "info";

        if (!isAppInForeground || currentActivity == null || currentActivity.get() == null) {
            return;
        }

        // Run on UI thread
        new Handler(Looper.getMainLooper()).post(() -> {
            Activity activity = currentActivity.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            // Create the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.in_app_notification_dialog, null);
            
            // Set the title and body
            TextView titleTextView = dialogView.findViewById(R.id.notification_title);
            TextView bodyTextView = dialogView.findViewById(R.id.notification_body);
            Button dismissButton = dialogView.findViewById(R.id.notification_dismiss_button);

            titleTextView.setText(title);
            bodyTextView.setText(body);

            // Apply style based on message type
            applyMessageTypeStyle(dialogView, messageType);

            builder.setView(dialogView);
            
            // Create and configure the dialog
            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setGravity(Gravity.TOP);
                
                android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                int marginInDp = (int) (10 * activity.getResources().getDisplayMetrics().density);
                params.y = marginInDp; // Set 10dp top margin
                dialog.getWindow().setAttributes(params);

                // Set animation for the dialog (slide down from top)
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationSlideDown;
            }
            
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);
            
            // Handle dismiss button click
            dismissButton.setOnClickListener(v -> dialog.dismiss());
            
            // Make the entire dialog clickable to trigger the notification action
            dialogView.setOnClickListener(v -> {
                dialog.dismiss();
                handleNotificationClick(activity, data);
            });
            
            // Show the dialog
            dialog.show();
            
            // Auto dismiss after 5 seconds without performing any action
            new Handler().postDelayed(() -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }, 5000);
        });
    }

    /**
     * Handle click on the notification - same behavior as clicking on a notification in the tray
     */
    private static void handleNotificationClick(Activity activity, Map<String, String> data) {
        Intent intent = NotificationManager.getInstance().createNotificationIntent(activity, data);
        activity.startActivity(intent);
    }

    /**
     * Apply visual style to the notification dialog based on message type
     * 
     * @param dialogView    The dialog view to style
     * @param messageType   The type of message (success, warning, error, info)
     */
    private static void applyMessageTypeStyle(View dialogView, String messageType) {
        // Apply dialog background and border using utility class
        com.example.monitoreoacua.utils.NotificationStyleUtils.applyDialogStyle(dialogView, messageType);
        
        // Apply text color to title and body
        TextView titleTextView = dialogView.findViewById(R.id.notification_title);
        TextView bodyTextView = dialogView.findViewById(R.id.notification_body);
        
        com.example.monitoreoacua.utils.NotificationStyleUtils.applyTextColor(
            messageType, titleTextView, bodyTextView);
    }
}

