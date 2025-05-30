package com.example.monitoreoacua.business.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

@Keep
public class Farm implements Parcelable, Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

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


    // Constructor vacío necesario para Parcelable y serialización
    @Keep
    public Farm() {
        // Constructor vacío requerido
    }

    @Keep
    public Farm(int id, String name, String address, String latitude, String longitude, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Keep
    protected Farm(Parcel in) {
        id = in.readInt();
        name = in.readString();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Keep
    public static final Parcelable.Creator<Farm> CREATOR = new Parcelable.Creator<Farm>() {
        @Override
        public Farm createFromParcel(Parcel in) {
            return new Farm(in);
        }

        @Override
        public Farm[] newArray(int size) {
            return new Farm[size];
        }
    };

    // Getters
    @Keep public int getId() { return id; }
    @Keep public String getName() { return name; }
    @Keep public String getAddress() { return address; }
    @Keep public String getLatitude() { return latitude; }
    @Keep public String getLongitude() { return longitude; }
    @Keep public String getCreatedAt() { return createdAt; }
    @Keep public String getUpdatedAt() { return updatedAt; }

    @Override
    @Keep
    public int describeContents() {
        return 0;
    }

    @Override
    @Keep
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    @Keep
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    @Keep
    public String toString() {
        return "Farm{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", latitude='" + latitude + '\'' +
            ", longitude='" + longitude + '\'' +
            ", createdAt='" + createdAt + '\'' +
            '}';
    }
}
