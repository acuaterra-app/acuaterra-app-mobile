package com.example.monitoreoacua.business.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a notification in the system.
 */
public class Notification implements Parcelable {

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private NotificationData data;

    public Notification(String title, String message, NotificationData data) {
        this.title = title;
        this.message = message;
        this.data = data;
    }

    protected Notification(Parcel in) {
        title = in.readString();
        message = in.readString();
        data = in.readParcelable(NotificationData.class.getClassLoader());
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationData getData() {
        return data;
    }

    /**
     * Convenience method to check if the notification is unread.
     * @return true if the notification state is not "read", false otherwise
     */
    public boolean isUnread() {
        return data != null && !"read".equals(data.getState());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(message);
        dest.writeParcelable(data, flags);
    }

    /**
     * Represents the data part of a notification.
     */
    public static class NotificationData implements Parcelable {

        @SerializedName("id")
        private int id;

        @SerializedName("state")
        private String state;

        @SerializedName("metaData")
        private Map<String, String> metaData;

        @SerializedName("dateHour")
        private String dateHour;

        public NotificationData(int id, String state, Map<String, String> metaData, String dateHour) {
            this.id = id;
            this.state = state;
            this.metaData = metaData;
            this.dateHour = dateHour;
        }

        protected NotificationData(Parcel in) {
            id = in.readInt();
            state = in.readString();
            metaData = new HashMap<>();
            in.readMap(metaData, String.class.getClassLoader());
            dateHour = in.readString();
        }

        public static final Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
            @Override
            public NotificationData createFromParcel(Parcel in) {
                return new NotificationData(in);
            }

            @Override
            public NotificationData[] newArray(int size) {
                return new NotificationData[size];
            }
        };

        public int getId() {
            return id;
        }

        public String getState() {
            return state;
        }

        public Map<String, String> getMetaData() {
            return metaData;
        }

        public String getDateHour() {
            return dateHour;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(state);
            dest.writeMap(metaData);
            dest.writeString(dateHour);
        }
    }
}
