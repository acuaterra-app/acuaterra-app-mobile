package com.example.monitoreoacua.service;

import com.example.monitoreoacua.service.response.ListNotificationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
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
            @Query("search") String search,
            @Query("limit") Integer limit);
            
    @GET("api/v2/shared/notifications")
    Call<ListNotificationResponse> getNotificationsByState(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("state") String state);
            
    @GET("api/v2/shared/notifications")
    Call<ListNotificationResponse> getNotificationsByState(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("state") String state,
            @Query("search") String search,
            @Query("limit") Integer limit);
}

