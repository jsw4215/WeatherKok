package com.devpilot.weatherkok.weather;

import android.content.Context;
import android.util.Log;

import com.devpilot.weatherkok.weather.interfaces.NaverContract;
import com.devpilot.weatherkok.weather.interfaces.NaverRetrofitInterface;
import com.devpilot.weatherkok.weather.utils.NaverData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.devpilot.weatherkok.src.ApplicationClass.getNaverRetrofit;

public class NaverService {
    private static final String TAG = NaverService.class.getSimpleName();

    private NaverContract mNaverContract;
    Context mContext;

    public NaverService(NaverContract mNaverContract, Context mContext) {
        this.mNaverContract = mNaverContract;
        this.mContext = mContext;
    }

    public void getNaverGeo(String id, String secret, String address, int type, int position) {
        NaverRetrofitInterface naverRetrofitInterface = getNaverRetrofit().create(NaverRetrofitInterface.class);
        Log.i(TAG, "Creating Retrofit중기예보");

        // show it
        naverRetrofitInterface.getNaverGeo(id, secret, address).enqueue(new Callback<NaverData>() {
            @Override
            public void onResponse(Call<NaverData> call, Response<NaverData> response) {
                final NaverData naverData = response.body();
                if (naverData == null||!naverData.getStatus().equals("OK")) {
                    mNaverContract.naverFailure(null);
                    return;
                }
                Log.i(TAG, "Success");
                Log.i(TAG, naverData.getStatus());
                mNaverContract.naverSuccess(response.isSuccessful(), naverData, type, position);
            }

            @Override
            public void onFailure(Call<NaverData> call, Throwable t) {
                Log.i(TAG, "Failure");
                mNaverContract.naverFailure(null);
            }
        });
    }



}
