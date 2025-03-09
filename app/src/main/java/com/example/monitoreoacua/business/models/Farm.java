package com.example.monitoreoacua.business.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Farm {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("users")
    private List<User> users;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public List<User> getUsers() { return users; }
}
