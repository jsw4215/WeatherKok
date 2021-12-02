package com.devpilot.weatherkok.weather.models.midWx;


import java.util.ArrayList;

public class WxItems {
private static final String TAG = WxItems.class.getSimpleName();



        public ArrayList<WxItem> item = new ArrayList<>();

        public ArrayList<WxItem> getItem() {
            return item;
        }

        public void setItem(ArrayList<WxItem> item) {
            this.item = item;
        }



}
