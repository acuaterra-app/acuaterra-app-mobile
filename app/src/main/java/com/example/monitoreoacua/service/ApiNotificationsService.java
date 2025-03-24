package com.example.monitoreoacua.service;

import com.example.monitoreoacua.service.response.ListNotificationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiNotificationsService {

    @GET("api/v2/shared/notifications")
    Call<ListNotificationResponse> getNotifications(@Header("Authorization") String token);
}

