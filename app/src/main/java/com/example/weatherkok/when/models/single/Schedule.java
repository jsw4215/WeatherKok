package com.example.weatherkok.when.models.single;

import java.util.ArrayList;

public class Schedule {
    private static final String TAG = Schedule.class.getSimpleName();

    String year;
    String month;
    String date;
    String where;
    ArrayList<String> who;

    public Schedule() {
    }

    public Schedule(String year, String month, String date, String where, ArrayList<String> who) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.where = where;
        this.who = who;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public ArrayList<String> getWho() {
        return who;
    }

    public void setWho(ArrayList<String> who) {
        this.who = who;
    }
}
