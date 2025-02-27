package com.example.monitoreoacua.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //private static final String BASE_URL = "http://192.168.100.7:3000/api/users"; // Reemplaza con tu URL
    //private static final String BASE_URL = "http://10.0.2.2:3000/api/users"; // Reemplaza con tu URL
    private static final String BASE_URL = "https://backmejorado.onrender.com"; // Reemplaza con tu URL

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
