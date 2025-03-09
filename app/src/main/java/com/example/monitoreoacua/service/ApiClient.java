package com.example.monitoreoacua.service;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.service.request.BaseRequest;
import com.example.monitoreoacua.utils.ApplicationContextProvider;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:9000"; // Special IP for accessing host from emulator
    //private static final String BASE_URL = "https://backend.acuaterra.tech"; // Special IP for accessing host from emulator
    private static Retrofit retrofit;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    /**
     * Interceptor for adding authentication token to requests
     */
    private static class AuthInterceptor implements Interceptor {

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            

            Request.Builder requestBuilder = originalRequest.newBuilder();
            
            /*if (requiresAuth) {
                String token = getAuthToken();
                if (token != null && !token.isEmpty()) {
                    requestBuilder.addHeader("Authorization", token);
                }
            }*/
            
            Request newRequest = requestBuilder.build();
            return chain.proceed(newRequest);
        }
    }
    
}
