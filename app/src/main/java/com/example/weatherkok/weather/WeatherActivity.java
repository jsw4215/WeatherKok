package com.example.weatherkok.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.intro.IntroActivity;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.utils.WeatherActivityAdapter;
import com.example.weatherkok.weather.utils.WxKokDataPresenter;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class WeatherActivity extends BaseActivity{
    String TAG = "WeatherActivity";
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ArrayList<String> mTargetPlaces = new ArrayList<>();
    ArrayList<String> mCodeList = new ArrayList<>();
    WxKokDataPresenter mWxKokDataPresenter;
    RelativeLayout rlBmWxCtr;
    TextView tvGotoNow;
    TextView tvBmWxDate;
    TextView tvBmWxPlace;
    TextView tvBmWxCondition;
    TextView tvBmWxTemperature;
    TextView tvBmWxTempMaxMin;
    TextView tvGoToFcstWeb;
    TextView tvBmNoList;
    ScheduleList mScheduleList;
    RecyclerView mRvBmWxList;
    WeatherActivityAdapter mWxAdapter;
    String mScheduledDate;
    ImageView mIvBmWxAdd;


    //날씨 정보를 얻는 서비스를 시작하고, 받아와서 정리된 데이터를 가져와 뿌리는 역할만 하는 곳
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_weather);

        Log.i(TAG, "weather");
        //스케쥴에 따른 날씨 정보 불러오기 및 preference에 데이터 저장
//        mWxKokDataPresenter = new WxKokDataPresenter(getBaseContext());

