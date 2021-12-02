package com.devpilot.weatherkok.when;

import android.util.Log;

import com.devpilot.weatherkok.when.interfaces.RestContract;
import com.devpilot.weatherkok.when.interfaces.RestInfoInterface;
import com.devpilot.weatherkok.when.models.ResponseParams;
import com.devpilot.weatherkok.when.models.single.ResponseSingle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.devpilot.weatherkok.src.ApplicationClass.getRetrofitForRest;

public class CalendarService {
    private static final String TAG = CalendarService.class.getSimpleName();

    private RestContract.ActivityView mRestContractActivityView;

    public CalendarService(RestContract.ActivityView mRestContractActivityView) {
        Log.i(TAG, "constructor : ");
        this.mRestContractActivityView = mRestContractActivityView;
    }

    public void getRestInfo(String year,
                            String month,
                            String key,
                            String type) {
        final RestInfoInterface restInfoInterface = getRetrofitForRest().create(RestInfoInterface.class);
        Log.i(TAG, "Creating Retrofit");
        restInfoInterface.getRestInfo(year, month, key, type).enqueue(new Callback<ResponseParams>() {
            @Override
            public void onResponse(Call<ResponseParams> call, Response<ResponseParams> response) {
                Log.i(TAG, "success : ");
                final ResponseParams responseRest = response.body();
                if (responseRest == null) {
                    mRestContractActivityView.validateFailure(null, year, month);
                    return;
                }
                Log.d("isSuccess : ", String.valueOf(response.isSuccessful()));

                Log.d("get Locdate", String.valueOf(responseRest.getResponse().getBody().getItems().getItem().get(0).getLocdate()));
                Log.d("getDateName : ", responseRest.getResponse().getBody().getItems().getItem().get(0).getDateName());
                mRestContractActivityView.validateSuccess(response.isSuccessful(), responseRest, year, month);
            }

            @Override
            public void onFailure(Call<ResponseParams> call, Throwable t) {
                Log.i(TAG,"failure");
                mRestContractActivityView.validateFailure(null, year, month);
            }
        });
    }


    public void getRestInfoForOneDay(String year,
                            String month,
                            String key,
                            String type) {
        final RestInfoInterface restInfoInterface = getRetrofitForRest().create(RestInfoInterface.class);
        Log.i(TAG, "OneDay Creating Retrofit");
        restInfoInterface.getRestInfoForOneDay(year, month, key, type).enqueue(new Callback<ResponseSingle>() {
            @Override
            public void onResponse(Call<ResponseSingle> call, Response<ResponseSingle> response) {
                Log.i(TAG, "success : ");
                final ResponseSingle responseRest = response.body();
                if (responseRest == null) {
                    mRestContractActivityView.validateFailure(null, year, month);
                    return;
                }
                Log.d("isSuccess : ", String.valueOf(response.isSuccessful()));
                Log.d("get Locdate", String.valueOf(responseRest.getResponse().getBody().getItems().getItem().getLocdate()));
                Log.d("getDateName : ", responseRest.getResponse().getBody().getItems().getItem().getDateName());
                mRestContractActivityView.validateSuccessSingle(response.isSuccessful(), responseRest, year, month);
            }

            @Override
            public void onFailure(Call<ResponseSingle> call, Throwable t) {
                Log.i(TAG,"failure");
                mRestContractActivityView.validateFaliureSingle(null, year, month);
            }
        });
    }


}
