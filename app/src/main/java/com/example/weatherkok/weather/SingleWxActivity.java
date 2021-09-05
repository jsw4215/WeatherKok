package com.example.weatherkok.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SingleWxActivity extends BaseActivity {
    private static final String TAG = SingleWxActivity.class.getSimpleName();
    int position;
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ScheduleList scheduleList;
    Schedule schedule;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if(intent!=null){
            position = intent.getIntExtra("position",-1);
        }

        if(position!=-1){
            scheduleList = getScheduleFromSp();
            schedule = scheduleList.getScheduleArrayList().get(position);
        }

        

    }




    private ScheduleList getScheduleFromSp() {

        //Preference에 날씨 정보 객체 불러오기

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null일 경우 처리할것.
        String loaded = pref.getString("bookMark", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference에 저장된 데이터 class 형태로 불러오기 완료

        return scheduleList;

    }

}
