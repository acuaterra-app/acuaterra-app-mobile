package com.example.monitoreoacua.utils;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.example.monitoreoacua.business.models.Farm;

public class FarmBundleUtil {
    private static final String EXTRA_FARM_DATA = "farm_data";
    private static final String EXTRA_FARM_ID = "extra_farm_id";
    private static final String EXTRA_FARM_NAME = "extra_farm_name";
    private static final String EXTRA_FARM_ADDRESS = "extra_farm_address";
    private static final String EXTRA_FARM_LATITUDE = "extra_farm_latitude";
    private static final String EXTRA_FARM_LONGITUDE = "extra_farm_longitude";
    private static final String EXTRA_FARM_CREATED_AT = "extra_farm_created_at";
    private static final String EXTRA_FARM_UPDATED_AT = "extra_farm_updated_at";

    public static void addFarmToIntent(@NonNull Intent intent, @NonNull Farm farm) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_FARM_ID, farm.getId());
        bundle.putString(EXTRA_FARM_NAME, farm.getName());
        bundle.putString(EXTRA_FARM_ADDRESS, farm.getAddress());
        bundle.putString(EXTRA_FARM_LATITUDE, farm.getLatitude());
        bundle.putString(EXTRA_FARM_LONGITUDE, farm.getLongitude());
        bundle.putString(EXTRA_FARM_CREATED_AT, farm.getCreatedAt());
        bundle.putString(EXTRA_FARM_UPDATED_AT, farm.getUpdatedAt());
        intent.putExtra(EXTRA_FARM_DATA, bundle);
    }

    public static Farm getFarmFromIntent(@NonNull Intent intent) {
        Bundle bundle = intent.getBundleExtra(EXTRA_FARM_DATA);
        if (bundle != null) {
            return new Farm(
                bundle.getInt(EXTRA_FARM_ID),
                bundle.getString(EXTRA_FARM_NAME),
                bundle.getString(EXTRA_FARM_ADDRESS),
                bundle.getString(EXTRA_FARM_LATITUDE),
                bundle.getString(EXTRA_FARM_LONGITUDE),
                bundle.getString(EXTRA_FARM_CREATED_AT),
                bundle.getString(EXTRA_FARM_UPDATED_AT)
            );
        }
        return null;
    }
}

