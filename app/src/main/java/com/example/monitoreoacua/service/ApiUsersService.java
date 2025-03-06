package com.example.monitoreoacua.service;

import com.example.monitoreoacua.models.request.LoginRequest;
import com.example.monitoreoacua.models.request.LogoutRequest;
import com.example.monitoreoacua.models.response.ApiResponse;
import com.example.monitoreoacua.models.response.LoginResponse;
import com.example.monitoreoacua.models.response.LogoutResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Interface for user-related API operations.
 * Contains methods for authentication and user management.
 */
public interface ApiUsersService {

    /**
     * Authenticates a user with the provided credentials.
     * @param loginRequest The login request containing email and password
     * @return A Call object with LoginResponse that contains authentication token and user information
     */
    @POST("/api/v2/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    /**
     * Logs out the current user, invalidating their session.
     * @param logoutRequest The logout request
     * @return A Call object with LogoutResponse containing a success message
     */
    @POST("/api/v2/auth/logout")
    Call<LogoutResponse> logout(@Body LogoutRequest logoutRequest);
}

