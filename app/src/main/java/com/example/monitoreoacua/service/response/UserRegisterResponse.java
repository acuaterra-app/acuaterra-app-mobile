package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserRegisterResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<User> data;

    @SerializedName("errors")
    private List<ApiError.ErrorDetail> errors;

    @SerializedName("meta")
    private ApiResponse.Meta meta;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getData() {
        return data.get(0);
    }

    public void setData(User data) {
        this.data.add(data);
    }

    public List<ApiError.ErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ApiError.ErrorDetail> errors) {
        this.errors = errors;
    }

    public ApiResponse.Meta getMeta() {
        return meta;
    }

    public void setMeta(ApiResponse.Meta meta) {
        this.meta = meta;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}