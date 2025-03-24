package com.example.monitoreoacua.business.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

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

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("deletedAt")
    private String deletedAt;

    @SerializedName("creator")
    private User creator;

    @SerializedName("farm")
    private Farm farm;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getSpeciesFish() { return speciesFish; }
    public String getFishQuantity() { return fishQuantity; }
    public String getFishAge() { return fishAge; }
    public String getDimensions() { return dimensions; }
    public int getIdFarm() { return idFarm; }
    public int getCreatedByUserId() { return createdByUserId; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getDeletedAt() { return deletedAt; }
    public User getCreator() { return creator; }
    public Farm getFarm() { return farm; }



}
