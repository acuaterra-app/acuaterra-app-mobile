package com.example.monitoreoacua.business.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.monitoreoacua.service.request.BaseRequest;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Module extends BaseRequest implements Serializable, Parcelable {
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



    @SerializedName("users")
    private List<User> users;

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

    @SerializedName("user_ids")
    private List<Integer> userIds;

    @SerializedName("is_active")
    private boolean isActive = false;

    public Module() {
        // Constructor vacío
    }

    // Constructor con campos del request API
    public Module(String name, String location, String latitude, String longitude,
                  String speciesFish, String fishQuantity, String fishAge,
                  String dimensions, int idFarm, List<Integer> users) {
        this(name, location, latitude, longitude, speciesFish, fishQuantity, 
             fishAge, dimensions, idFarm, users, false);
    }

    public Module(String name, String location, String latitude, String longitude,
                  String speciesFish, String fishQuantity, String fishAge,
                  String dimensions, int idFarm, List<Integer> users, boolean isActive) {
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speciesFish = speciesFish;
        this.fishQuantity = fishQuantity;
        this.fishAge = fishAge;
        this.dimensions = dimensions;
        this.idFarm = idFarm;
        this.userIds = users;
        this.isActive = isActive;
    }

    // Constructor Parcelable
    protected Module(Parcel in) {
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
        createdAt = in.readString();
        updatedAt = in.readString();
        deletedAt = in.readString();
        isActive = in.readInt() == 1;
        // Nota: los objetos complejos como creator, farm, sensors, users pueden requerir Parcelable adicional o manejo especial si necesitas pasarlos también.
    }

    public static final Creator<Module> CREATOR = new Creator<Module>() {
        @Override
        public Module createFromParcel(Parcel in) {
            return new Module(in);
        }

        @Override
        public Module[] newArray(int size) {
            return new Module[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(location);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(speciesFish);
        dest.writeString(fishQuantity);
        dest.writeString(fishAge);
        dest.writeString(dimensions);
        dest.writeInt(idFarm);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(deletedAt);
        dest.writeInt(isActive ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters y Setters
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
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
    public List<Integer> getUserIds() { return userIds; }
    public void setUserIds(List<Integer> userIds) { this.userIds = userIds; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getDeletedAt() { return deletedAt; }
    public User getCreator() { return creator; }
    public Farm getFarm() { return farm; }
    public List<Sensor> getSensors() { return sensors; }
    public void setSensors(List<Sensor> sensors) { this.sensors = sensors; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }


}
