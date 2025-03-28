package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiUserService;
import com.example.monitoreoacua.service.response.ApiResponse;
import com.example.monitoreoacua.service.response.ListUserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListUsersRequest extends BaseRequest{
    private static final String TAG = "ListUsersRequest";

    public ListUsersRequest() {
        super();
        setRequiresAuthentication(true);
    }

    public void fetchUsers(OnApiRequestCallback<List<User>, Throwable> callback) {
        ApiUserService apiUserService = ApiClient.getClient().create(ApiUserService.class);

        apiUserService.getUsers(getAuthToken()).enqueue(new Callback<ListUserResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListUserResponse> call, @NonNull Response<ListUserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ListUserResponse listUserResponse = response.body();
                    List<User> users = listUserResponse.getData();
                    callback.onSuccess(users);
                } else {
                    Log.d(TAG, "Error with response: " + response);
                    callback.onFail(new Throwable("Error with response"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListUserResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Error with response: " + t);
                callback.onFail(t);
            }
        });
    }
}
