package com.example.monitoreoacua.business.models.auth;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.monitoreoacua.business.models.Role;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a user in the system.
 */
public class AuthUser implements Parcelable {

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
    private String role;

    // Constructor
    public AuthUser(int id, String name, String email, String dni, int idRol, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dni = dni;
        this.idRol = idRol;
        this.role = role;
    }

    // Constructor for Parcelable
    protected AuthUser(Parcel in) {
        id = in.readInt();
        name = in.readString();
        email = in.readString();
        dni = in.readString();
        idRol = in.readInt();
        role = in.readString();
    }

    // CREATOR for Parcelable
    public static final Creator<AuthUser> CREATOR = new Creator<AuthUser>() {
        @Override
        public AuthUser createFromParcel(Parcel in) {
            return new AuthUser(in);
        }

        @Override
        public AuthUser[] newArray(int size) {
            return new AuthUser[size];
        }
    };

    // Getters
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

    public String getRole() {
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
        dest.writeString(role);
    }
}
