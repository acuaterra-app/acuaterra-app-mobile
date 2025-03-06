package com.example.monitoreoacua.business.models;

import com.google.gson.annotations.SerializedName;

public class User {
    private int id;
    @SerializedName("name")
    private String name;
    private String email;
    private String rol;
    private String dni;
    @SerializedName("id_rol")
    private int idRol;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }

    public String getDni() {
        return dni;
    }

    public int getIdRol() {
        return idRol;
    }
}

