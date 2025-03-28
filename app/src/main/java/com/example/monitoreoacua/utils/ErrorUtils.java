package com.example.monitoreoacua.utils;

import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.response.ApiError;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static ApiError parseError(Response<?> response) {
        Converter<ResponseBody, ApiError> converter =
                ApiClient.getClient().responseBodyConverter(ApiError.class, new Annotation[0]);
        ApiError apiError = new ApiError();
        if (response.errorBody() != null) {
            try {
                apiError = converter.convert(response.errorBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return apiError;
    }
}