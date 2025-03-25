package com.example.monitoreoacua.service;

import com.example.monitoreoacua.service.response.ListNotificationResponse;
import com.example.monitoreoacua.service.response.MarkNotificationAsReadResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiNotificationsService {

    @GET("api/v2/shared/notifications")
    Call<ListNotificationResponse> getNotifications(
            @Header("Authorization") String token,
            @Query("page") int page);

    @GET("api/v2/shared/notifications")
    Call<ListNotificationResponse> getNotifications(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("state") String state,
            @Query("limit") Integer limit);

    @PATCH("api/v2/shared/notifications/{notificationId}/read")
    Call<MarkNotificationAsReadResponse> markNotificationAsRead(
            @Header("Authorization") String token,
            @Path("notificationId") int notificationId);

}