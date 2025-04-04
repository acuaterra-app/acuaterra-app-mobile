package com.example.monitoreoacua.business.models;

import java.util.List;

public class ModuleRequest {
    private String name;
    private String location;
    private String latitude;
    private String longitude;
    private String species_fish;
    private String fish_quantity;
    private String fish_age;
    private String dimensions;
    private int id_farm;
    private int created_by_user_id;
    private List<Integer> users;

    public ModuleRequest() {
        // Constructor vacío necesario para serialización
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSpecies_fish() {
        return species_fish;
    }

    public void setSpecies_fish(String species_fish) {
        this.species_fish = species_fish;
    }

    public String getFish_quantity() {
        return fish_quantity;
    }

    public void setFish_quantity(String fish_quantity) {
        this.fish_quantity = fish_quantity;
    }

    public String getFish_age() {
        return fish_age;
    }

    public void setFish_age(String fish_age) {
        this.fish_age = fish_age;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public int getId_farm() {
        return id_farm;
    }

    public void setId_farm(int id_farm) {
        this.id_farm = id_farm;
    }

    public int getCreated_by_user_id() {
        return created_by_user_id;
    }

    public void setCreated_by_user_id(int created_by_user_id) {
        this.created_by_user_id = created_by_user_id;
    }

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }
}

