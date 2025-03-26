package com.example.monitoreoacua.business.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Farm implements Parcelable {
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

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;
    
    @SerializedName("users")
    private List<User> users;

    // Constructor
    public Farm(int id, String name, String address, String latitude, String longitude, 
                String createdAt, String updatedAt, List<User> users) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.users = users != null ? users : new ArrayList<>();
    }

    // Basic constructor with required fields
    public Farm(int id, String name, String address, String latitude, String longitude) {
        this(id, name, address, latitude, longitude, null, null, null);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public List<User> getUsers() { return users; }

    // Parcelable implementation
    protected Farm(Parcel in) {
        id = in.readInt();
        name = in.readString();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        users = new ArrayList<>();
        in.readTypedList(users, User.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeTypedList(users);
    }

    public static final Creator<Farm> CREATOR = new Creator<Farm>() {
        @Override
        public Farm createFromParcel(Parcel in) {
            return new Farm(in);
        }

        @Override
        public Farm[] newArray(int size) {
            return new Farm[size];
        }
    };
}
