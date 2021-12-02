package com.devpilot.weatherkok.weather.models.midTemp;

import java.util.ArrayList;

public class TempItems {
    private static final String TAG = TempItems.class.getSimpleName();

        public ArrayList<TempItem> item = new ArrayList<>();

        public ArrayList<TempItem> getItem() {
            return item;
        }

        public void setItem(ArrayList<TempItem> item) {
            this.item = item;
        }

}
