package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListUserResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<List<User>> nestedData;

    @SerializedName("errors")
    private List<String> errors;

    @SerializedName("meta")
    private Meta meta;

    public String getMessage() {
        return message;
    }

    public List<User> getData() {
        return nestedData != null && !nestedData.isEmpty() ? nestedData.get(0) : null;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Meta getMeta() {
        return meta;
    }

    public static class Meta {
        @SerializedName("pagination")
        private Pagination pagination;

        public Pagination getPagination() {
            return pagination;
        }
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

        public int getTotalPages() {
            return totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getPerPage() {
            return perPage;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public boolean isHasPrev() {
            return hasPrev;
        }
    }
}