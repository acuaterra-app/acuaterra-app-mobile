package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;
import java.util.Map;

/**
 * Interface for handling different types of push notifications
 */
public interface NotificationHandler {
    
    boolean canHandle(String notificationType);

    void handle(Context context, Map<String, String> data);
}

