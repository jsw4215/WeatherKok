package com.devpilot.weatherkok.when.models.single;

import java.util.ArrayList;

public class ScheduleList {
    private static final String TAG = ScheduleList.class.getSimpleName();
    ArrayList<Schedule> scheduleArrayList;

    public ScheduleList() {
    }

    public ScheduleList(ArrayList<Schedule> scheduleArrayList) {
        this.scheduleArrayList = scheduleArrayList;
    }

    public ArrayList<Schedule> getScheduleArrayList() {
        return scheduleArrayList;
    }

    public void setScheduleArrayList(ArrayList<Schedule> scheduleArrayList) {
        this.scheduleArrayList = scheduleArrayList;
    }
}
