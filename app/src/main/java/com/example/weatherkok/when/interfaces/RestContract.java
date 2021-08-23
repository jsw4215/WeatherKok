package com.example.weatherkok.when.interfaces;

import android.content.Context;

import com.example.weatherkok.when.models.ResponseParams;
import com.example.weatherkok.when.models.RestResponse;
import com.example.weatherkok.when.models.single.ResponseSingle;

import okhttp3.Response;

public interface RestContract {

    interface ActivityView {

        void run(String year, String month, Context context);

        void validateSuccess(boolean isSuccess, ResponseParams responseInBody,String year, String month);

        void validateSuccessSingle(boolean isSuccess, ResponseSingle responseSingle,String year, String month);

        void validateFailure(String message,String year, String month);

        void validateFaliureSingle(String message, String year, String month);
    }

    interface Presenter {

        void fromSidoToSigungu();


    }



}
