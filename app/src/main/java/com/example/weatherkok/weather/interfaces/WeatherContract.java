package com.example.weatherkok.weather.interfaces;

import com.example.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.example.weatherkok.weather.models.midTemp.MidTempResponse;
import com.example.weatherkok.weather.models.midWx.WxResponse;


public interface WeatherContract {

    interface ActivityView {

        void validateSuccess(boolean isSuccess, WxResponse wxResponse);

        void validateShortSuccess(boolean isSuccess, ShortsResponse shortsResponse);

        void validateFailure(String message);

        void validateShortFailure(String message);

        void validateMidTempSuccess(MidTempResponse midTempResponse);

        void validateMidTempFailure(String message);

    }

    interface Presenter {



    }

}
