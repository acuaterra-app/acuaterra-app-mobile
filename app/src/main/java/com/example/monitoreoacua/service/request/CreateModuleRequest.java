package com.example.monitoreoacua.service.request;

import java.util.List;

public class CreateModuleRequest extends BaseRequest{


    private String name;
    private String location;
    private String latitude;
    private String longitude;
    private String speciesFish;
    private String fishQuantity;
    private String fishAge;
    private String dimensions;
    private int idFarm;
    private int createdByUserId;
    private List<Integer> users;

    public CreateModuleRequest(String name, String location, String latitude, String longitude,
                               String speciesFish, String fishQuantity, String fishAge,
                               String dimensions, int idFarm, int createdByUserId, List<Integer> users) {
        super();
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speciesFish = speciesFish;
        this.fishQuantity = fishQuantity;
        this.fishAge = fishAge;
        this.dimensions = dimensions;
        this.idFarm = idFarm;
        this.createdByUserId = createdByUserId;
        this.users = users;
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

    public String getSpeciesFish() {
        return speciesFish;
    }

    public void setSpeciesFish(String speciesFish) {
        this.speciesFish = speciesFish;
    }

    public String getFishQuantity() {
        return fishQuantity;
    }

    public void setFishQuantity(String fishQuantity) {
        this.fishQuantity = fishQuantity;
    }

    public String getFishAge() {
        return fishAge;
    }

    public void setFishAge(String fishAge) {
        this.fishAge = fishAge;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public int getIdFarm() {
        return idFarm;
    }

    public void setIdFarm(int idFarm) {
        this.idFarm = idFarm;
    }

    public int getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }

}
