package com.example.weatherkok.weather.interfaces;

import com.example.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.example.weatherkok.weather.models.midTemp.MidTempResponse;
import com.example.weatherkok.weather.models.midWx.WxResponse;
import com.example.weatherkok.weather.models.xy;


public interface WeatherContract {

    interface ActivityView {

        void validateSuccess(boolean isSuccess, WxResponse wxResponse, int num);

        void validateShortSuccess(boolean isSuccess, ShortsResponse shortsResponse, String lat, String lon, int position);

        void validateFailure(String message, String regId, int num);

        void validateShortFailure(String message, xy data, int position);

        void validateMidTempSuccess(MidTempResponse midTempResponse, int num);

        void validateMidTempFailure(String message, String regId,int num);

    }

    interface Presenter {



    }

}
