package com.example.weatherkok.weather.interfaces;

import com.example.weatherkok.weather.models.midWx.WxResponse;
import com.example.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.example.weatherkok.weather.utils.NaverData;

public interface NaverContract {

    void naverSuccess(boolean isSuccess, NaverData naverData, int type, int position);

    void naverFailure(String message);


}
