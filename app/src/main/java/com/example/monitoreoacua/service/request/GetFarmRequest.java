package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.response.FarmResponse;
import com.example.monitoreoacua.service.response.ListFarmResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetFarmRequest extends BaseRequest {

    private static final String TAG = "GetFarmRequest";

    public GetFarmRequest() {
        super();
        setRequiresAuthentication(true);
    }

    public void getFarmById(OnApiRequestCallback<Farm, Throwable> callback, int id){
        ApiFarmsService apiFarmsService = ApiClient.getClient().create(ApiFarmsService.class);

        apiFarmsService.getFarmById(getAuthToken(),id).enqueue(new Callback<FarmResponse>() {
            @Override
            public void onResponse(@NonNull Call<FarmResponse> call, @NonNull Response<FarmResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "Error fetching farm details: " + response);
                    return;
                }
                Farm farm = response.body().getFarm();
                if (farm == null) {
                    Log.e(TAG, "Farm data is null in the response");
                    return;
                }
                callback.onSuccess(farm);
            }

            @Override
            public void onFailure(@NonNull Call<FarmResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error when fetching notifications: " + t.getMessage(), t);
                callback.onFail(t);
            }
        });
    }
}
