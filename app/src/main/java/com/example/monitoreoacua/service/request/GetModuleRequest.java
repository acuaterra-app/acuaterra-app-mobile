package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.response.GetModuleResponse;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetModuleRequest extends BaseRequest {
    private static final String TAG = "GetModuleRequestTag";

    public void getModuleById(OnApiRequestCallback<Module, Throwable> callback, int id) {
        Log.d(TAG, "getModuleById: " + id);
        Log.d("My_Tag", "GetModuleRequest - Making API call with module ID: " + id);
        ApiModulesService apiModulesService = ApiClient.getClient().create(ApiModulesService.class);

        // Ensure we're passing the module ID correctly to the API
        String authToken = getAuthToken();
        Log.d("My_Tag", "API call parameters - token: " + (authToken != null ? "valid token" : "null") + ", id: " + id);
        apiModulesService.getModuleById(authToken, id).enqueue(new Callback<GetModuleResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetModuleResponse> call, @NonNull Response<GetModuleResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    String errorMessage = "Error fetching module details: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onFail(new Exception(errorMessage));
                    return;
                }
                
                Module module = response.body().getModule();
                if (module == null) {
                    String errorMessage = "Module data is null in the response";
                    Log.e(TAG, errorMessage);
                    callback.onFail(new Exception(errorMessage));
                    return;
                }
                
                callback.onSuccess(module);
            }

            @Override
            public void onFailure(@NonNull Call<GetModuleResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error when fetching module: " + t.getMessage(), t);
                callback.onFail(t);
            }
        });
    }
}

