package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Module;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ListModuleResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Module> data;

    @SerializedName("errors")
    private List<String> errors;

    @SerializedName("meta")
    private MetaData meta;

    public String getMessage() { return message; }
    public List<Module> getData() { return data; }
    public List<String> getErrors() { return errors; }
    public MetaData getMeta() { return meta; }

    public List<Module> getAllModules() {
        return data;
    }

    public Module getFirstModule() {
        return data != null && !data.isEmpty() ? data.get(0) : null;
    }
}
