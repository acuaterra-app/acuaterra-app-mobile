package com.example.monitoreoacua.service;


import com.example.monitoreoacua.service.response.ListFarmResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Interface for user-related API operations.
 * Contains methods for authentication and user management.
 */
public interface ApiFarmsService {

    @GET("api/v2/owner/farms")
    Call<ListFarmResponse> getFarms(@Header("Authorization") String token);

}


