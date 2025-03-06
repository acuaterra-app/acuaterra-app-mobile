package com.example.monitoreoacua.service.request;

/**
 * Base class for all request objects in the application.
 * Contains a flag to determine if the request requires authentication.
 */
public class BaseRequest {
    
    private boolean requiresAuthentication;

    public BaseRequest() {
        this.requiresAuthentication = false;
    }

    public boolean isRequiresAuthentication() {
        return requiresAuthentication;
    }

    public void setRequiresAuthentication(boolean requiresAuthentication) {
        this.requiresAuthentication = requiresAuthentication;
    }
}

