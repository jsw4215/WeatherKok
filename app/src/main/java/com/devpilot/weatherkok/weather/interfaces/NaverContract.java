package com.devpilot.weatherkok.weather.interfaces;

import com.devpilot.weatherkok.weather.utils.NaverData;

public interface NaverContract {

    void naverSuccess(boolean isSuccess, NaverData naverData, int type, int position);

    void naverFailure(String message);


}
