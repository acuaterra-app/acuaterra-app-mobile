package com.example.monitoreoacua.service.request;

import com.example.monitoreoacua.business.models.ModuleRequest;

public class CreateModuleRequest extends BaseRequest {
    private ModuleRequest module;
    
    public CreateModuleRequest() {
        // Constructor vacío necesario para serialización
    }
    
    public CreateModuleRequest(ModuleRequest module) {
        this.module = module;
    }
    
    public ModuleRequest getModule() {
        return module;
    }
    
    public void setModule(ModuleRequest module) {
        this.module = module;
    }
}

