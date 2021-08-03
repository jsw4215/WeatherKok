package com.example.weatherkok.weather;

import android.util.Log;

import com.example.weatherkok.weather.interfaces.WeatherContract;
import com.example.weatherkok.weather.interfaces.WeatherRetrofitInterface;
import com.example.weatherkok.weather.models.ResponseParams;
import com.example.weatherkok.weather.models.WxResponse;
import com.example.weatherkok.where.interfaces.WhereRetrofitInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

import static com.example.weatherkok.src.ApplicationClass.getRetrofitForWeather;

public class WeatherService {
    private static final String TAG = WeatherService.class.getSimpleName();

    private WeatherContract.ActivityView mWeatherContractActivityView;

    public WeatherService(WeatherContract.ActivityView mWeatherContractActivityView) {
        this.mWeatherContractActivityView = mWeatherContractActivityView;
    }

    void getMidLandFcst(String key, int numOfRows, int pageNo,
                        String dataType, String regId, String tmFc) {
        WeatherRetrofitInterface weatherRetrofitInterface = getRetrofitForWeather().create(WeatherRetrofitInterface.class);
        Log.i(TAG, "Creating Retrofit");
        weatherRetrofitInterface.getMidLandFcst(key, numOfRows, pageNo, dataType, regId, tmFc).enqueue(new Callback<ResponseParams>() {
            @Override
            public void onResponse(Call<ResponseParams> call, Response<ResponseParams> response) {
                final ResponseParams wxResponse = response.body();
                if (wxResponse == null) {
                    mWeatherContractActivityView.validateFailure(null);
                    return;
                }
                Log.i(TAG, "Success");
                Log.i(TAG, wxResponse.getResponse().getBody().items.item.get(0).wf3Am);
                mWeatherContractActivityView.validateSuccess(response.isSuccessful(), wxResponse.getResponse().getBody().getDataType());
            }

            @Override
            public void onFailure(Call<ResponseParams> call, Throwable t) {
                Log.i(TAG, "Failure");
                mWeatherContractActivityView.validateFailure(null);
            }
        });
    }
}
