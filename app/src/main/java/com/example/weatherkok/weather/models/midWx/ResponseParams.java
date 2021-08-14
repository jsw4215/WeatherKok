package com.example.weatherkok.weather.models.midWx;

public class ResponseParams {
    private static final String TAG = ResponseParams.class.getSimpleName();

    WxResponse response;

    public WxResponse getResponse() {
        return response;
    }

    public void setResponse(WxResponse response) {
        this.response = response;
    }
}
