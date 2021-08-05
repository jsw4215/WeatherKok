package com.example.weatherkok.datalist.data.wxdata.am;

public class AM {
    private static final String TAG = AM.class.getSimpleName();
    String rain;
    String snow;
    String cloud;
    String windy;
    int temperature;

    public AM() {
    }

    public AM(String rain, String snow, String cloud, String windy, int temperature) {
        this.rain = rain;
        this.snow = snow;
        this.cloud = cloud;
        this.windy = windy;
        this.temperature = temperature;
    }


    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getSnow() {
        return snow;
    }

    public void setSnow(String snow) {
        this.snow = snow;
    }

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public String getWindy() {
        return windy;
    }

    public void setWindy(String windy) {
        this.windy = windy;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
