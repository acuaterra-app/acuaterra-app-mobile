package com.example.monitoreoacua.service;

import com.example.monitoreoacua.service.request.AssignUsersRequest;
import com.example.monitoreoacua.service.request.UserRequest;
import com.example.monitoreoacua.service.response.ListUserResponse;
import com.example.monitoreoacua.service.response.UserRegisterResponse;
import com.example.monitoreoacua.service.response.UserUpdateResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiUserService {
    @GET("api/v2/owner/users")
    Call<ListUserResponse> getUsers(
            @Header("authorization") String token
    );

    @POST("api/v2/owner/users")
    Call<UserRegisterResponse> register( @Header("authorization") String token, @Body UserRequest user);

    @PUT("api/v2/owner/users/{id}")
    Call<UserUpdateResponse> updateUser(@Header("authorization") String token, @Path("id") int userId, @Body UserRequest user);

    @DELETE("api/v2/owner/users/{id}")
    Call<Void> deleteUser(@Header("Authorization") String token, @Path("id") int userId);

    @POST("api/v2/owner/modules/{moduleId}/monitors")
    Call<Void> assignUsers(
            @Header("Authorization") String token,
            @Path("moduleId") int moduleId,
            @Body AssignUsersRequest request
    );

}
