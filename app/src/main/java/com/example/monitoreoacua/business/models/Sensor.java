package com.example.monitoreoacua.business.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sensor implements Serializable {
    private int id;
    private String name;
    private List<Threshold> thresholds;

    public Sensor() {
        this.thresholds = new ArrayList<>();
    }

    public Sensor(int id, String name) {
        this.id = id;
        this.name = name;
        this.thresholds = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Threshold> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<Threshold> thresholds) {
        this.thresholds = thresholds;
    }

    public static class Threshold implements Serializable {
        private int id;
        private int id_sensor;
        private String value;
        private String type;
        private Date createdAt;
        private Date updatedAt;
        private Date deletedAt;

        public Threshold() {
        }

        public Threshold(int id, int id_sensor, String value, String type, Date createdAt, Date updatedAt, Date deletedAt) {
            this.id = id;
            this.id_sensor = id_sensor;
            this.value = value;
            this.type = type;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.deletedAt = deletedAt;
        }

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

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Date getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(Date deletedAt) {
            this.deletedAt = deletedAt;
        }
    }
}

