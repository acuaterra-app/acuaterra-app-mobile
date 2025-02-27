package com.example.monitoreoacua.service;

import com.example.monitoreoacua.models.objects.Bitacora;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiBitacoraService {

    // Obtener todas las bitácoras
    @GET("/api/bitacora/listarBitacora")
    Call<List<Bitacora>> getAllBitacoras(); // Cambiado a List<Bitacora>
}
