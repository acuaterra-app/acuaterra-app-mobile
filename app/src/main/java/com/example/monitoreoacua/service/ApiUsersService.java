package com.example.monitoreoacua.service;

import com.example.monitoreoacua.models.request.LoginRequest;
import com.example.monitoreoacua.models.response.LoginResponse;
import com.example.monitoreoacua.models.request.RegisterRequest;
import com.example.monitoreoacua.models.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ApiUsersService {

    @POST("/api/users/registerMVC")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @POST("/api/users/loginMVC")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
