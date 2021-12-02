package com.devpilot.weatherkok.datalist.data.wxdata.pm;

public class PM {
    private static final String TAG = PM.class.getSimpleName();
    int rainPercent;
    String wxCondition;
    int tempMax;
    int tempMin;

    public PM() {
    }

    public PM(int rainPercent, String wxCondition, int tempMax, int tempMin) {
        this.rainPercent = rainPercent;
        this.wxCondition = wxCondition;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
    }

    public int getRainPercent() {
        return rainPercent;
    }

    public void setRainPercent(int rainPercent) {
        this.rainPercent = rainPercent;
    }

    public String getWxCondition() {
        return wxCondition;
    }

    public void setWxCondition(String wxCondition) {
        this.wxCondition = wxCondition;
    }

    public int getTempMax() {
        return tempMax;
    }

    public void setTempMax(int tempMax) {
        this.tempMax = tempMax;
    }

    public int getTempMin() {
        return tempMin;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }
}
