package com.example.monitoreoacua.service;

import com.example.monitoreoacua.business.models.Measurement;
import com.example.monitoreoacua.service.request.ListMeasurementRequest;
import com.example.monitoreoacua.service.response.ListMeasurementResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
/**
 * Interface for measurement-related API operations.
 * Contains methods for retrieving aquaponic system measurements
 */
public interface ApiMeasurementsService {


    /**
     * Get measurements from the aquaponic system
     *
     * @param moduleId The ID of the module from which to fetch measurements
     * @param token The authorization token
     * @return A Call object containing the list of measurements
     */
    @GET("/api/v2/module/measurement")
    Call<ListMeasurementResponse> getMeasurements(
            @Query("moduleId") int moduleId,
            @Query("sensorId") int sensorId,
            @Header("Authorization") String token
    );

    /**
     * Get the latest measurement for a specific sensor
     *
     * @param sensorId The ID of the sensor
     * @param token The authorization token
     * @return A Call object containing the measurement
     */
    @GET("/api/v2/measurements/sensor/{sensorId}/latest")
    Call<ListMeasurementResponse> getLatestMeasurement(
            @Path("sensorId") int sensorId,
            @Header("Authorization") String token
    );
}
