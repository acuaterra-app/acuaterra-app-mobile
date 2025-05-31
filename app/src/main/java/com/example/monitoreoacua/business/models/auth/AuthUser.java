package com.example.monitoreoacua.business.models.auth;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.monitoreoacua.business.models.Role;
import com.example.monitoreoacua.business.utils.RolePermissionHelper;
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

    public AuthUser(int id, String name, String email, String dni, int idRol, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dni = dni;
        this.idRol = idRol;
        this.role = role;
    }

    protected AuthUser(Parcel in) {
        id = in.readInt();
        name = in.readString();
        email = in.readString();
        dni = in.readString();
        idRol = in.readInt();
        role = in.readString();
    }

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
        if (role == null) {
            return null;
        }
        
        // Check if the role is "monitor" (case-insensitive) and normalize it
        if ("monitor".equalsIgnoreCase(role)) {
            // Return the exact constant from RolePermissionHelper to ensure consistency
            return RolePermissionHelper.ROLE_MONITOR;
        }
        
        // For other roles, just normalize to lowercase
        return role.toLowerCase();
    }

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
    
    @Override
    public String toString() {
        return "AuthUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", dni='" + dni + '\'' +
                ", idRol=" + idRol +
                ", role='" + role + '\'' +
                ", normalizedRole='" + getRole() + '\'' +
                '}';
    }
}
