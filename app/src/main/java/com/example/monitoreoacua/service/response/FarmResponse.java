package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Farm;

/**
 * Response wrapper for Farm API responses.
 * Extends the base ApiResponse with FarmData as the data type.
 * Follows the same pattern as LoginResponse.
 */
public class FarmResponse extends ApiResponse<Farm> {
    
    /**
     * Returns the Farm object from the response.
     * @return The Farm object or null if no data is available
     */
    public Farm getFarm() {
        return getFirstDataItem();
    }
}
