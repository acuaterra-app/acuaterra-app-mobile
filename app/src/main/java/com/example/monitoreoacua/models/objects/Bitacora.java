package com.example.monitoreoacua.models.objects;

import com.google.gson.annotations.SerializedName;

public class Bitacora {
    @SerializedName("id_bitacora")
    private int idBitacora;

    @SerializedName("id_modulo")
    private int idModulo;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("descripcion")
    private String descripcion;

    // Constructor vacío necesario para Retrofit
    public Bitacora() {}

    // Constructor con parámetros
    public Bitacora(int idBitacora, int idModulo, String fecha, String descripcion) {
        this.idBitacora = idBitacora;
        this.idModulo = idModulo;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdBitacora() { return idBitacora; }
    public void setIdBitacora(int idBitacora) { this.idBitacora = idBitacora; }

    public int getIdModulo() { return idModulo; }
    public void setIdModulo(int idModulo) { this.idModulo = idModulo; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return "Bitacora{" +
                "idBitacora=" + idBitacora +
                ", idModulo=" + idModulo +
                ", fecha='" + fecha + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
