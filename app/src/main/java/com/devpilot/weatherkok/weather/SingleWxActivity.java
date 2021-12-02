package com.devpilot.weatherkok.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.alarm.PreferenceHelper;
import com.devpilot.weatherkok.datalist.data.ScheduleData;
import com.devpilot.weatherkok.main.dialog.WeatherIconDialog;
import com.devpilot.weatherkok.src.BaseActivity;
import com.devpilot.weatherkok.weather.utils.SingleWxAdapter;
import com.devpilot.weatherkok.when.models.Schedule;
import com.devpilot.weatherkok.when.models.ScheduleList;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.devpilot.weatherkok.alarm.Constants.SHARED_PREF_AD_KEY;

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
    TextView tvBmNoList;
    RecyclerView mRvBmWxList;
    SingleWxAdapter mBmAdapter;
    Toolbar mTbWxMain;
    ActionBar mActionBar;
    TextView mTvBmForcast;
    private AdView mAdview;
    FrameLayout adContainerView;
    ImageView mIvNowInfo;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_weather);
        boolean key = PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_AD_KEY);

        Intent intent = getIntent();

        if(intent!=null){
            position = intent.getIntExtra("position",-1);
        }

        if(position!=-1){
            scheduleList = getScheduleFromSp();
            schedule = scheduleList.getScheduleArrayList().get(position);
        }

        initView();

        mTbWxMain = findViewById(R.id.tb_weather_main);
        setSupportActionBar(mTbWxMain);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);

        setNowWx();

        setListWx();

        admob(key);

        mTvBmForcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse(getString(R.string.forcast_url));
                Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(it);

            }
        });

        mIvNowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WeatherIconDialog weatherIconDialog = new WeatherIconDialog(SingleWxActivity.this);
                weatherIconDialog.show();

            }
        });

    }

    private void admob(boolean key) {

        adContainerView = findViewById(R.id.admob_container);

        if(key){

            adContainerView.setVisibility(View.INVISIBLE);

        }else{

            adContainerView.setVisibility(View.VISIBLE);

            MobileAds.initialize(this, new OnInitializationCompleteListener() { //광고 초기화
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            mAdview = new AdView(this);
            mAdview.setAdUnitId(getString(R.string.ad_mob_unit_id));
            adContainerView.addView(mAdview);
            loadBanner();

        }

    }

    private void loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        AdRequest adRequest =
                new AdRequest.Builder().build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        mAdview.setAdSize(adSize);


        // Step 5 - Start loading the ad in the background.
        mAdview.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void setListWx() {

        //어댑터랑 리스트 연결
        mRvBmWxList = (RecyclerView) findViewById(R.id.rv_bookmark_wx_list);
        tvBmNoList = findViewById(R.id.tv_bm_weather_no_list);

        if(schedule==null||schedule.getScheduleData().getFcst().getWxToday()==null) {
            tvBmNoList.setVisibility(View.VISIBLE);
            mRvBmWxList.setVisibility(View.GONE);
        } else {
            mBmAdapter = new SingleWxAdapter(schedule, this);
            tvBmNoList.setVisibility(View.GONE);
            mRvBmWxList.setVisibility(View.VISIBLE);

            mRvBmWxList.setLayoutManager(new LinearLayoutManager(this));
            mRvBmWxList.setAdapter(mBmAdapter);
        }

    }

    private void initView() {
        
        mTvNowWxLocation = findViewById(R.id.tv_now_weather_location);
        mTvNowWxCondition = findViewById(R.id.tv_now_weather_condition);
        mTvNowWxTemperature = findViewById(R.id.tv_now_weather_temperature);
        mTvNowWxTempMaxMin = findViewById(R.id.tv_now_weather_max_min);
        mTvNowWxGoToFcstWeb = findViewById(R.id.tv_now_weather_forecast);
        mLlWxPage = findViewById(R.id.ll_weather_page);
        mTbWxMain = findViewById(R.id.tb_weather_main);
        mTvBmForcast = findViewById(R.id.tv_now_weather_forecast);
        mIvNowInfo = findViewById(R.id.iv_now_info);

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

        String where = removeAdminArea(schedule.getScheduleData().getPlace());
        //장소
        mTvNowWxLocation.setText(where);
    }

    private String removeAdminArea(String location) {

        String[] splited = location.split(" ");
        String temp2="";

        if(splited[0].startsWith("경기")){
            //광주라면
            if(splited[1].startsWith("광주")) {

                if (splited.length == 2) {
                    temp2 = splited[0] + " " + splited[1];
                } else if (splited.length == 3) {
                    temp2 = splited[0] + " " + splited[1] + " " + splited[2];
                } else if (splited.length == 4) {
                    temp2 = splited[0] + " " + splited[1] + " " + splited[2] + " " + splited[3];
                } else if (splited.length == 5) {
                    temp2 = splited[0] + " " + splited[1] + " " + splited[2] + " " + splited[3] + " " + splited[4];
                } else {
                    temp2 = splited[0] + " " + splited[0] + splited[1];
                }

            }else{

                if (splited.length == 2) {
                    temp2 = splited[1];
                } else if (splited.length == 3) {
                    temp2 = splited[1] + " " + splited[2];
                } else if (splited.length == 4) {
                    temp2 = splited[1] + " " + splited[2] + " " + splited[3];
                } else if (splited.length == 5) {
                    temp2 = splited[1] + " " + splited[2] + " " + splited[3] + " " + splited[4];
                } else {
                    temp2 = splited[0] + splited[1];
                }

            }

        }else {

            if (splited.length == 2) {
                temp2 = splited[1];
            } else if (splited.length == 3) {
                temp2 = splited[1] + " " + splited[2];
            } else if (splited.length == 4) {
                temp2 = splited[1] + " " + splited[2] + " " + splited[3];
            } else if (splited.length == 5) {
                temp2 = splited[1] + " " + splited[2] + " " + splited[3] + " " + splited[4];
            } else {
                temp2 = splited[0] + splited[1];
            }

            splited[0]=setAdminString(splited[0]);

            if(splited[1].endsWith("시")) {

            }else{
                temp2 = splited[0] + " " + temp2;
            }

        }

        return temp2;

    }

    private String setAdminString(String s) {

        if(s.startsWith("서울")){
            s="서울";
        }else if(s.startsWith("경기")){
            s="경기";

        }else if(s.startsWith("인천")){
            s="인천";
        }else if(s.startsWith("강원")){
            s="강원";
        }else if(s.startsWith("충청북")){
            s="충북";
        }else if(s.startsWith("충청남")){
            s="충남";
        }else if(s.startsWith("경상북")){
            s="경북";
        }else if(s.startsWith("경상남")){
            s="경남";
        }else if(s.startsWith("전라남")){
            s="전남";
        }else if(s.startsWith("전라북")){
            s="전북";
        }else if(s.startsWith("세종")){
            s="세종";
        }else if(s.startsWith("대구")){
            s="대구";
        }else if(s.startsWith("대전")){
            s="대전";
        }else if(s.startsWith("부산")){
            s="부산";
        }else if(s.startsWith("울산")){
            s="울산";
        }else if(s.startsWith("제주")){
            s="제주도";
        }else if(s.startsWith("광주")){
            s="광주";
        }

        return s;

    }

    private void setNowWxCond(ScheduleData scheduleData, String strCurHH){
        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        String wx= "맑음";

        for (int i = 0; i < scheduleData.getFcst().getTempToday().size(); i++) {
            if (scheduleData.getFcst().getTempToday().get(i).getTimeHhmm().equals(strCurHH)) {
                mTvNowWxTemperature.setText(scheduleData.getFcst().getTempToday().get(i).getTemperature() + "\u00B0");

                mTvNowWxTempMaxMin.setText("최고 " + scheduleData.getFcst().getTempList().getItem().get(0).getTaMax0() + "\u00B0 최저 "
                        + scheduleData.getFcst().getTempList().getItem().get(0).getTaMin0() + "\u00B0");
            }
        }


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
        }else if(wx.equals("비/눈")){
            mLlWxPage.setBackground(snowRain);
        }else if(wx.contains("비")){
            mLlWxPage.setBackground(rain);
        }else if(wx.contains("눈")){
            mLlWxPage.setBackground(snow);
        }else if(wx.contains("소나기")){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.single_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_close:{
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
