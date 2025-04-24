package com.example.monitoreoacua.business.models;


import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

/**
 * Model class representing a measurement from the aquaponic system.
 */
public class Measurement implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("id_sensor")
    private int id_sensor;

    @SerializedName("value")
    private double value;

    @SerializedName("date")
    private String date;

    @SerializedName("time")
    private String time;

    @SerializedName("sensor")
    private Sensor sensor;

    /**
     * Default constructor
     */
    public Measurement() {
    }

    /**
     * Constructor without sensor object
     */
    public Measurement(int id, int id_sensor, double value, String date, String time) {
        this.id = id;
        this.id_sensor = id_sensor;
        this.value = value;
        this.date = date;
        this.time = time;
    }

    /**
     * Full constructor
     *
     * @param id        The measurement ID
     * @param id_sensor The sensor ID
     * @param value     The measured value
     * @param date      The date when measurement was taken
     * @param time      The time when measurement was taken
     * @param sensor    The sensor that took the measurement
     */
    public Measurement(int id, int id_sensor, double value, String date, String time, Sensor sensor) {
        this.id = id;
        this.id_sensor = id_sensor;
        this.value = value;
        this.date = date;
        this.time = time;
        this.sensor = sensor;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_sensor() {
        return id_sensor;
    }

    public void setId_sensor(int id_sensor) {
        this.id_sensor = id_sensor;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "id=" + id +
                ", id_sensor=" + id_sensor +
                ", value=" + value +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", sensor=" + sensor +
                '}';
    }
}