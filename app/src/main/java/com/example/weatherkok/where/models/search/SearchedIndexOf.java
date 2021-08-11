package com.example.weatherkok.where.models.search;

public class SearchedIndexOf {
    private static final String TAG = SearchedIndexOf.class.getSimpleName();

    String address;

    int startIndex;

    public SearchedIndexOf() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
}
