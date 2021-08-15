package com.example.weatherkok.weather.interfaces;

import com.example.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.example.weatherkok.weather.models.midTemp.MidTempResponse;
import com.example.weatherkok.weather.models.midWx.WxResponse;
import com.example.weatherkok.weather.models.xy;


public interface WeatherContract {

    interface ActivityView {

        void validateSuccess(boolean isSuccess, WxResponse wxResponse);

        void validateShortSuccess(boolean isSuccess, ShortsResponse shortsResponse, String lat, String lon);

        void validateFailure(String message, String regId);

        void validateShortFailure(String message, xy data);

        void validateMidTempSuccess(MidTempResponse midTempResponse);

        void validateMidTempFailure(String message, String regId);

    }

    interface Presenter {



    }

}
