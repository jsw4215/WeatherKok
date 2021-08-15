package com.example.weatherkok.weather.models.shortsExpectation;

public class todayTemp {
    private static final String TAG = todayTemp.class.getSimpleName();
    String temperature;
    String timeHhmm;

    public todayTemp() {
    }

    public todayTemp(String temperature, String timeHhmm) {
        this.temperature = temperature;
        this.timeHhmm = timeHhmm;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTimeHhmm() {
        return timeHhmm;
    }

    public void setTimeHhmm(String timeHhmm) {
        this.timeHhmm = timeHhmm;
    }
}
