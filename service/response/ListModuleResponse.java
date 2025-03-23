package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Module;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListModuleResponse extends ApiResponse {
    @SerializedName("data")
    private List<Module> modules;

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }
}

