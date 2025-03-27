package com.example.monitoreoacua.service;

import com.example.monitoreoacua.service.request.UserRequest;
import com.example.monitoreoacua.service.response.ListUserResponse;
import com.example.monitoreoacua.service.response.UserRegisterResponse;



import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiUserService {
    @GET("api/v2/owner/users")
    Call<ListUserResponse> getUsers(
            @Header("authorization") String token
    );

    @POST("api/v2/shared/users")
    Call<UserRegisterResponse> register( @Header("authorization") String token, @Body UserRequest user);
}
