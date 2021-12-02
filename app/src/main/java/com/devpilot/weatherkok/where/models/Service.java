package com.devpilot.weatherkok.where.models;

public class Service {
    String name;
    String version;
    String operation;
    String time;

    public Service() {
    }

    public Service(String name, String version, String operation, String time) {
        this.name = name;
        this.version = version;
        this.operation = operation;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
