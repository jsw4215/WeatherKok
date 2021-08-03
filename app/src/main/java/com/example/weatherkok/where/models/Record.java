package com.example.weatherkok.where.models;

public class Record {
    String total;
    String current;

    public Record() {
    }

    public Record(String total, String current) {
        this.total = total;
        this.current = current;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
}
