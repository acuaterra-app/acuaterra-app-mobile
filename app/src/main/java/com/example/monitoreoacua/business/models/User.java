package com.example.monitoreoacua.business.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a user in the system.
 */
public class User implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("dni")
    private String dni;

    @SerializedName("id_rol")
    private int idRol;

    @SerializedName("rol")
    private Role role;

    public User(int id, String name, String email, String dni, int idRol, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dni = dni;
        this.idRol = idRol;
        this.role = role;
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        email = in.readString();
        dni = in.readString();
        idRol = in.readInt();
        role = in.readParcelable(Role.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDni() {
        return dni;
    }

    public int getIdRol() {
        return idRol;
    }

    public Role getRole() {
        return role;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(dni);
        dest.writeInt(idRol);
        dest.writeParcelable(role, flags);
    }
}
