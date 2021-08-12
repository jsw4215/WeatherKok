package com.example.weatherkok.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.weatherkok.R;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.interfaces.WeatherContract;
import com.example.weatherkok.weather.models.WxResponse;
import com.example.weatherkok.when.models.ResponseParams;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.where.interfaces.WhereContract;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.http.Query;

public class WeatherActivity extends BaseActivity implements WeatherContract.ActivityView {
    String TAG = "WeatherActivity";
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Log.i(TAG, "OnCreate");

        manageDataForMidWxExpectation();

    }

    // 등록된 스케쥴에서 위치정보 가져와서
    // 도단위 시단위 찾아서
    // api연결
    private ArrayList<String> getPlaceOfSchedule(){
        
        ScheduleList scheduleList = getScheduleFromPreference();
        
        ArrayList<Schedule> list = scheduleList.getScheduleArrayList();
        
        ArrayList<String> place = new ArrayList<>();

        for(int i =0;i<list.size();i++){
            place.add(list.get(i).getWhere());
        }

        return place;
    }

    private Date getToday(){
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private String calculateToday(){
        Date date = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentYear = curYearFormat.format(date);

        int temp = Integer.parseInt(curMonthFormat.format(date));
        String currentMonth = String.valueOf(temp);
        if(temp<10) {
            currentMonth = "0"+currentMonth;
        }
        String currentDay = curDayFormat.format(date);
        //오늘일자에 저장
        //currentMonth="07";
       return currentYear+currentMonth+currentDay;
    }

    private void midWxService(String place, String Date){
        //중기예보
        //지역 정보 도단위, 시단위
        String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        int numOfRows = 10;
        int pageNo = 1;
        String dataType = "JSON";
        String regId = "11B00000";
        String tmFc;
        tmFc = "202108120600";

        WeatherService weatherService = new WeatherService(this);
        weatherService.getMidLandFcst(key, numOfRows, pageNo, dataType, regId, tmFc);
    }

    private void manageDataForMidWxExpectation(){

        ArrayList<String> targetPlaces = getPlaceOfSchedule();
        ArrayList<String> codeList = new ArrayList<>();

        for(int i =0;i<targetPlaces.size();i++) {
            codeList.add(getPlaceCode(targetPlaces.get(i)));
        }

        Log.i(TAG, "code : " + codeList.get(0) + " " + targetPlaces.get(0));
        Log.i(TAG, "code : " + codeList.get(1) + " " + targetPlaces.get(1));

        midWxService(codeList.get(0), calculateToday());
    }

    private String getPlaceCode(String place) {

        String Code = "";

        if(place.startsWith("서울")||place.startsWith("인천")||place.startsWith("경기도")){
            Code = "11B00000";
        } else if(place.startsWith("강원도영서")){
            Code = "11D10000";
        } else if(place.startsWith("강원도영동")){
            Code = "11D20000";
        } else if(place.startsWith("대전")||place.startsWith("세종")||place.startsWith("충청남도")){
            Code = "11C20000";
        } else if(place.startsWith("충청북도")){
            Code = "11C10000";
        } else if(place.startsWith("광주")||place.startsWith("전라남도")){
            Code = "11F20000";
        } else if(place.startsWith("전라북도")){
            Code = "11F10000";
        } else if(place.startsWith("대구")||place.startsWith("경상북도")){
            Code = "11H10000";
        } else if(place.startsWith("부산")||place.startsWith("울산")||place.startsWith("경상남도")){
            Code = "11H20000";
        }  else if(place.startsWith("제주")){
            Code = "11G10000";
        }

        return Code;

    }

    private void getMidTemp(){

        //중기기온 API

    }

    private void getShorts(){

        //단기예보 API

    }

    private ScheduleList getScheduleFromPreference(){

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        //Preference에서 날씨 정보 객체 불러오기
        Gson gson = new GsonBuilder().create();
        String loaded = pref.getString("schedule","");

        ScheduleList loadedFromSP = gson.fromJson(loaded, ScheduleList.class);

        return loadedFromSP;
    }

    @Override
    public void validateSuccess(boolean isSuccess, WxResponse wxResponse) {

            Log.i(TAG, "data도착 : " + wxResponse.getBody().getItems().getItem().get(0).getRegId());
            Log.i(TAG, "data도착 : " + wxResponse.getBody().getItems().getItem().get(0).getWf4Am());

    }

    @Override
    public void validateFailure(String message) {

    }

}