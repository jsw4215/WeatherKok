package com.example.weatherkok.when.interfaces;

import com.example.weatherkok.when.models.ResponseParams;
import com.example.weatherkok.when.models.RestResponse;
import com.example.weatherkok.when.models.single.ResponseSingle;

import okhttp3.Response;

public interface RestContract {

    interface ActivityView {

        void validateSuccess(boolean isSuccess, ResponseParams responseInBody);

        void validateSuccessSingle(boolean isSuccess, ResponseSingle responseSingle);

        void validateFailure(String message);

        void validateFaliureSingle(String message);
    }

    interface Presenter {

        void fromSidoToSigungu();


    }



}
