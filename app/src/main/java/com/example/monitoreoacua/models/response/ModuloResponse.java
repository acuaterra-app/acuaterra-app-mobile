package com.example.monitoreoacua.models.response;

import com.example.monitoreoacua.models.objects.Modulo;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ModuloResponse {
    private String message;

    @SerializedName("data")
    private List<Modulo> modules; // Cambiado a lista simple

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getters y Setters actualizados
    public List<Modulo> getModules() {
        return modules;
    }

    public void setModules(List<Modulo> modules) {
        this.modules = modules;
    }
}