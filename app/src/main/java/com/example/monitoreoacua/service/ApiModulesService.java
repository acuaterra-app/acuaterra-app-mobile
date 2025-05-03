package com.example.monitoreoacua.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.service.request.ListModulesRequest;
import com.example.monitoreoacua.service.request.RegisterModuleRequest;
import com.example.monitoreoacua.service.response.ListModuleResponse;
import com.example.monitoreoacua.service.response.GetModuleResponse;
import com.example.monitoreoacua.service.response.RegisterModuleResponse;

/**
 * Interface for user-related API operations.
 * Contains methods for module management
 */
public interface ApiModulesService {

    @GET("/api/v2/shared/modules/{farmId}")
    Call<ListModuleResponse> getModules(
            @Path("farmId") int farmId,
            @Header("Authorization") String token
    );

    @POST("/api/v2/owner/modules")
    Call<RegisterModuleResponse> createModule(
            @Header("Authorization") String token,
            @Body Module module
    );

    @GET("/api/v2/owner/modules/{id}")
    Call<GetModuleResponse> getModuleById(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @PUT("/api/v2/owner/modules/{id}")
    Call<RegisterModuleResponse> updateModule(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body Module module
    );

}