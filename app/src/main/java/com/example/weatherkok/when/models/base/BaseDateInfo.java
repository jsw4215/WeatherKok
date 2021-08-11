package com.example.weatherkok.when.models.base;

import com.example.weatherkok.when.models.Schedule;

public class BaseDateInfo {
    private static final String TAG = BaseDateInfo.class.getSimpleName();
    //solar 날짜 원래 몇일인지 저장
    int date;
    //공휴일 이라면, 이름
    String nameOfDay;
    //음력은 몇일인지
    String luna;
    //누구와 언제 어디서 무슨 스케쥴이 있는지 정보
    Schedule schedule;

    public BaseDateInfo(int solar, String nameOfDay, String luna, Schedule schedule) {
        this.date = solar;
        this.nameOfDay = nameOfDay;
        this.luna = luna;
        this.schedule = schedule;
    }

    public BaseDateInfo() {
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getNameOfDay() {
        return nameOfDay;
    }

    public void setNameOfDay(String nameOfDay) {
        this.nameOfDay = nameOfDay;
    }

    public String getLuna() {
        return luna;
    }

    public void setLuna(String luna) {
        this.luna = luna;
    }
}
