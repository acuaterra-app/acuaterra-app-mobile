package com.example.monitoreoacua.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.service.response.ListModuleResponse;

/**
 * Interface for user-related API operations.
 * Contains methods for module management
 */
public interface ApiModulesService {

    @GET("/api/v2/shared/modules/{farm_id}")
    Call<ListModuleResponse> getModules(
            @Path("farmId") int farmId,
            @Header("Authorization") String token
    );

    @POST("/api/v2/owner/modules")
    Call<Void> createModule(
            @Body Module module,
            @Header("Authorization") String token
    );
}


