package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Measurement;
import java.util.List;


/**
 * Response class for measurements list
 */
public class ListMeasurementResponse extends ApiResponse<Measurement> {

    /**
     * Get all measurements from the response
     * @return List of measurements
     */
    public List<Measurement> getAllMeasurements() {
        return getData();
    }
}