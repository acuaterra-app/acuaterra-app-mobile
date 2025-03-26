package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.auth.AuthToken;
import com.example.monitoreoacua.business.models.auth.AuthUser;
import com.google.gson.annotations.SerializedName;

public class LoginResponse extends ApiResponse<LoginResponse.Data> {


    public AuthToken getToken() {
        Data data = getFirstDataItem();
        return data != null ? new AuthToken(data.getToken()) : null;
    }


    public AuthUser getUser() {
        Data data = getFirstDataItem();
        return data != null ? data.getUser() : null;
    }


    public static class Data {
        @SerializedName("token")
        private String token;
        @SerializedName("user")
        private AuthUser authUser;

        public String getToken() {
            return token;
        }

        public AuthUser getUser() {
            return authUser;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setUser(AuthUser authUser) {
            this.authUser = authUser;
        }
    }

}
