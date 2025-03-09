package com.example.monitoreoacua.service.response;

import android.widget.Toast;

import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;

import java.util.List;

public class ListFarmResponse extends ApiResponse<ListFarmResponse.Data>{

    public Farm getFirstFarm() {
        Data data = getFirstDataItem();
        return data != null ? data.getFarm() : null;
    }

    public List<Farm> getAllFarms() {
        Data data = getFirstDataItem();
        return data != null ? data.getFarms() : null;
    }

    public static class Data {
        private List<Farm> farms;

        public List<Farm> getFarms() {
            return farms;
        }

        public Farm getFarm() {
            return (farms != null && !farms.isEmpty()) ? farms.get(0) : null;
        }
    }
}
