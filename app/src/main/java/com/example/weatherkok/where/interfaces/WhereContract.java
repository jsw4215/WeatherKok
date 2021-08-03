package com.example.weatherkok.where.interfaces;

import com.example.weatherkok.where.models.Record;
import com.example.weatherkok.where.models.Result;

public interface WhereContract {

    interface ActivityView {

        void validateSuccess(boolean isSuccess, Record record, Result result);

        void validateFailure(String message);

    }

    interface Presenter {



    }

}
