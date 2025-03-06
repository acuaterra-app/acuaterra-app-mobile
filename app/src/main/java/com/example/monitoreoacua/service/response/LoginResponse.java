package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.auth.AuthToken;
import com.example.monitoreoacua.business.models.User;

public class LoginResponse extends ApiResponse<LoginResponse.Data> {


    public AuthToken getToken() {
        Data data = getFirstDataItem();
        return data != null ? new AuthToken(data.getToken()) : null;
    }


    public User getUser() {
        Data data = getFirstDataItem();
        return data != null ? data.getUser() : null;
    }


    public static class Data {
        private String token;
        private User user;

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }

}
