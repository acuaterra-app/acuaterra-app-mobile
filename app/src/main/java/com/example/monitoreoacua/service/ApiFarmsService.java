package com.example.monitoreoacua.service;


import com.example.monitoreoacua.service.response.FarmResponse;
import com.example.monitoreoacua.service.response.ListFarmResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Interface for farm-related API operations.
 * Contains methods for fetching farms and farm details.
 */
public interface ApiFarmsService {

    @GET("api/v2/owner/farms")
    Call<ListFarmResponse> getFarms(@Header("Authorization") String token);

    /**
     * Get a farm by its ID
     * 
     * @param token The authorization token
     * @param id The ID of the farm to fetch
     * @return A Call object with the farm response
     */
    @GET("api/v2/shared/farms/{id}")
    Call<FarmResponse> getFarmById(@Header("Authorization") String token, @Path("id") int id);
}
