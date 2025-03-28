package com.example.monitoreoacua.service.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest extends BaseRequest {

    private String email;
    private String password;
    @SerializedName("device_id")
    private String device_id;

    public LoginRequest(String email, String password, String deviceId) {
        super(); // Call the BaseRequest constructor
        this.email = email;
        this.password = password;
        this.device_id = deviceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

}
