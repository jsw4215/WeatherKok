package com.devpilot.weatherkok.weather.interfaces;

import com.devpilot.weatherkok.weather.utils.NaverData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NaverRetrofitInterface {

    @GET("geocode")
    Call<NaverData> getNaverGeo(
            @Query("X-NCP-APIGW-API-KEY-ID") String id,
            @Query("X-NCP-APIGW-API-KEY") String secret,
            @Query("query") String address
    );

}
