package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.ApiUserService;
import com.example.monitoreoacua.service.response.ApiError;
import com.example.monitoreoacua.service.response.RegisterModuleResponse;
import com.example.monitoreoacua.service.response.UserRegisterResponse;
import com.example.monitoreoacua.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.monitoreoacua.business.models.Module;

public class RegisterModuleRequest extends BaseRequest {

    private static final String TAG = "RegisterModuleRequest";

    public RegisterModuleRequest(){
        super();
        setRequiresAuthentication(true);
    }

    public void registerModuleRequest(Module module, OnApiRequestCallback<RegisterModuleResponse, Throwable> callback) {
        ApiModulesService apiModulesService = ApiClient.getClient().create(ApiModulesService.class);

        apiModulesService.createModule(getAuthToken(), module).enqueue(new Callback<RegisterModuleResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterModuleResponse> call,@NonNull Response<RegisterModuleResponse> response) {
                if (response.isSuccessful()) {
                    RegisterModuleResponse registerModuleResponse = response.body();
                    callback.onSuccess(registerModuleResponse);
                } else {
                    ApiError apiError = ErrorUtils.parseError(response);
                    Log.d(TAG, "Error message: " + apiError.getMessage());
                    for (ApiError.ErrorDetail errorDetail : apiError.getErrors()) {
                        Log.d(TAG, "Error: " + errorDetail.getMsg());
                    }
                    Log.d(TAG, "Error with response: " + response.message());
                    callback.onFail(new Throwable("Error en el registro"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterModuleResponse> call,@NonNull Throwable t) {
                Log.d(TAG, "Error with response: " + t);
                callback.onFail(t);
            }
        });
    }
}