//        mWxKokDataPresenter.getScheduleDateWxApi();

        mScheduleList = getScheduleFromSp();
        if(mScheduleList.getScheduleArrayList().size()==0){
            Intent intent = new Intent(WeatherActivity.this, IntroActivity.class);
            intent.putExtra("from","goToWeather");
            startActivity(intent);
        }

        initCenterView();

        initView();
        //지난 스케쥴을 삭제하고, 가장 빠른일자를 골라내는 함수를 한 뒤,
        decorCenter();
        //아래의 나머지 스케쥴 리스트를 연동
        decorBottom();

        decorTop();

        mIvBmWxAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WeatherActivity.this, WxListActivity.class);
                startActivity(intent);
            }
        });

        tvGotoNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WeatherActivity.this, IntroActivity.class);
                intent.putExtra("from","goToNow");
                startActivity(intent);

            }
        });

    }

    private void decorBottom() {

        tvBmNoList = findViewById(R.id.tv_bm_weather_no_list);
        mRvBmWxList = (RecyclerView) findViewById(R.id.rv_bookmark_wx_list);

        if(mScheduleList.getScheduleArrayList().size()==0) {
            tvBmNoList.setVisibility(View.VISIBLE);
            mRvBmWxList.setVisibility(View.GONE);
        } else {
            mWxAdapter = new WeatherActivityAdapter(this, mScheduleList);
            tvBmNoList.setVisibility(View.GONE);
            mRvBmWxList.setVisibility(View.VISIBLE);

            mRvBmWxList.setLayoutManager(new LinearLayoutManager(this));
            mRvBmWxList.setAdapter(mWxAdapter);
        }

    }

    private void initCenterView() {
        //스케쥴에서 가장 일찍 다가올 스케쥴을 여기에 덮는다.

        rlBmWxCtr = findViewById(R.id.rl_bm_weather_center);
        tvBmWxDate = findViewById(R.id.tv_bm_weather_dates);
        tvBmWxPlace = findViewById(R.id.tv_bm_weather_location);
        tvBmWxCondition = findViewById(R.id.tv_bm_weather_condition);
        tvBmWxTemperature = findViewById(R.id.tv_bm_weather_temperature);
        tvBmWxTempMaxMin = findViewById(R.id.tv_bm_weather_max_min);
        tvGotoNow = findViewById(R.id.tv_now_weather_go);
        tvGoToFcstWeb = findViewById(R.id.tv_bm_weather_forecast);

    }

    private void decorTop(){
        mIvBmWxAdd = findViewById(R.id.iv_bm_weather_add);

        mIvBmWxAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, WxListActivity.class);
                startActivity(intent);
            }
        });

    }

    private void decorCenter(){

        //가장 빠른일자의 스케쥴
        Schedule schedule = getFirstDateSchedule(mScheduleList);

        ScheduleData scheduleData = new ScheduleData(schedule.getScheduleData().getScheduledDate(),
                schedule.getScheduleData().getPlace(), schedule.getScheduleData().getFcst());

        String date = schedule.getScheduleData().getScheduledDate();
        //스케쥴 날짜 세팅
        setmScheduledDate(date);

        String day = "";

        tvBmWxDate.setText(schedule.getYear() + "/" + schedule.getMonth() + "/" + schedule.getDate() + " (" + scheduleData.getDay() + ")");

        tvBmWxPlace.setText(schedule.getWhere());

        boolean checker = checkAMPM();

        int diffDays = (int) howFarFromToday(date);
        //현재시간 출력하여 오전 오후 나누기

        int currentHour = getHourHH();
        //해당 날짜의 날씨 적용 및 오늘이라면 현재 시간 비교
        findScheduleDateWxData(scheduleData, checker, diffDays, currentHour);

        //현재온도 적용
        //단기예보 response에서 현재 시간 비교해서 TMP 변수 클래스에 만들어서 넣어두기
        //matchingCurTimeGetTemp(currentHour, schedule);
        //첫날 적용 후 삭제
        mScheduleList.getScheduleArrayList().remove(0);
    }

    private long howFarFromToday(String dateCompared) {

        Calendar getToday = Calendar.getInstance();
        getToday.setTime(new Date()); //금일 날짜

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyyMMdd").parse(dateCompared);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i(TAG, "비교 날짜가 옳지 않습니다.");
        }
        Calendar cmpDate = Calendar.getInstance();
        cmpDate.setTime(date); //특정 일자

        long diffSec = (cmpDate.getTimeInMillis() - getToday.getTimeInMillis()) / 1000;
        long diffDays = diffSec / (24 * 60 * 60); //일자수 차이

        return diffDays;

    }

    private void findScheduleDateWxData(ScheduleData scheduleData, boolean checker, int diffDays, int currHh) {

        String strCurHH = "";

        if(currHh<10){
            strCurHH = "0" + String.valueOf(currHh);
        }

        strCurHH = strCurHH + "00";

        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        Log.i(TAG, "오늘과 몇일 차이인가? : " + diffDays);
        if(diffDays==0){
            //오늘이면 시간비교를 해서 해당 시간의 날씨예보를 담을것

                setNowWxCond(scheduleData, strCurHH);

        }else if(diffDays==1){
            int max = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax1();
            int min = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin1();
            tvBmWxTemperature.setText((min+max)/2 + "\u00B0");

            tvBmWxTempMaxMin.setText("최고 " + max + "\u00B0 최저 "
                    + min + "\u00B0");
            if(!checker){
                //만약 비가 온다면, 비, 소나기,눈, 비/눈
                //비가 안오면,
                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }
        }else if(diffDays==2){
            int max = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax2();
            int min = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin2();
            tvBmWxTemperature.setText((min+max)/2 + "\u00B0");

            tvBmWxTempMaxMin.setText("최고 " + max + "\u00B0 최저 "
                    + min + "\u00B0");
            if(!checker){
                //만약 비가 온다면, 비, 소나기,눈, 비/눈
                //비가 안오면,
                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf2Am());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }
        }
        else if(diffDays==3){
            int max = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax3();
            int min = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin3();
            tvBmWxTemperature.setText((min+max)/2 + "\u00B0");

            tvBmWxTempMaxMin.setText("최고 " + max + "\u00B0 최저 "
                    + min + "\u00B0");
            if(!checker){
                //만약 비가 온다면, 비, 소나기,눈, 비/눈
                //비가 안오면,
                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Am()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf3Am());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf3Pm());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }
        }
        else if(diffDays==4){
            int max = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax4();
            int min = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin4();
            tvBmWxTemperature.setText((min+max)/2 + "\u00B0");

            tvBmWxTempMaxMin.setText("최고 " + max + "\u00B0 최저 "
                    + min + "\u00B0");
            if(!checker){
                //만약 비가 온다면, 비, 소나기,눈, 비/눈
                //비가 안오면,
                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Am()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf4Am());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf4Pm());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }
        }
        else if(diffDays==5){
            int max = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax5();
            int min = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin5();
            tvBmWxTemperature.setText((min+max)/2 + "\u00B0");

            tvBmWxTempMaxMin.setText("최고 " + max + "\u00B0 최저 "
                    + min + "\u00B0");
            if(!checker){
                //만약 비가 온다면, 비, 소나기,눈, 비/눈
                //비가 안오면,
                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Am()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf5Am());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf5Pm());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }
        }
        else if(diffDays==6){
            int max = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax6();
            int min = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin6();
            tvBmWxTemperature.setText((min+max)/2 + "\u00B0");

            tvBmWxTempMaxMin.setText("최고 " + max + "\u00B0 최저 "
                    + min + "\u00B0");
            if(!checker){
                //만약 비가 온다면, 비, 소나기,눈, 비/눈
                //비가 안오면,
                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Am()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf6Am());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf6Pm());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }
        }
        else if(diffDays==7){
            int max = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax7();
            int min = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin7();
            tvBmWxTemperature.setText((min+max)/2 + "\u00B0");

            tvBmWxTempMaxMin.setText("최고 " + max + "\u00B0 최저 "
                    + min + "\u00B0");
            if(!checker){
                //만약 비가 온다면, 비, 소나기,눈, 비/눈
                //비가 안오면,
                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Am()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf7Am());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf7Pm());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }

            }
        }
        else if(diffDays==8){
            int max = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax8();
            int min = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin8();
            tvBmWxTemperature.setText((min+max)/2 + "\u00B0");

            tvBmWxTempMaxMin.setText("최고 " + max + "\u00B0 최저 "
                    + min + "\u00B0");
                //만약 비가 온다면, 비, 소나기,눈, 비/눈
                //비가 안오면,
                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt8()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf8());
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt8()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt8()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt8()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt8()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                }
        }
        else if(diffDays==9){
            int max = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax9();
            int min = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin9();
            tvBmWxTemperature.setText((min+max)/2 + "\u00B0");

            tvBmWxTempMaxMin.setText("최고 " + max + "\u00B0 최저 "
                    + min + "\u00B0");
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt9()==0){
                tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf9());
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt9()==1){
                //비
                tvBmWxCondition.setText(R.string.rain);
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt9()==2){
                //비눈
                tvBmWxCondition.setText(R.string.rain_snow);
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt9()==3){
                //눈
                tvBmWxCondition.setText(R.string.snow);
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt9()==4){
                //소나기
                tvBmWxCondition.setText(R.string.shower);
            }
        }
        else if(diffDays==10){
            int max = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax10();
            int min = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin10();
            tvBmWxTemperature.setText((min+max)/2 + "\u00B0");

            tvBmWxTempMaxMin.setText("최고 " + max + "\u00B0 최저 "
                    + min + "\u00B0");
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt10()==0){
                tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf10());
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt10()==1){
                //비
                tvBmWxCondition.setText(R.string.rain);
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt10()==2){
                //비눈
                tvBmWxCondition.setText(R.string.rain_snow);
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt10()==3){
                //눈
                tvBmWxCondition.setText(R.string.snow);
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt10()==4){
                //소나기
                tvBmWxCondition.setText(R.string.shower);
            }
        }

    }

    private void setNowWxCond(ScheduleData scheduleData,String strCurHH){
        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)

        for (int i = 0; i < scheduleData.getFcst().getTempToday().size(); i++) {
            if (scheduleData.getFcst().getTempToday().get(i).getTimeHhmm().equals(strCurHH)) {
                tvBmWxTemperature.setText(scheduleData.getFcst().getTempToday().get(i) + "\u00B0");

                tvBmWxTempMaxMin.setText("최고 " + scheduleData.getFcst().getTempList().getItem().get(0).getTaMax0() + "\u00B0 최저 "
                        + scheduleData.getFcst().getTempList().getItem().get(0).getTaMin0() + "\u00B0");
            }
        }


        for(int i=0;i<scheduleData.getFcst().getWxToday().size();i++){

            if(scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)){

                if(scheduleData.getFcst().getWxToday().get(i).getRainType()==0) {

                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        tvBmWxCondition.setText("맑음");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        tvBmWxCondition.setText("구름많음");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        tvBmWxCondition.setText("흐림");
                    }


                }else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        tvBmWxCondition.setText(R.string.rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        tvBmWxCondition.setText(R.string.rain_snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        tvBmWxCondition.setText(R.string.snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        tvBmWxCondition.setText(R.string.shower);
                    }

                }

                break;
            }

        }
    }

    private int getHourHH() {

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        return inthour;

    }

    private boolean checkAMPM() {

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        //현재시간 출력하여 오전 오후 나누기
        if(inthour<12||inthour==24){
            //오전
            return false;
        }else {
            //오후
            return true;
        }

    }

    private void matchingCurTimeGetTemp(int currentHour, Schedule schedule) {

        String strCurHH = "";

        if(currentHour<10){
            strCurHH = "0" + String.valueOf(currentHour);
        }

        strCurHH = strCurHH + "00";

        for(int i = 0;i<schedule.getScheduleData().getFcst().getTempToday().size();i++){
            if((schedule.getScheduleData().getFcst().getTempToday().get(i)).equals(strCurHH)){
                tvBmWxTemperature.setText(schedule.getScheduleData().getFcst().getTempToday().get(i) + "\u00B0");
            }
        }

    }

    private Date getToday(){
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private Schedule getFirstDateSchedule(ScheduleList scheduleList) {

        ArrayList<Integer> temp = new ArrayList<>();
        //지난 스케쥴 제거
        mScheduleList = removePastSchedule(scheduleList);



        for(int i =0;i<scheduleList.getScheduleArrayList().size();i++) {
            if(!TextUtils.isEmpty(scheduleList.getScheduleArrayList().get(i).getScheduleData().getScheduledDate())){
            temp.add(Integer.valueOf(scheduleList.getScheduleArrayList().get(i).getScheduleData().getScheduledDate()));}
        }

        int minDate = 0;
        int maxIndex = 0;
        //가장 최근 날짜
        if(temp!=null&&temp.size()!=0) {
            minDate = Collections.min(temp);

            maxIndex = Collections.max(temp);
        }
        minDate = getIndexFirestdateSchedule(minDate, temp);

        return scheduleList.getScheduleArrayList().get(minDate);

    }

    private int getIndexFirestdateSchedule(int minDate, ArrayList<Integer> temp) {

        for(int i=0;i<temp.size();i++){
            if((temp.get(i)).equals(minDate)) return i;
        }

        return 0;
    }

    private ScheduleList removePastSchedule(ScheduleList scheduleList) {

        String today = getFutureDay("yyyyMMdd", 0);
        int intToday = Integer.parseInt(today);
        //테스트용 더미

        for(int i =0;i<scheduleList.getScheduleArrayList().size();i++) {
            if(!TextUtils.isEmpty(scheduleList.getScheduleArrayList().get(i).getScheduleData().getScheduledDate())
                    &&intToday > Integer.valueOf(scheduleList.getScheduleArrayList().get(i).getScheduleData().getScheduledDate())){
                scheduleList.getScheduleArrayList().remove(i);
            }
        }

        return scheduleList;

    }

    public static String getFutureDay(String pattern, int gap) {
        DateFormat dtf = new SimpleDateFormat(pattern);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, gap);
        return dtf.format(cal.getTime());
    }

    private void initView() {
        //나머지 스케쥴이 추가될 리스트







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

    public String getmScheduledDate() {
        return mScheduledDate;
    }

    public void setmScheduledDate(String mScheduledDate) {
        this.mScheduledDate = mScheduledDate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        decorBottom();
    }
}