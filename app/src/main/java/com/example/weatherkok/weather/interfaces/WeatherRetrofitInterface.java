package com.example.weatherkok.weather.interfaces;

import com.example.weatherkok.weather.models.ResponseParams;
import com.example.weatherkok.weather.models.WxResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherRetrofitInterface {

    @GET("getMidLandFcst")
    Call<ResponseParams> getMidLandFcst(
            @Query("serviceKey") final String key,
            @Query("numOfRows") int numOfRows,
            @Query("pageNo") int pageNo,
            @Query("dataType") String dataType,
            @Query("regId") String regId,
            @Query("tmFc") String tmFc
    );






}
