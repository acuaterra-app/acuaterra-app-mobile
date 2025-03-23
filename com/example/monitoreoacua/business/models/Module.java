package com.example.monitoreoacua.business.models;

import com.google.gson.annotations.SerializedName;

public class Module {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("location")
    private String location;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("species_fish")
    private String speciesFish;

    @SerializedName("fish_quantity")
    private String fishQuantity;

    @SerializedName("fish_age")
    private String fishAge;

    @SerializedName("dimensions")
    private String dimensions;

    @SerializedName("id_farm")
    private int idFarm;

    @SerializedName("created_by_user_id")
    private int createdByUserId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("deleted_at")
    private String deletedAt;

    @SerializedName("creator")
    private Creator creator;

    @SerializedName("farm")
    private Farm farm;

    // Getters
    public int getId() { 
        return id; 
    }

    public String getName() { 
        return name; 
    }

    public String getLocation() { 
        return location; 
    }

    public String getLatitude() { 
        return latitude; 
    }

    public String getLongitude() { 
        return longitude; 
    }

    public String getSpeciesFish() { 
        return speciesFish; 
    }

    public String getFishQuantity() { 
        return fishQuantity; 
    }

    public String getFishAge() { 
        return fishAge; 
    }

    public String getDimensions() { 
        return dimensions; 
    }

    public int getIdFarm() { 
        return idFarm; 
    }

    public int getCreatedByUserId() { 
        return createdByUserId; 
    }

    public String getCreatedAt() { 
        return createdAt; 
    }

    public String getUpdatedAt() { 
        return updatedAt; 
    }

    public String getDeletedAt() { 
        return deletedAt; 
    }

    public Creator getCreator() { 
        return creator; 
    }

    public Farm getFarm() { 
        return farm; 
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setSpeciesFish(String speciesFish) {
        this.speciesFish = speciesFish;
    }

    public void setFishQuantity(String fishQuantity) {
        this.fishQuantity = fishQuantity;
    }

    public void setFishAge(String fishAge) {
        this.fishAge = fishAge;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public void setIdFarm(int idFarm) {
        this.idFarm = idFarm;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }
}
