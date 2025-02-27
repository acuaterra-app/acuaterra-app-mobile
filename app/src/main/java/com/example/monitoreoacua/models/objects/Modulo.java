package com.example.monitoreoacua.models.objects;
import com.google.gson.annotations.SerializedName;


public class Modulo {

    @SerializedName("id_modulo")
    private int idModulo;
    private String nombre;
    private String ubicacion;

    @SerializedName("especie_pescados")
    private String especiePescados;

    @SerializedName("cantidad_pescados")
    private String cantidadPescados;

    @SerializedName("edad_pescados")
    private String edadPescados;
    private String dimensiones;

    @SerializedName("id_persona_modulo")
    private int idPersonaModulo;

    @SerializedName("nombre_persona_modulo")
    private String nombrePersonaModulo;

    @SerializedName("id_persona_asignada")
    private int idPersonaAsignada;

    @SerializedName("nombre_persona_asignada")
    private String nombrePersonaAsignada;

    // Constructor en el orden de la API
    public Modulo(int idModulo, String nombre, String ubicacion, String especiePescados,
                  String cantidadPescados, String edadPescados, String dimensiones,
                  int idPersonaModulo, String nombrePersonaModulo,
                  int idPersonaAsignada, String nombrePersonaAsignada) {
        this.idModulo = idModulo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.especiePescados = especiePescados;
        this.cantidadPescados = cantidadPescados;
        this.edadPescados = edadPescados;
        this.dimensiones = dimensiones;
        this.idPersonaModulo = idPersonaModulo;
        this.nombrePersonaModulo = nombrePersonaModulo;
        this.idPersonaAsignada = idPersonaAsignada;
        this.nombrePersonaAsignada = nombrePersonaAsignada;
    }

    /*
    public Modulo(String nombrePersonaAsignada, Integer idPersonaAsignada, String nombrePersonaModulo, int idPersonaModulo, String dimensiones, String edadPescados, String cantidadPescados, String especiePescados, String ubicacion, String nombre, int idModulo) {
        this.nombrePersonaAsignada = nombrePersonaAsignada;
        this.idPersonaAsignada = idPersonaAsignada;
        this.nombrePersonaModulo = nombrePersonaModulo;
        this.idPersonaModulo = idPersonaModulo;
        this.dimensiones = dimensiones;
        this.edadPescados = edadPescados;
        this.cantidadPescados = cantidadPescados;
        this.especiePescados = especiePescados;
        this.ubicacion = ubicacion;
        this.nombre = nombre;
        this.idModulo = idModulo;
    }
     */

    public String getNombrePersonaAsignada() {
        return nombrePersonaAsignada;
    }

    public void setNombrePersonaAsignada(String nombrePersonaAsignada) {
        this.nombrePersonaAsignada = nombrePersonaAsignada;
    }

    public Integer getIdPersonaAsignada() {
        return idPersonaAsignada;
    }

    public void setIdPersonaAsignada(Integer idPersonaAsignada) {
        this.idPersonaAsignada = idPersonaAsignada;
    }

    public String getNombrePersonaModulo() {
        return nombrePersonaModulo;
    }

    public void setNombrePersonaModulo(String nombrePersonaModulo) {
        this.nombrePersonaModulo = nombrePersonaModulo;
    }

    public int getIdPersonaModulo() {
        return idPersonaModulo;
    }

    public void setIdPersonaModulo(int idPersonaModulo) {
        this.idPersonaModulo = idPersonaModulo;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public String getEdadPescados() {
        return edadPescados;
    }

    public void setEdadPescados(String edadPescados) {
        this.edadPescados = edadPescados;
    }

    public String getCantidadPescados() {
        return cantidadPescados;
    }

    public void setCantidadPescados(String cantidadPescados) {
        this.cantidadPescados = cantidadPescados;
    }

    public String getEspeciePescados() {
        return especiePescados;
    }

    public void setEspeciePescados(String especiePescados) {
        this.especiePescados = especiePescados;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(int idModulo) {
        this.idModulo = idModulo;
    }
}
