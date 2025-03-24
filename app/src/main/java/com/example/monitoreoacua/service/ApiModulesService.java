package com.example.monitoreoacua.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import com.example.monitoreoacua.service.response.ListModuleResponse;
import com.example.monitoreoacua.service.request.CreateModuleRequest;
import com.example.monitoreoacua.service.response.CreateModuleResponse;


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
    Call<CreateModuleResponse> createModule(
            @Body CreateModuleRequest  createModuleRequest,
            @Header("Authorization") String token
    );
}


