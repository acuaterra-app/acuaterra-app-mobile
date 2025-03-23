package com.example.monitoreoacua.service.response;

import com.google.gson.annotations.SerializedName;

public class Pagination {
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

    public int getTotal() { return total; }
    public int getTotalPages() { return totalPages; }
    public int getCurrentPage() { return currentPage; }
    public int getPerPage() { return perPage; }
    public boolean isHasNext() { return hasNext; }
    public boolean isHasPrev() { return hasPrev; }
}

