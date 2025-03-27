package com.example.monitoreoacua.service;

import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.service.response.ApiResponse;
import com.example.monitoreoacua.service.response.ListUserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiUserService {
    @GET("api/v2/owner/users")
    Call<ListUserResponse> getUsers(
            @Header("authorization") String token
    );
}
