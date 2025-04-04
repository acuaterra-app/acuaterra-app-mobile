package com.example.monitoreoacua.service.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiUserService;
import com.example.monitoreoacua.service.response.ApiError;
import com.example.monitoreoacua.service.response.UserRegisterResponse;
import com.example.monitoreoacua.service.response.UserUpdateResponse;
import com.example.monitoreoacua.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterUserRequest extends BaseRequest {
    private static final String TAG = "RegisterUserRequest";

    public RegisterUserRequest() {
        super();
        setRequiresAuthentication(true);
    }

    public void registerUser(UserRequest user, OnApiRequestCallback<UserRegisterResponse, Throwable> callback) {
        ApiUserService apiUserService = ApiClient.getClient().create(ApiUserService.class);

        apiUserService.register(getAuthToken(), user).enqueue(new Callback<UserRegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserRegisterResponse> call, @NonNull Response<UserRegisterResponse> response) {
                if (response.isSuccessful()) {
                    UserRegisterResponse registerUserResponse = response.body();
                    callback.onSuccess(registerUserResponse);
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
            public void onFailure(@NonNull Call<UserRegisterResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Error with response: " + t);
                callback.onFail(t);
            }
        });
    }

    public void updateUser(int userId, UserRequest user, OnApiRequestCallback<UserUpdateResponse, Throwable> callback) {
        ApiUserService apiUserService = ApiClient.getClient().create(ApiUserService.class);

        apiUserService.updateUser(getAuthToken(), userId, user).enqueue(new Callback<UserUpdateResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserUpdateResponse> call, @NonNull Response<UserUpdateResponse> response) {
                if (response.isSuccessful()) {
                    UserUpdateResponse updateUserResponse = response.body();
                    callback.onSuccess(updateUserResponse);
                } else {
                    ApiError apiError = ErrorUtils.parseError(response);
                    Log.d(TAG, "Error message: " + apiError.getMessage());
                    for (ApiError.ErrorDetail errorDetail : apiError.getErrors()) {
                        Log.d(TAG, "Error: " + errorDetail.getMsg());
                    }
                    Log.d(TAG, "Error with response: " + response.message());
                    callback.onFail(new Throwable("Error en la actualización"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserUpdateResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Error with response: " + t);
                callback.onFail(t);
            }
        });
    }

    public void deleteUser(int userId, OnApiRequestCallback<Void, Throwable> callback) {
        ApiUserService apiUserService = ApiClient.getClient().create(ApiUserService.class);

        apiUserService.deleteUser(getAuthToken(), userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    ApiError apiError = ErrorUtils.parseError(response);
                    Log.d(TAG, "Error message: " + apiError.getMessage());
                    for (ApiError.ErrorDetail errorDetail : apiError.getErrors()) {
                        Log.d(TAG, "Error: " + errorDetail.getMsg());
                    }
                    Log.d(TAG, "Error with response: " + response.message());
                    callback.onFail(new Throwable("Error al eliminar"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d(TAG, "Error with response: " + t);
                callback.onFail(t);
            }
        });
    }
}