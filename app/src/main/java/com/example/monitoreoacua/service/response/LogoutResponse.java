package com.example.monitoreoacua.service.response;

/**
 * Response class for logout endpoint.
 * Extends the generic ApiResponse with a specific Data type.
 */
public class LogoutResponse extends ApiResponse<LogoutResponse.Data> {

    public static class Data {
        // Empty class as the logout response typically has an empty data array
        // Can be extended with fields if the API response changes in the future
    }
}

