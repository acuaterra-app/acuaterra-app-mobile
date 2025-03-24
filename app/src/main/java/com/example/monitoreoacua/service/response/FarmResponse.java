package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Farm;

/**
 * Response wrapper for single Farm API responses.
 * Extends the base ApiResponse with Farm as the data type.
 */
public class FarmResponse extends ApiResponse<Farm> {
    /**
     * Returns the farm object from the response.
     * This is a convenience method that returns the first item from the data list.
     *
     * @return The Farm object or null if no data is available
     */
    public Farm getFarm() {
        return getFirstDataItem();
    }
}

