package com.devpilot.weatherkok.weather.interfaces;

import com.devpilot.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.devpilot.weatherkok.weather.models.midTemp.MidTempResponse;
import com.devpilot.weatherkok.weather.models.midWx.ResponseParams;

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

    @GET("getVilageFcst")
    Call<ShortsResponse> getShortFcst(
            @Query("serviceKey") final String key,
            @Query("numOfRows") int numOfRows,
            @Query("pageNo") int pageNo,
            @Query("dataType") String dataType,
            @Query("base_date") String baseDate,
            @Query("base_time") String baseTime,
            @Query("nx") int nx,
            @Query("ny") int ny
    );

    @GET("getMidTa")
    Call<MidTempResponse> getMidTemp(
            @Query("serviceKey") final String key,
            @Query("numOfRows") int numOfRows,
            @Query("pageNo") int pageNo,
            @Query("dataType") String dataType,
            @Query("regId") String regId,
            @Query("tmFc") String tmFc
    );

}
