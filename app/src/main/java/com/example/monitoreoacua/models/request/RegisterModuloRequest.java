package com.example.monitoreoacua.models.request;

public class RegisterModuloRequest {
    private String nombre;
    private String ubicacion;
    private String especie_pescados;
    private int cantidad_pescados;
    private int edad_pescados;
    private String dimensiones;
    private int id_persona;

    // Constructor
    public RegisterModuloRequest(String nombre, String ubicacion, String especie_pescados,
                                 int cantidad_pescados, int edad_pescados, String dimensiones,
                                 int id_persona) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.especie_pescados = especie_pescados;
        this.cantidad_pescados = cantidad_pescados;
        this.edad_pescados = edad_pescados;
        this.dimensiones = dimensiones;
        this.id_persona = id_persona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEspecie_pescados() {
        return especie_pescados;
    }

    public void setEspecie_pescados(String especie_pescados) {
        this.especie_pescados = especie_pescados;
    }

    public int getCantidad_pescados() {
        return cantidad_pescados;
    }

    public void setCantidad_pescados(int cantidad_pescados) {
        this.cantidad_pescados = cantidad_pescados;
    }

    public int getEdad_pescados() {
        return edad_pescados;
    }

    public void setEdad_pescados(int edad_pescados) {
        this.edad_pescados = edad_pescados;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public int getId_persona() {
        return id_persona;
    }

    public void setId_persona(int id_persona) {
        this.id_persona = id_persona;
    }
}
