package com.example.weatherkok.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherkok.R;
import com.example.weatherkok.main.MainActivity;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.utils.WxListAdapter;
import com.example.weatherkok.weather.utils.WxNowListAdapter;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class WxListActivity extends BaseActivity {
    private static final String TAG = WxListActivity.class.getSimpleName();
    RecyclerView mRvBml;
    WxListAdapter mBmlAdapter;
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ScheduleList mScheduleList;
    TextView mTvBmWxListDelete;
    TextView mTvBmWxListAdd;
    ImageView mIvBmTrash;
    boolean mTrashChecker = false;
    ArrayList<Boolean> mDelList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bookmarklist);

        initView();

        mScheduleList = getScheduleFromSp();

        makeRv(mScheduleList);

        listeners();
    }

    private void initView(){
        mRvBml = findViewById(R.id.rv_bmlist);

        mTvBmWxListAdd = findViewById(R.id.tv_bmlist_add_place);

        mTvBmWxListDelete = findViewById(R.id.tv_bmlist_deletion);

        mIvBmTrash = findViewById(R.id.iv_bml_trashcan);

        mIvBmTrash.setVisibility(View.GONE);
    }

    private void listeners(){

        mTvBmWxListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        mTvBmWxListDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIvBmTrash.setVisibility(View.VISIBLE);

                mTvBmWxListDelete.setVisibility(View.GONE);

                mTrashChecker = true;

                mBmlAdapter.notifyDataSetChanged();
            }
        });

        mIvBmTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //삭제처리

                mIvBmTrash.setVisibility(View.GONE);

                mTvBmWxListDelete.setVisibility(View.VISIBLE);

                deleteList();

                mTrashChecker = false;

                makeRv(mScheduleList);

                setScheduleDataInToSp(mScheduleList);

            }
        });

    }

    private void deleteList() {

        mDelList = mBmlAdapter.getDeleteList();

        for(int i= 0;i<mScheduleList.getScheduleArrayList().size();i++){
            if(mDelList.get(i)){
                mScheduleList.getScheduleArrayList().get(i).setYear("delete");
            }
        }

        for(int i=0;i<mScheduleList.getScheduleArrayList().size();i++) {
            if(mScheduleList.getScheduleArrayList().get(i).getYear().equals("delete")){
            mScheduleList.getScheduleArrayList().remove(i);}
        }

    }

    private ScheduleList getValueWithKeyFromSp(String key) {

        //Preference에 날씨 정보 객체 불러오기

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null일 경우 처리할것.
        String loaded = pref.getString(key, "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference에 저장된 데이터 class 형태로 불러오기 완료

        return scheduleList;

    }

    public boolean ismTrashChecker() {
        return mTrashChecker;
    }

    private void makeRv(ScheduleList scheduleList){

        mBmlAdapter = new WxListAdapter(WxListActivity.this, scheduleList);

        mRvBml.setLayoutManager(new LinearLayoutManager(this));
        mRvBml.setAdapter(mBmlAdapter);
        mBmlAdapter.notifyDataSetChanged();

    }

    private void setScheduleDataInToSp(ScheduleList scheduleList) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        //Preference에 정보 객체 저장하기
        //JSON으로 변환
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //초기화
        //editor.remove(year + month);

        editor.putString("schedule", jsonString);
        editor.commit();
        //저장완료

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
