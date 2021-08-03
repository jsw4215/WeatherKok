package com.example.weatherkok.where.models;

public class Page {
    String total;
    String current;
    String size;

    public Page() {
    }

    public Page(String total, String current, String size) {
        this.total = total;
        this.current = current;
        this.size = size;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
