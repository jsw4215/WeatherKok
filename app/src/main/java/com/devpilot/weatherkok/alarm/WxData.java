package com.devpilot.weatherkok.alarm;

public class WxData {
    private static final String TAG = WxData.class.getSimpleName();

    int am;
    int pm;

    public WxData() {
    }

    public WxData(int am, int pm) {
        this.am = am;
        this.pm = pm;
    }

    public int getAm() {
        return am;
    }

    public void setAm(int am) {
        this.am = am;
    }

    public int getPm() {
        return pm;
    }

    public void setPm(int pm) {
        this.pm = pm;
    }
}
