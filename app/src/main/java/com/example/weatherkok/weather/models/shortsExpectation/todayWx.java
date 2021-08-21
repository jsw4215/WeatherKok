package com.example.weatherkok.weather.models.shortsExpectation;

public class todayWx {
    private static final String TAG = todayWx.class.getSimpleName();
    String timeHhmm;
    //POP 강수확률
    //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
    //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
    //1:강수확률,2:하늘상태,3:강수형태
    int type;
    int weather;
    int rainPercent;
    int rainType;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public todayWx(String timeHhmm, int weather, int rainPercent, int rainType) {
        this.timeHhmm = timeHhmm;
        this.weather = weather;
        this.rainPercent = rainPercent;
        this.rainType = rainType;
    }

    public int getRainPercent() {
        return rainPercent;
    }

    public void setRainPercent(int rainPercent) {
        this.rainPercent = rainPercent;
    }

    public int getRainType() {
        return rainType;
    }

    public void setRainType(int rainType) {
        this.rainType = rainType;
    }

    public todayWx() {
    }

    public String getTimeHhmm() {
        return timeHhmm;
    }

    public void setTimeHhmm(String timeHhmm) {
        this.timeHhmm = timeHhmm;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }
}
