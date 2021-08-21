package com.example.weatherkok.weather.interfaces;

import com.example.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.example.weatherkok.weather.models.midTemp.MidTempResponse;
import com.example.weatherkok.weather.models.midWx.WxResponse;
import com.example.weatherkok.weather.models.xy;


public interface WeatherContract {

    interface ActivityView {

        void validateSuccess(boolean isSuccess, WxResponse wxResponse, int num, int type);

        void validateShortSuccess(boolean isSuccess, ShortsResponse shortsResponse, String lat, String lon, int position, int type);

        void validateFailure(String message, String regId, int num, int type);

        void validateShortFailure(String message, xy data, int position, int type);

        void validateMidTempSuccess(MidTempResponse midTempResponse, int num, int type);

        void validateMidTempFailure(String message, String regId,int num, int type);

    }

    interface Presenter {



    }

}
