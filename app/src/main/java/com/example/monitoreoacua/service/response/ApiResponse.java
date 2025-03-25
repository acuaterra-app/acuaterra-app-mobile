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
    
    @SerializedName("meta")
    private Meta meta;

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
    
    public Meta getMeta() {
        return meta;
    }
    
    public void setMeta(Meta meta) {
        this.meta = meta;
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
    
    public static class Meta {
        @SerializedName("totalItems")
        private int totalItems;
        
        @SerializedName("totalPages")
        private int totalPages;
        
        @SerializedName("currentPage")
        private int currentPage;
        
        @SerializedName("itemsPerPage") 
        private int itemsPerPage;
        
        public int getTotalItems() {
            return totalItems;
        }
        
        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
        
        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
        
        public int getCurrentPage() {
            return currentPage;
        }
        
        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }
        
        public int getItemsPerPage() {
            return itemsPerPage;
        }
        
        public void setItemsPerPage(int itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
        }
    }
}

