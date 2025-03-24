package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Farm;

/**
 * Response class for a single farm API request.
 */
public class FarmResponse extends ApiResponse<Farm> {
    
    /**
     * Get the farm from the response.
     * 
     * @return The farm object or null if no data is available
     */
    public Farm getFarm() {
        return getFirstDataItem();
    }
}

