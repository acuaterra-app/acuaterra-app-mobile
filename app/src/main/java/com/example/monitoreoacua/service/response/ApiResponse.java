package com.example.monitoreoacua.service.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Base class for all API responses with standardized structure.
 * @param <T> The type of data contained in the response
 */
public class ApiResponse<T> {
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<T> data;
    
    @SerializedName("errors")
    private List<String> errors;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
    

    public T getFirstDataItem() {
        if (data != null && !data.isEmpty()) {
            return data.get(0);
        }
        return null;
    }
}

