package com.example.monitoreoacua.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

/**
 * Utility class for applying styles to notifications based on message type
 */
public class NotificationStyleUtils {

    private static final String TAG = "NotificationStyleUtils";

    // Color constants for different message types
    private static final int SUCCESS_BACKGROUND = Color.parseColor("#DFFFD8"); // Vibrant light green
    private static final int SUCCESS_TEXT = Color.parseColor("#2E8B57"); // Sea green
    private static final int SUCCESS_BORDER = Color.parseColor("#4CAF50"); // Bright green

    private static final int WARNING_BACKGROUND = Color.parseColor("#FFF3D4"); // Warm amber light
    private static final int WARNING_TEXT = Color.parseColor("#FF8C00"); // Dark amber
    private static final int WARNING_BORDER = Color.parseColor("#FFC107"); // Amber gold

    private static final int ERROR_BACKGROUND = Color.parseColor("#FFE6E6"); // Light red
    private static final int ERROR_TEXT = Color.parseColor("#D32F2F"); // Bright red
    private static final int ERROR_BORDER = Color.parseColor("#F44336"); // Strong red

    private static final int INFO_BACKGROUND = Color.parseColor("#D4F1F9"); // Sky blue
    private static final int INFO_TEXT = Color.parseColor("#0277BD"); // Deep blue
    private static final int INFO_BORDER = Color.parseColor("#2196F3"); // Bright blue

    /**
     * Get the background color for a message type
     *
     * @param messageType The type of message (success, warning, error, info)
     * @return The color value for the background
     */
    public static int getBackgroundColor(String messageType) {
        switch (messageType.toLowerCase()) {
            case "success":
                return SUCCESS_BACKGROUND;
            case "warning":
                return WARNING_BACKGROUND;
            case "error":
                return ERROR_BACKGROUND;
            case "info":
            default:
                return INFO_BACKGROUND;
        }
    }

    /**
     * Get the text color for a message type
     *
     * @param messageType The type of message (success, warning, error, info)
     * @return The color value for the text
     */
    public static int getTextColor(String messageType) {
        switch (messageType.toLowerCase()) {
            case "success":
                return SUCCESS_TEXT;
            case "warning":
                return WARNING_TEXT;
            case "error":
                return ERROR_TEXT;
            case "info":
            default:
                return INFO_TEXT;
        }
    }

    /**
     * Get the border color for a message type
     *
     * @param messageType The type of message (success, warning, error, info)
     * @return The color value for the border
     */
    public static int getBorderColor(String messageType) {
        switch (messageType.toLowerCase()) {
            case "success":
                return SUCCESS_BORDER;
            case "warning":
                return WARNING_BORDER;
            case "error":
                return ERROR_BORDER;
            case "info":
            default:
                return INFO_BORDER;
        }
    }

    /**
     * Apply style to a CardView based on message type
     *
     * @param cardView    The CardView to style
     * @param messageType The type of message (success, warning, error, info)
     */
    public static void applyStyleToCardView(CardView cardView, String messageType) {
        if (cardView == null) return;

        Log.d(TAG, "Applying style for message type: '" + messageType + "'");

        int backgroundColor = getBackgroundColor(messageType);
        int borderColor = getBorderColor(messageType);

        cardView.setCardBackgroundColor(backgroundColor);
        
        // You can customize additional CardView properties here if needed
        cardView.setCardElevation(4f); // Slight elevation
        cardView.setRadius(16f); // Rounded corners
        
        // For border, we'd need to use a custom solution as CardView doesn't have direct border support
        // This would typically be done via a background drawable in the layout XML
    }

    /**
     * Apply style to text views based on message type
     *
     * @param messageType    The type of message (success, warning, error, info) 
     * @param textViews      The TextView(s) to style
     */
    public static void applyTextColor(String messageType, TextView... textViews) {
        if (textViews == null || textViews.length == 0) return;

        int textColor = getTextColor(messageType);
        
        for (TextView textView : textViews) {
            if (textView != null) {
                textView.setTextColor(textColor);
            }
        }
    }

    
    /**
     * Apply the appropriate color to an unread indicator view based on message type
     *
     * @param indicatorView The unread indicator view to style
     * @param messageType   The type of message (success, warning, error, info)
     */
    public static void applyUnreadIndicatorColor(View indicatorView, String messageType) {
        if (indicatorView == null) return;
        
        Log.d(TAG, "Applying unread indicator style for message type: '" + messageType + "'");
        
        // Use the border color for the unread indicator to make it stand out
        int indicatorColor = getBorderColor(messageType);
        
        // Apply solid color background to the indicator
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(indicatorColor);
        indicatorView.setBackground(drawable);
    }
}

