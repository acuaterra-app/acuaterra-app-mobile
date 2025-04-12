package com.example.monitoreoacua.service.response;


import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.business.models.User;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class RegisterModuleResponse extends ApiResponse<RegisterModuleResponse.Data> {

    @SerializedName("statusCode")
    private int statusCode;


    public static class Data {
        @SerializedName("module")
        public Module moduleData;

        @SerializedName("sensorUser")
        public User sensorUser;
    }

    public List<Data> getResponseModuleData() {
        return getData();
    }

}
