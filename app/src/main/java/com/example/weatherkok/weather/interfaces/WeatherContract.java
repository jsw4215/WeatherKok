package com.example.weatherkok.weather.interfaces;

import com.example.weatherkok.where.models.Record;
import com.example.weatherkok.where.models.Result;

public interface WeatherContract {

    interface ActivityView {

        void validateSuccess(boolean isSuccess, String data);

        void validateFailure(String message);

    }

    interface Presenter {



    }

}
