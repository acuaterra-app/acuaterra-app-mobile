package com.example.monitoreoacua.service.response;

import com.google.gson.annotations.SerializedName;

public class MetaData {
    @SerializedName("pagination")
    private Pagination pagination;

    public Pagination getPagination() { return pagination; }
}

