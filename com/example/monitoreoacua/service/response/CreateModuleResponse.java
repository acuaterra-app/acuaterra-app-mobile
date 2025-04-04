package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Module;

public class CreateModuleResponse extends ApiResponse {
    private Module data;
    
    public CreateModuleResponse() {
        // Constructor vacío necesario para serialización
    }
    
    public Module getData() {
        return data;
    }
    
    public void setData(Module data) {
        this.data = data;
    }
}

