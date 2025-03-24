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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

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
            
            // Set dialog width to match parent with margins
            if (dialog.getWindow() != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
            }
            
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
        // Use NotificationManager to create and handle the intent
        // This will only be called when user explicitly clicks on the notification
        Intent intent = com.example.monitoreoacua.firebase.NotificationManager.getInstance().createNotificationIntent(activity, data);
        activity.startActivity(intent);
    }

    /**
     * Apply visual style to the notification dialog based on message type
     * 
     * @param dialogView    The dialog view to style
     * @param messageType   The type of message (success, warning, error, info)
     */
    private static void applyMessageTypeStyle(View dialogView, String messageType) {
        int backgroundColor;
        int textColor;
        int borderColor;

        // Add debug logging to help diagnose message type issues
        Log.d("InAppNotify", "Applying style for message type: '" + messageType + "', lowercase: '" + messageType.toLowerCase() + "'");

        // Define colors based on message type
        switch (messageType.toLowerCase()) {
            case "success":
                Log.d("InAppNotify", "Matched SUCCESS case");
                backgroundColor = Color.parseColor("#DFFFD8"); // Vibrant light green
                textColor = Color.parseColor("#2E8B57"); // Sea green
                borderColor = Color.parseColor("#4CAF50"); // Bright green
                break;
            case "warning":
                Log.d("InAppNotify", "Matched WARNING case");
                backgroundColor = Color.parseColor("#FFF3D4"); // Warm amber light
                textColor = Color.parseColor("#FF8C00"); // Dark amber
                borderColor = Color.parseColor("#FFC107"); // Amber gold
                break;
            case "error":
                Log.d("InAppNotify", "Matched ERROR case");
                backgroundColor = Color.parseColor("#FFE6E6"); // Light red
                textColor = Color.parseColor("#D32F2F"); // Bright red
                borderColor = Color.parseColor("#F44336"); // Strong red
                break;
            case "info":
            default:
                Log.d("InAppNotify", "Matched INFO/DEFAULT case");
                backgroundColor = Color.parseColor("#D4F1F9"); // Sky blue
                textColor = Color.parseColor("#0277BD"); // Deep blue
                borderColor = Color.parseColor("#2196F3"); // Bright blue
                break;
        }

        // Apply background color
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(16); // Rounded corners
        drawable.setColor(backgroundColor);
        drawable.setStroke(3, borderColor); // Add slightly thicker border for better visibility
        dialogView.setBackground(drawable);

        // Apply text color to title and body
        TextView titleTextView = dialogView.findViewById(R.id.notification_title);
        TextView bodyTextView = dialogView.findViewById(R.id.notification_body);
        if (titleTextView != null) {
            titleTextView.setTextColor(textColor);
        }
        if (bodyTextView != null) {
            bodyTextView.setTextColor(textColor);
        }
    }
}

