package com.example.weatherkok.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.intro.IntroActivity;
import com.example.weatherkok.weather.WeatherActivity;
import com.example.weatherkok.weather.interfaces.WeatherContract;
import com.example.weatherkok.weather.models.midTemp.MidTempResponse;
import com.example.weatherkok.weather.models.midWx.WxResponse;
import com.example.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.example.weatherkok.weather.models.xy;
import com.example.weatherkok.weather.utils.WxKokDataPresenter;
import com.example.weatherkok.when.CalendarActivity;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.where.WhereActivity;
import com.example.weatherkok.who.kakao.kotlin.WhoActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Preference를 불러오기 위한 키
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    Context mContext;
    String TAG = "MainActivity";
    LinearLayout mLlMainWhere;
    LinearLayout mLlMainWhen;
    LinearLayout mLlMainWho;
    TextView mTvMainWhere;
    TextView mTvMainWhen;
    TextView mTvMainWho;
    TextView mTvMainStart;
    String where = "temp_where";
    String when = "temp_when";
    String who = "temp_who";
    String whoMain;
    String whenMain;
    String whereMain;
    WxKokDataPresenter mWxKokDataPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getBaseContext();

        initView();
        //preference를 체크하여 선택한 데이터가 있으면 해당 데이터로 띄운다.
        checkPreference();



        //preference 연동
//첫번째 매개변수(PREFERENCE) : 저장/불러오기 하기 위한 key이다.
//이 고유키로 앱의 할당된 저장소(data/data/[패키지 이름]/shared_prefs) 에 "com.studio572.samplesharepreference.xml" 로 저장된다.
//이 때 xml 파일명은 사용자 정의가 가능하다.
//
//> 두번째 매개변수(MODE_PRIVATE) : 프리퍼런스의 저장 모드를 정의한다.
//        [MODE_PRIVATE : 이 앱안에서 데이터 공유]
//[MODE_WORLD_READABLE : 다른 앱과 데이터 읽기 공유]
//[MODE_WORLD_WRITEABLE : 다른 앱과 데이터 쓰기 공유]
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

//불러오기
// key에 해당한 value를 불러온다.
// 두번째 매개변수는 , key에 해당하는 value값이 없을 때에는 이 값으로 대체한다.
        whereMain = pref.getString(where, "");
        whenMain = pref.getString(when,"");
        whoMain = pref.getString(who,"");


        //클릭하면 화면 이동
        mLlMainWhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), WhereActivity.class);
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        mTvMainWhen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CalendarActivity.class);
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        mTvMainWho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whoMain="더미";
                Intent intent = new Intent(getBaseContext(), WhoActivity.class);
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        mTvMainStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //데이터 저장하기 - 스케쥴 데이터에 저장(schedule + 날짜 데이터에 저장) - scheduleList화 시킨 jsonarray에 추가
                if(whereMain!=""&&whenMain!=""&&whoMain!=null) {

                    setScheduleInPreference(whereMain, whenMain, whoMain);
                    resetSP();
                    Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                    intent.putExtra("from", "goToWeather");
                    startActivity(intent);
                    finish();

                }else {
                    //3개가 다 입력되어있지 않으면, 시작 불가능 다이얼로그

                }
            }
        });

    }

    private void resetSP(){
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(where, "");
        editor.putString(when, "");
        editor.putString(who, "");

        editor.apply();
    }

    private void setScheduleInPreference(String where, String when, String who) {
        Schedule schedule = new Schedule();
        ScheduleList scheduleList = new ScheduleList();
        ScheduleData scheduleData = new ScheduleData();
        schedule.getWho().add(who);
        schedule.setWhere(where);

        String[] splited = new String[3];
        splited = when.split("/");
        scheduleData.setScheduledDate(splited[0]+splited[1]+splited[2]);
        schedule.setYear(splited[0]);
        schedule.setMonth(splited[1]);
        schedule.setDate(splited[2]);
        schedule.setScheduleData(scheduleData);
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //Preference에서 날씨 정보 객체 불러오기
        Gson gson = new GsonBuilder().create();
        String loaded = pref.getString("schedule","");

        ScheduleList loadedFromSP = gson.fromJson(loaded,ScheduleList.class);
        ArrayList<Schedule> temp = new ArrayList<>();
        if(loadedFromSP.getScheduleArrayList()==null){
            loadedFromSP.setScheduleArrayList(temp);
        }
        loadedFromSP.getScheduleArrayList().add(schedule);

        setScheduleDataInToSp(loadedFromSP);
    }

    private void setScheduleDataInToSp(ScheduleList scheduleList) {

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
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

    private void checkPreference() {

        //Intent 확인해서 없으면(앱을 켜서 처음 들어온 화면, 다른 화면에서 돌아온 화면이 아니라),
        Intent intent = getIntent();
        boolean check = intent.getBooleanExtra("fromApp",false);
        //intent.putExtra("fromApp",true);

        if(check) {
            Log.i(TAG, "intent : " + intent);
            SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            //언제
            String result_where = pref.getString(where, "");
            if (!result_where.equals("")) {
                mTvMainWhere.setText(result_where);
            }
            //어디로
            String result_when = pref.getString(when, "");
            if (!result_when.equals("")) {
                mTvMainWhen.setText(result_when);
            }
            //누구랑
            String result_who = pref.getString(who, "");
            if (!result_who.equals("")) {
                mTvMainWho.setText(result_who);
            }
        }
    }

    private void initView() {

        mLlMainWhere = findViewById(R.id.ll_main_where);
        mLlMainWhen = findViewById(R.id.ll_main_when);
        mLlMainWho = findViewById(R.id.ll_main_who);
        mTvMainWhere = findViewById(R.id.tv_main_where);
        mTvMainWhen = findViewById(R.id.tv_main_when);
        mTvMainWho = findViewById(R.id.tv_main_who);
        mTvMainStart = findViewById(R.id.tv_main_start);

    }


//    private void saveTheShedule(String scheduledDate){
//        //preference 동작 준비
//        SharedPreferences sp = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//
//
//
//        //Preference에 스케쥴 정보 객체 저장하기
//        Gson gson = new GsonBuilder().create();
//        //JSON으로 변환
//        String jsonString = gson.toJson(scheduleData, ScheduleData.class);
//        Log.i("jsonString : ",jsonString);
//        SharedPreferences sp = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("schedule",jsonString);
//        editor.commit();
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(where,"");
        editor.putString(when,"");
        editor.putString(who,"");

    }

}