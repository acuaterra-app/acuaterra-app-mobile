package com.example.monitoreoacua.business.models;


import com.example.monitoreoacua.service.request.BaseRequest;
import android.os.Parcelable;
import com.example.monitoreoacua.business.models.auth.AuthUser;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Module extends BaseRequest implements Serializable {
    private static final long serialVersionUID = 1L;
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


    // Agregar lista de IDs de usuarios
    private List<Integer> users;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("deletedAt")
    private String deletedAt;

    @SerializedName("creator")
    private User creator;

    @SerializedName("farm")
    private Farm farm;

    @SerializedName("sensors")
    private List<Sensor> sensors;

    public Module() {
        // Constructor vacío
    }

    // Constructor con campos del request API
    public Module(String name, String location, String latitude, String longitude,
                  String speciesFish, String fishQuantity, String fishAge,
                  String dimensions, int idFarm, List<Integer> users) {
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speciesFish = speciesFish;
        this.fishQuantity = fishQuantity;
        this.fishAge = fishAge;
        this.dimensions = dimensions;
        this.idFarm = idFarm;
        this.users = users;
    }

    @Override
    public String toString() {
        return "Module{" +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", speciesFish='" + speciesFish + '\'' +
                ", fishQuantity='" + fishQuantity + '\'' +
                ", fishAge='" + fishAge + '\'' +
                ", dimensions='" + dimensions + '\'' +
                ", users=" + users +
                ", idFarm=" + idFarm +
                '}';
    }

    // Getter y Setter para users
    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getSpeciesFish() { return speciesFish; }
    public String getFishQuantity() { return fishQuantity; }
    public String getFishAge() { return fishAge; }
    public String getDimensions() { return dimensions; }
    public int getIdFarm() { return idFarm; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getDeletedAt() { return deletedAt; }
    public User getCreator() { return creator; }
    public Farm getFarm() { return farm; }
    public List<Sensor> getSensors() { return sensors; }
    public void setSensors(List<Sensor> sensors) { this.sensors = sensors; }

}


    public static final Parcelable.Creator<Module> CREATOR = new Parcelable.Creator<Module>() {
        @Override
        public Module createFromParcel(android.os.Parcel in) {
            return new Module(in);
        }

        @Override
        public Module[] newArray(int size) {
            return new Module[size];
        }
    };

    protected Module(android.os.Parcel in) {
        id = in.readInt();
        name = in.readString();
        location = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        speciesFish = in.readString();
        fishQuantity = in.readString();
        fishAge = in.readString();
        dimensions = in.readString();
        idFarm = in.readInt();
        createdByUserId = in.readInt();
        createdAt = in.readString();
        updatedAt = in.readString();
        deletedAt = in.readString();
    }
}

