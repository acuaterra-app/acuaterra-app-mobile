package com.example.monitoreoacua.models.request;

public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private String n_documento_identidad;
    private String sede;
    private int rol = 3; // Rol por defecto
    private String n_ficha;
    private String jornada;
    private String nombre_del_programa;

    public RegisterRequest(String nombre, String email, String password, String n_documento_identidad, String sede, int rol, String n_ficha, String jornada, String nombre_del_programa) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.n_documento_identidad = n_documento_identidad;
        this.sede = sede;
        this.rol = rol;
        this.n_ficha = n_ficha;
        this.jornada = jornada;
        this.nombre_del_programa = nombre_del_programa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getN_documento_identidad() {
        return n_documento_identidad;
    }

    public void setN_documento_identidad(String n_documento_identidad) {
        this.n_documento_identidad = n_documento_identidad;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public String getN_ficha() {
        return n_ficha;
    }

    public void setN_ficha(String n_ficha) {
        this.n_ficha = n_ficha;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }

    public String getNombre_del_programa() {
        return nombre_del_programa;
    }

    public void setNombre_del_programa(String nombre_del_programa) {
        this.nombre_del_programa = nombre_del_programa;
    }

}
