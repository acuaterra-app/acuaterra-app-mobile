package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Notification;

import java.util.List;

public class ListNotificationResponse extends  ApiResponse<Notification>{

    public List<Notification> getAllNotification(){
        return getData();
    }
}

