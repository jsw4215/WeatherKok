package com.example.weatherkok.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.models.shortsExpectation.todayWx;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SingleWxActivity extends BaseActivity {
    private static final String TAG = SingleWxActivity.class.getSimpleName();
    int position;
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ScheduleList scheduleList;
    Schedule schedule;
    TextView mTvNowWxLocation;
    TextView mTvNowWxCondition;
    TextView mTvNowWxTemperature;
    TextView mTvNowWxTempMaxMin;
    TextView mTvNowWxGoToFcstWeb;
    LinearLayout mLlWxPage;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_weather);
        
        Intent intent = getIntent();

        if(intent!=null){
            position = intent.getIntExtra("position",-1);
        }

        if(position!=-1){
            scheduleList = getScheduleFromSp();
            schedule = scheduleList.getScheduleArrayList().get(position);
        }
        
        
        initView();

        setNowWx();

        setListWx();
        
        

    }

    private void setListWx() {

        //어댑터랑 리스트 연결



    }

    private void initView() {
        
        mTvNowWxLocation = findViewById(R.id.tv_now_weather_location);
        mTvNowWxCondition = findViewById(R.id.tv_now_weather_condition);
        mTvNowWxTemperature = findViewById(R.id.tv_now_weather_temperature);
        mTvNowWxTempMaxMin = findViewById(R.id.tv_now_weather_max_min);
        mTvNowWxGoToFcstWeb = findViewById(R.id.tv_now_weather_forecast);
        mLlWxPage = findViewById(R.id.ll_weather_page);


    }

    private void setNowWx() {
        
        String time ="";

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentHour = curHour.format(dateToday);

        currentHour=makingStrHour(currentHour);

        setNowWxCond(schedule.getScheduleData(), currentHour);
    
    }

    private void setNowWxCond(ScheduleData scheduleData, String strCurHH){
        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        String wx= "맑음";

//        for (int i = 0; i < scheduleData.getFcst().getTempToday().size(); i++) {
//            if (scheduleData.getFcst().getTempToday().get(i).getTimeHhmm().equals(strCurHH)) {
//                tvBmWxTemperature.setText(scheduleData.getFcst().getTempToday().get(i).getTemperature() + "\u00B0");
//
//                tvBmWxTempMaxMin.setText("최고 " + scheduleData.getFcst().getTempList().getItem().get(0).getTaMax0() + "\u00B0 최저 "
//                        + scheduleData.getFcst().getTempList().getItem().get(0).getTaMin0() + "\u00B0");
//            }
//        }


        for(int i=0;i<scheduleData.getFcst().getWxToday().size();i++){

            if(scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)){

                if(scheduleData.getFcst().getWxToday().get(i).getRainType()==0) {

                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        mTvNowWxCondition.setText("맑음");
                        setBackgroundWxImage("맑음");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        mTvNowWxCondition.setText("구름많음");
                        setBackgroundWxImage("구름많음");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        mTvNowWxCondition.setText("흐림");
                        setBackgroundWxImage("흐림");
                    }


                }else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        mTvNowWxCondition.setText(R.string.rain);
                        setBackgroundWxImage("비");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        mTvNowWxCondition.setText(R.string.rain_snow);
                        setBackgroundWxImage("비/눈");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        mTvNowWxCondition.setText(R.string.snow);
                        setBackgroundWxImage("눈");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        mTvNowWxCondition.setText(R.string.shower);
                        setBackgroundWxImage("눈");
                    }

                }

                break;
            }

        }
    }

    private void setBackgroundWxImage(String wx) {

        Drawable sunny = getResources().getDrawable(R.drawable.bg_sunny);
        Drawable cloudy = getResources().getDrawable(R.drawable.bg_cloudy);
        Drawable gray = getResources().getDrawable(R.drawable.bg_gray);
        Drawable rain = getResources().getDrawable(R.drawable.bg_rain);
        Drawable snowRain = getResources().getDrawable(R.drawable.bg_snow_rain);
        Drawable snow = getResources().getDrawable(R.drawable.bg_snow);
        Drawable shower = getResources().getDrawable(R.drawable.bg_shower);

        if(wx.contains("맑음")){
            mLlWxPage.setBackground(sunny);
        }else if(wx.contains("구름")){
            mLlWxPage.setBackground(cloudy);
        }else if(wx.contains("흐림")){
            mLlWxPage.setBackground(gray);
        }else if(wx.equals("비")){
            mLlWxPage.setBackground(rain);
        }else if(wx.equals("비/눈")){
            mLlWxPage.setBackground(snowRain);
        }else if(wx.equals("눈")){
            mLlWxPage.setBackground(snow);
        }else if(wx.equals("소나기")){
            mLlWxPage.setBackground(shower);
        }


    }


    private Date getToday(){
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private String makingStrHour(String currentHh) {
        int currHh = Integer.parseInt(currentHh);


        if(currHh<10){
            currentHh = "0" + String.valueOf(currHh);
        }

        currentHh = currentHh + "00";

        return currentHh;

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
