package com.example.monitoreoacua.service.response;

import android.widget.Toast;

import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;

import java.util.List;

public class ListFarmResponse extends ApiResponse<Farm> {

    public Farm getFirstFarm() {
        return getFirstDataItem();
    }

    public List<Farm> getAllFarms() {
        return getData();
    }
}
