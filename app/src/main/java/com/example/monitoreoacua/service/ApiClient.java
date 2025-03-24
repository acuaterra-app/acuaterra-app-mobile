package com.example.monitoreoacua.service;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://assessment-exercise-reservation-replication.trycloudflare.com"; // Special IP for accessing host from emulator
    //private static final String BASE_URL = "http://10.0.2.2:9000"; // Special IP for accessing host from emulator
    //private static final String BASE_URL = "https://backend.acuaterra.tech"; // Special IP for accessing host from emulator
    private static Retrofit retrofit;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
