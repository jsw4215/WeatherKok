package com.devpilot.weatherkok.when.models.single;

import com.google.gson.annotations.Expose;

public class Items {
    private static final String TAG = Items.class.getSimpleName();

    @Expose
    private Item item = null;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
