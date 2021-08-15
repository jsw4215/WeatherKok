package com.example.weatherkok.weather;

import android.util.Log;

import com.example.weatherkok.weather.interfaces.WeatherContract;
import com.example.weatherkok.weather.interfaces.WeatherRetrofitInterface;
import com.example.weatherkok.weather.models.midWx.ResponseParams;
import com.example.weatherkok.weather.models.midTemp.MidTempResponse;
import com.example.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.example.weatherkok.weather.models.xy;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.weatherkok.src.ApplicationClass.getRetrofitForShortWx;
import static com.example.weatherkok.src.ApplicationClass.getRetrofitForWeather;

public class WeatherService {
    private static final String TAG = WeatherService.class.getSimpleName();

    private WeatherContract.ActivityView mWeatherContractActivityView;

    public WeatherService(WeatherContract.ActivityView WeatherContractActivityView) {
        this.mWeatherContractActivityView = WeatherContractActivityView;
    }

    public void getMidLandFcst(String key, int numOfRows, int pageNo,
                        String dataType, String regId, String tmFc,int num) {
        WeatherRetrofitInterface weatherRetrofitInterface = getRetrofitForWeather().create(WeatherRetrofitInterface.class);
        Log.i(TAG, "Creating Retrofit중기예보");
        weatherRetrofitInterface.getMidLandFcst(key, numOfRows, pageNo, dataType, regId, tmFc).enqueue(new Callback<ResponseParams>() {
            @Override
            public void onResponse(Call<ResponseParams> call, Response<ResponseParams> response) {
                final ResponseParams responseparams = response.body();
                if (responseparams == null||responseparams.getResponse().getBody()==null||responseparams.getResponse().getBody().getItems().getItem().get(0)==null) {
                    mWeatherContractActivityView.validateFailure(null, regId, num);
                    return;
                }
                Log.i(TAG, "Success");
                Log.i(TAG, responseparams.getResponse().getBody().items.item.get(0).wf3Am);
                mWeatherContractActivityView.validateSuccess(response.isSuccessful(), responseparams.getResponse(),num);
            }

            @Override
            public void onFailure(Call<ResponseParams> call, Throwable t) {
                Log.i(TAG, "Failure");
                mWeatherContractActivityView.validateFailure(null, regId,num);
            }
        });
    }

    public void getShortFcst(            String key,
                                  int numOfRows,
                                  int pageNo,
                                  String dataType,
                                  String baseDate,
                                  String baseTime,
                                  xy data,
                                         int position) {
        WeatherRetrofitInterface weatherRetrofitInterface = getRetrofitForShortWx().create(WeatherRetrofitInterface.class);

        String[] splitx = new String[2];
        splitx = data.getX().split("\\.");
        String[] splity = new String[2];
        splity = data.getY().split("\\.");

        int intnx = Integer.parseInt(splitx[0]);
        int intny = Integer.parseInt(splity[0]);

        Log.i(TAG, "Creating Retrofit");
        weatherRetrofitInterface.getShortFcst(key, numOfRows, pageNo, dataType, baseDate, baseTime, intnx, intny).enqueue(new Callback<ShortsResponse>() {
            @Override
            public void onResponse(Call<ShortsResponse> call, Response<ShortsResponse> response) {
                final ShortsResponse shortsResponse = response.body();
                if (shortsResponse == null||shortsResponse.getResponse().getBody()==null) {
                    mWeatherContractActivityView.validateShortFailure(null, data, position);
                    return;
                }
                Log.i(TAG, "Success");
                Log.i(TAG, "단기예보 도착" + shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstDate());
                mWeatherContractActivityView.validateShortSuccess(response.isSuccessful(), shortsResponse, data.getLat(), data.getLon(), position);
            }

            @Override
            public void onFailure(Call<ShortsResponse> call, Throwable t) {
                Log.i(TAG, "Failure");
                mWeatherContractActivityView.validateShortFailure(null, data, position);
            }
        });
    }

    public void getMidTemp(String key, int numOfRows, int pageNo,
                    String dataType, String regId, String tmFc,int num) {
        WeatherRetrofitInterface weatherRetrofitInterface = getRetrofitForWeather().create(WeatherRetrofitInterface.class);
        Log.i(TAG, "Creating Retrofit");
        weatherRetrofitInterface.getMidTemp(key, numOfRows, pageNo, dataType, regId, tmFc).enqueue(new Callback<MidTempResponse>() {
            @Override
            public void onResponse(Call<MidTempResponse> call, Response<MidTempResponse> response) {
                final MidTempResponse midTempResponse = response.body();
                if (midTempResponse == null||midTempResponse.getResponse().getBody()==null||midTempResponse.getResponse().getBody().getItems().getItem().get(0)==null) {
                    mWeatherContractActivityView.validateMidTempFailure(null, regId, num);
                    return;
                }
                Log.i(TAG, "Success");
                Log.i(TAG, "중기온도 도착" + midTempResponse.getResponse().getBody().getItems().item.get(0).getTaMax3Low());
                mWeatherContractActivityView.validateMidTempSuccess(midTempResponse, num);
            }

            @Override
            public void onFailure(Call<MidTempResponse> call, Throwable t) {
                Log.i(TAG, "Failure");
                mWeatherContractActivityView.validateMidTempFailure(null, regId, num);
            }
        });
    }

}
