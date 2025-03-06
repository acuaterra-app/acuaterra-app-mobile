package com.example.monitoreoacua.service.request;

public class LogoutRequest extends BaseRequest {

    public LogoutRequest() {
        super();
        setRequiresAuthentication(true);
    }
}

