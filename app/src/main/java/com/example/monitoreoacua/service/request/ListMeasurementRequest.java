package com.example.monitoreoacua.service.request;


/**
 * Request class for listing measurements
 */
public class ListMeasurementRequest extends BaseRequest {

    public ListMeasurementRequest() {
        super();
        setRequiresAuthentication(true);
    }
}