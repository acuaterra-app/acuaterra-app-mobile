package com.example.monitoreoacua.service.response;
import com.example.monitoreoacua.business.models.Farm;

import java.util.List;

public class ListFarmResponse extends ApiResponse<Farm> {
    public List<Farm> getAllFarms() {
        return getData();
    }
}
