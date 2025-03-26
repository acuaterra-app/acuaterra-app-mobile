package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Module;

public class GetModuleResponse extends ApiResponse<Module> {

    public Module getModule() {
        return getFirstDataItem();
    }
}

