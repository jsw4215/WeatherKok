package com.example.weatherkok.weather.interfaces;

import com.example.weatherkok.weather.models.WxResponse;
import com.example.weatherkok.when.models.Body;
import com.example.weatherkok.weather.models.ResponseParams;
import com.example.weatherkok.where.models.Record;
import com.example.weatherkok.where.models.Result;

public interface WeatherContract {

    interface ActivityView {

        void validateSuccess(boolean isSuccess, WxResponse wxResponse);

        void validateFailure(String message);

    }

    interface Presenter {



    }

}
