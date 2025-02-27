package com.example.monitoreoacua.service;

import com.example.monitoreoacua.models.objects.Modulo;
import com.example.monitoreoacua.models.response.ModuloResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiModulosService {

    // Obtener la lista de módulos
    @GET("/api/modulos/listarModuloMVC")
    Call<List<Modulo>> getAllModules(@Header("Authorization") String token); // Respuesta directa como lista

    // Obtener un módulo por id
    //@GET("/api/modulos/moduloIdMVC/{id}")
    //Call<ModuloResponse> getModulo(@Path("id") int id);

    // Registrar un nuevo módulo
    //@POST("/api/registerModMVC")
    //Call<RegisterModuleResponse> createModulo(@Body Modulo modulo);

    // Actualizar un módulo existente
    //@PUT("/api/modulos/editarModuloMVC/{id}")
    //Call<ModuloResponse> updateModulo(@Path("id") int id, @Body Modulo modulo);

    // Eliminar un módulo
    //@DELETE("/api/modulos/eliminarModuloMVC/{id}")
    //Call<ModuloResponse> deleteModulo(@Path("id") int id);
}
