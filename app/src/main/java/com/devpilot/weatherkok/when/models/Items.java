package com.devpilot.weatherkok.when.models;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Items {
    private static final String TAG = Items.class.getSimpleName();

    @Expose
    private List<Item> item = null;

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }
}
