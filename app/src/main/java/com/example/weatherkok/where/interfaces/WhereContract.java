package com.example.weatherkok.where.interfaces;

import com.example.weatherkok.where.models.Record;
import com.example.weatherkok.where.models.Result;

public interface WhereContract {

    interface ActivityView {

        void validateSuccess(boolean isSuccess, Record record, Result result);

        void validateSggSuccess(boolean isSuccess, Record record, Result result);

        void validateEmdSuccess(boolean isSuccess, Record record, Result result);

        void validateFailure(String message);

    }

    interface Presenter {

        void fromSidoToSigungu();


    }

}
