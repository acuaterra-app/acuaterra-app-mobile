package com.example.monitoreoacua.business.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a user role in the system.
 */
public class Role implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Role(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Role> CREATOR = new Creator<Role>() {
        @Override
        public Role createFromParcel(Parcel in) {
            return new Role(in);
        }

        @Override
        public Role[] newArray(int size) {
            return new Role[size];
        }
    };

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}

