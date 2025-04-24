package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiUserService;
import com.example.monitoreoacua.service.response.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignMonitorsRequest extends BaseRequest {
    private static final String TAG = "AssignMonitorsRequest";

    public AssignMonitorsRequest() {
        super();
        setRequiresAuthentication(true);
    }

    public void assignMonitors(int moduleId, List<Integer> monitorIds, OnApiRequestCallback<ApiResponse, Throwable> callback) {
        ApiUserService apiUserService = ApiClient.getClient().create(ApiUserService.class);

        // Crear el cuerpo de la solicitud
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "assign");
        requestBody.put("monitorIds", monitorIds);

        apiUserService.assignMonitors(getAuthToken(), moduleId, requestBody).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    Log.d(TAG, "Error with response: " + response);
                    callback.onFail(new Throwable("Error with response"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Error with response: " + t);
                callback.onFail(t);
            }
        });
    }
}