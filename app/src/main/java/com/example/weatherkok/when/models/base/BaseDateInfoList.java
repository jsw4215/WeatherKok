package com.example.weatherkok.when.models.base;

import java.util.ArrayList;

public class BaseDateInfoList {
    private static final String TAG = BaseDateInfoList.class.getSimpleName();
    public ArrayList<BaseDateInfo> baseDateInfoList = new ArrayList<>();

    public BaseDateInfoList() {
    }

    public BaseDateInfoList(ArrayList<BaseDateInfo> baseDateInfoList) {
        this.baseDateInfoList = baseDateInfoList;
    }

    public ArrayList<BaseDateInfo> getBaseDateInfoList() {
        return baseDateInfoList;
    }

    public void setBaseDateInfoList(ArrayList<BaseDateInfo> baseDateInfoList) {
        this.baseDateInfoList = baseDateInfoList;
    }
}