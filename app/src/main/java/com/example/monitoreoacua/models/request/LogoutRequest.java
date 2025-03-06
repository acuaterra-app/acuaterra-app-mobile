package com.example.monitoreoacua.models.request;

import androidx.annotation.NonNull;

public class LogoutRequest extends BaseRequest {

    public LogoutRequest() {
        super();
        setRequiresAuthentication(true);
    }
}

