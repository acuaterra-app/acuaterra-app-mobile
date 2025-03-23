package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Module;

import java.util.List;

public class ListModuleResponse extends ApiResponse<Module> {

    public Module getFirstModule() {
        return getFirstDataItem();
    }

    public List<Module> getAllModules() {
        return getData();
    }
}