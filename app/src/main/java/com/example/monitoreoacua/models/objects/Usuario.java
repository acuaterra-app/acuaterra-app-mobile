package com.example.monitoreoacua.models.objects;

public class Usuario {
    public String nombre;
    public String ficha;
    public String jornada;
    public String sede;
    public String email;
    public String password;

    public Usuario() {
    }

    public Usuario(String nombre, String ficha, String jornada, String sede, String email, String password) {
        this.nombre = nombre;
        this.ficha = ficha;
        this.jornada = jornada;
        this.sede = sede;
        this.email = email;
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFicha() {
        return ficha;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFicha(String ficha) {
        this.ficha = ficha;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

