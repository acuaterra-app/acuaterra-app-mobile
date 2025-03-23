package com.example.monitoreoacua.service.response;
import com.example.monitoreoacua.business.models.Module;
import java.util.List;

public class ListModuleResponse extends ApiResponse<Module> {

    public List<Module> getAllModules() {
        return getData();
    }
}