package com.example.weatherkok.when.interfaces;

import com.example.weatherkok.when.models.ResponseParams;
import com.example.weatherkok.when.models.RestResponse;
import com.example.weatherkok.when.models.single.ResponseSingle;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestInfoInterface {

    @GET("getRestDeInfo")
    Call<ResponseParams> getRestInfo(
            @Query("solYear") String year,
            @Query("solMonth") String month,
            @Query("ServiceKey") String key,
            @Query("_type") String type
    );

    @GET("getRestDeInfo")
    Call<ResponseSingle> getRestInfoForOneDay(
            @Query("solYear") String year,
            @Query("solMonth") String month,
            @Query("ServiceKey") String key,
            @Query("_type") String type
    );

}
