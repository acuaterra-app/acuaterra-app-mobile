package com.example.monitoreoacua.firebase.handlers;

import android.content.Context;

import com.example.monitoreoacua.business.models.Notification;

import java.util.Map;

/**
 * Interface for handling different types of push notifications
 */
public interface NotificationHandler {
    
    boolean canHandle(String notificationType);

    void handle(Context context, Notification notification);
}

