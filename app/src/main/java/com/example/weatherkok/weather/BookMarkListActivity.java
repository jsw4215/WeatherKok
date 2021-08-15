package com.example.weatherkok.weather;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherkok.R;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.utils.BookMarkAdapter;
import com.example.weatherkok.weather.utils.BookMarkListAdapter;
import com.example.weatherkok.when.models.ScheduleList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BookMarkListActivity extends BaseActivity {
    private static final String TAG = BookMarkListActivity.class.getSimpleName();
    RecyclerView mRvBml;
    BookMarkListAdapter mBmlAdapter;
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ScheduleList mScheduleList;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bookmarklist);

        initView();

        mScheduleList = getScheduleFromSp();

        makeRv(mScheduleList);
    }

    private void initView(){
        mRvBml = findViewById(R.id.rv_bmlist);
    }

    private void makeRv(ScheduleList scheduleList){

        mBmlAdapter = new BookMarkListAdapter(getBaseContext(), scheduleList);

        mRvBml.setLayoutManager(new LinearLayoutManager(this));
        mRvBml.setAdapter(mBmlAdapter);


    }

    private ScheduleList getScheduleFromSp() {

        //Preference에 날씨 정보 객체 불러오기

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null일 경우 처리할것.
        String loaded = pref.getString("schedule", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference에 저장된 데이터 class 형태로 불러오기 완료

        return scheduleList;

    }


}
