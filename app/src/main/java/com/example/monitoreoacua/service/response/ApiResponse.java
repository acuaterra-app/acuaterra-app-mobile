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
        @SerializedName("pagination")
        private Pagination pagination;
        
        public Pagination getPagination() {
            return pagination;
        }
        
        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
        
        public static class Pagination {
            @SerializedName("total")
            private int total;
            
            @SerializedName("totalPages")
            private int totalPages;
            
            @SerializedName("currentPage")
            private int currentPage;
            
            @SerializedName("perPage") 
            private int perPage;
            
            @SerializedName("hasNext")
            private boolean hasNext;
            
            @SerializedName("hasPrev")
            private boolean hasPrev;
            
            public int getTotal() {
                return total;
            }
            
            public void setTotal(int total) {
                this.total = total;
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
            
            public int getPerPage() {
                return perPage;
            }
            
            public void setPerPage(int perPage) {
                this.perPage = perPage;
            }
            
            public boolean isHasNext() {
                return hasNext;
            }
            
            public void setHasNext(boolean hasNext) {
                this.hasNext = hasNext;
            }
            
            public boolean isHasPrev() {
                return hasPrev;
            }
            
            public void setHasPrev(boolean hasPrev) {
                this.hasPrev = hasPrev;
            }
        }
    }
}

