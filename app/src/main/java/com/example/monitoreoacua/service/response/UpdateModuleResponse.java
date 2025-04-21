package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.business.models.User;
import com.google.gson.annotations.SerializedName;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateModuleResponse extends ApiResponse<UpdateModuleResponse.Data> {

    @SerializedName("statusCode")
    private int statusCode;

    public static class Data {
        @SerializedName("module")
        public Module moduleData;

        @SerializedName("sensorUser")
        public User sensorUser;
    }

    public List<UpdateModuleResponse.Data> getResponseModuleData() {
        return getData();
    }

}

