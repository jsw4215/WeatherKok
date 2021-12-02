package com.devpilot.weatherkok.when.interfaces;

import com.devpilot.weatherkok.when.models.ResponseParams;
import com.devpilot.weatherkok.when.models.single.ResponseSingle;

public interface RestContract {

    interface ActivityView {

        void validateSuccess(boolean isSuccess, ResponseParams responseInBody,String year, String month);

        void validateSuccessSingle(boolean isSuccess, ResponseSingle responseSingle,String year, String month);

        void validateFailure(String message,String year, String month);

        void validateFaliureSingle(String message, String year, String month);
    }

    interface Presenter {

        void fromSidoToSigungu();


    }



}
