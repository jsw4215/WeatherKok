package com.example.weatherkok.when.models.single;

import com.google.gson.annotations.SerializedName;

public class Body {
    private static final String TAG = Body.class.getSimpleName();

    @SerializedName("items")
    private Items items;
    @SerializedName("numOfRows")
    private Integer numOfRows;
    @SerializedName("pageNo")
    private Integer pageNo;
    @SerializedName("totalCount")
    private Integer totalCount;

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public Integer getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(Integer numOfRows) {
        this.numOfRows = numOfRows;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

}
