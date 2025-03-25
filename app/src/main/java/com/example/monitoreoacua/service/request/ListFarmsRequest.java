package com.example.monitoreoacua.service.request;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiFarmsService;
import com.example.monitoreoacua.service.response.ListFarmResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFarmsRequest extends BaseRequest {

    private static final String TAG = "ListFarmRequest";
    public ListFarmsRequest() {
        super();
        setRequiresAuthentication(true);
    }

    public void fetchFarms(OnApiRequestCallback<List<Farm>, Throwable> callback){
        ApiFarmsService apiFarmsService = ApiClient.getClient().create(ApiFarmsService.class);

        apiFarmsService.getFarms(getAuthToken()).enqueue(new Callback<ListFarmResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListFarmResponse> call, @NonNull Response<ListFarmResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        ListFarmResponse listFarmResponse = response.body();
                        List<Farm> farms = listFarmResponse.getAllFarms();

                       callback.onSuccess(farms);
                    } else {
                        Log.d(TAG, "Error with response: " + response);
                        callback.onFail(new Throwable("Error with response"));
                    }
                }

            @Override
            public void onFailure(@NonNull Call<ListFarmResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Error with response: " + t);
                callback.onFail(t);
            }
        });
    }
}