package com.example.monitoreoacua.service;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.models.request.BaseRequest;
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
    private static final String PREF_NAME = "user_prefs";
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
        private final Gson gson = new Gson();

        private boolean isAuthRequiredFromBody(Request request) {
            try {
                // Only check POST, PUT, PATCH requests that might have a body
                if (request.body() == null || 
                    (!request.method().equals("POST") && 
                     !request.method().equals("PUT") && 
                     !request.method().equals("PATCH"))) {
                    return false;
                }
                
                okhttp3.MediaType mediaType = request.body().contentType();
                if (mediaType != null && mediaType.subtype().contains("json")) {
                    okio.Buffer buffer = new okio.Buffer();
                    request.body().writeTo(buffer);
                    String requestBody = buffer.readUtf8();
                    
                    try {
                        BaseRequest baseRequest = gson.fromJson(requestBody, BaseRequest.class);
                        if (baseRequest != null && baseRequest.isRequiresAuthentication()) {
                            return true;
                        }
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
            return false;
        }

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            
            boolean requiresAuthBody = isAuthRequiredFromBody(originalRequest);
            
            Request.Builder requestBuilder = originalRequest.newBuilder();
            
            if (requiresAuthBody) {
                String token = getAuthToken();
                if (token != null && !token.isEmpty()) {
                    requestBuilder.addHeader("Authorization", token);
                }
            }
            
            Request newRequest = requestBuilder.build();
            return chain.proceed(newRequest);
        }
    }
    
    /**
     * Get the auth token from SharedPreferences
     * @return the token or null if not found
     */
    private static String getAuthToken() {
        Context context = ApplicationContextProvider.getContext();
        if (context == null) {
            return null;
        }
        
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }
}
