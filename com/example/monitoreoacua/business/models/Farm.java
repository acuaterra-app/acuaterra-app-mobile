package com.example.monitoreoacua.business.models;

import com.google.gson.annotations.SerializedName;

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

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
}

