package com.example.weatherkok.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.weatherkok.R;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.interfaces.WeatherContract;
import com.example.weatherkok.where.interfaces.WhereContract;

import java.math.BigInteger;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.http.Query;

public class WeatherActivity extends BaseActivity implements WeatherContract.ActivityView {
    String TAG = "WeatherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Log.i(TAG, "OnCreate");

        String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        int numOfRows = 10;
        int pageNo = 1;
        String dataType = "JSON";
        String regId = "11B00000";
        String tmFc;
        tmFc = "202108030600";

        WeatherService weatherService = new WeatherService(this);
        weatherService.getMidLandFcst(key, numOfRows, pageNo, dataType, regId, tmFc);









    }

    @Override
    public void validateSuccess(boolean isSuccess, String data) {

    }

    @Override
    public void validateFailure(String message) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}