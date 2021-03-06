package com.devpilot.weatherkok.where.interfaces;

import com.devpilot.weatherkok.where.models.ResponseParams;
import com.devpilot.weatherkok.where.models.SidoResponse;
import com.devpilot.weatherkok.where.models.WhereRequestBody;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WhereRetrofitInterface {

    @GET("data")
    Call<ResponseParams> getSidoList(
            @Query("key") final String key,
            @Query("domain") String domain,
            @Query("request") String request,
            @Query("format") String format,
            @Query("size") int size,
            @Query("page") int page,
            @Query("geometry") boolean geometry,
            @Query("attribute") boolean attribute,
            @Query("crs") String crs,
            @Query("geomfilter") String geomfilter,
            @Query("data") String data
            //,@Path("number") int number
    );

    @GET("data")
    Call<ResponseParams> getSigunguList(
            @Query("key") final String key,
            @Query("domain") final String domain,
            @Query("request") String request,
            @Query("format") String format,
            @Query("size") int size,
            @Query("page") int page,
            @Query("geometry") boolean geometry,
            @Query("attribute") boolean attribute,
            @Query("crs") String crs,
            @Query("geomfilter") String geomfilter,
            @Query("data") String data
            //,@Path("number") int number
    );

    @GET("data")
    Call<ResponseParams> getEmdList(
            @Query("key") final String key,
            @Query("domain") final String domain,
            @Query("request") String request,
            @Query("format") String format,
            @Query("size") int size,
            @Query("page") int page,
            @Query("geometry") boolean geometry,
            @Query("attribute") boolean attribute,
            @Query("crs") String crs,
            @Query("geomfilter") String geomfilter,
            @Query("data") String data
            //,@Path("number") int number
    );

    Call<SidoResponse> getSidoList(WhereRequestBody whereRequestBody);

//    @POST("/test")
//    Call<DefaultResponse> postTest(@Body RequestBody params);
//
//    @POST("/login")
//    Call<SignInResponse> postSignInInfo(@Body SignInBody signInBody);

}
