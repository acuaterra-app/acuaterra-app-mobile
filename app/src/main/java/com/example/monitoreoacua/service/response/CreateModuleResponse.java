package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.business.models.User;
import java.util.Date;
import java.util.List;

public class CreateModuleResponse extends ApiResponse<CreateModuleResponse.Data> {

    public int getId() {
        Data data = getFirstDataItem();
        return data != null ? data.getModule().getId() : 0;
    }

    public static class Data {
        private Module module;
        private SensorUser sensorUser;
        private List<String> errors;

        public Module getModule() {
            return module;
        }

        public SensorUser getSensorUser() {
            return sensorUser;
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    public static class Module {
        private int id;
        private String name;
        private String location;
        private String latitude;
        private String longitude;
        private String speciesFish;
        private String fishQuantity;
        private String fishAge;
        private String dimensions;
        private int idFarm;
        private int createdByUserId;
        private Date createdAt;
        private String updatedAt;
        private String deletedAt;
        private User creator;
        private Farm farm;

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
        public int getCreatedByUserId() { return createdByUserId; }
        public Date getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public String getDeletedAt() { return deletedAt; }
        public User getCreator() { return creator; }
        public Farm getFarm() { return farm; }
    }

    public static class SensorUser {
        private int id;
        private String email;
        private String role;

        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }
}

