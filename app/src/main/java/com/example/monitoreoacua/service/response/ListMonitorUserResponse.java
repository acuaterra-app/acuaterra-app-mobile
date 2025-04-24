package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.User;

import java.util.List;

public class ListMonitorUserResponse {
    private String message;
    private List<List<UserMonitorResponse>> data; // Ajustado para reflejar un arreglo de arreglos
    private List<String> errors;
    private Object meta;

    public String getMessage() {
        return message;
    }

    public List<List<UserMonitorResponse>> getData() {
        return data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Object getMeta() {
        return meta;
    }
}
