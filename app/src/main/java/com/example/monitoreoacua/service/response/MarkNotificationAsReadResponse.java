package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.business.models.Notification;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MarkNotificationAsReadResponse extends ApiResponse<Notification> {

    public Notification getNotification() {
        return getFirstDataItem();
    }
}

