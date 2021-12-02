package com.devpilot.weatherkok.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.alarm.NotificationHelper;
import com.devpilot.weatherkok.alarm.PreferenceHelper;
import com.devpilot.weatherkok.datalist.data.ScheduleData;
import com.devpilot.weatherkok.intro.IntroActivity;
import com.devpilot.weatherkok.main.dialog.WeatherIconDialog;
import com.devpilot.weatherkok.src.BaseActivity;
import com.devpilot.weatherkok.weather.utils.SingleWxAdapter;
import com.devpilot.weatherkok.weather.utils.WxKokDataPresenter;
import com.devpilot.weatherkok.when.models.Schedule;
import com.devpilot.weatherkok.when.models.ScheduleList;
import com.devpilot.weatherkok.where.utils.GpsTracker;
import com.devpilot.weatherkok.who.kakao.kotlin.WhoActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.devpilot.weatherkok.alarm.Constants.SHARED_PREF_AD_KEY;
import static com.devpilot.weatherkok.alarm.Constants.SHARED_PREF_NOTIFICATION_KEY;

public class NowWxActivity extends BaseActivity {
    private static final String TAG = NowWxActivity.class.getSimpleName();
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
    ScheduleList mScheduleList;
    RecyclerView mRvBmWxList;
    SingleWxAdapter mBmAdapter;
    String mScheduledDate;
    GpsTracker mGpsTracker;
    public static Context mNowWxContext;
    ImageView mIvBmWxAdd;
    TextView tvBmNoList;
    DrawerLayout mDrawerLayout;
    ImageView mIvBmMenu;
    NavigationView navigationView;
    Toolbar mTbWxMain;
    ActionBar mActionBar;
    Switch switchActivateNotify;
    MenuItem mAlarm;
    LinearLayout mLlWxPage;
    ImageView mIvNowInfo;
    ScheduleList scheduleList = new ScheduleList();
    TextView mTvBmForcast;
    private AdView mAdview;
    FrameLayout adContainerView;

    //날씨 정보를 얻는 서비스를 시작하고, 받아와서 정리된 데이터를 가져와 뿌리는 역할만 하는 곳
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_now_weather);
        mNowWxContext = getBaseContext();

        boolean key = PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_AD_KEY);

        Log.i(TAG, "weather");
        //스케쥴에 따른 날씨 정보 불러오기 및 preference에 데이터 저장

        initCenterView();

        initView();

        decorCenter();

        decorBottom();

        decorTop();

        admob(key);

        //툴바
        mTbWxMain = findViewById(R.id.tb_weather_main);
        setSupportActionBar(mTbWxMain);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24);

        //drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_weather_main);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        initSwitchLayout(WorkManager.getInstance(getApplicationContext()));

//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                item.setChecked(true);
//                switch (item.getItemId())
//                {
//                    case R.id.menu_alarm:
//
//                }
//
//                return true;
//            }
//        });


        tvGotoNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NowWxActivity.this, IntroActivity.class);
                intent.putExtra("from","goToWeather");
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                finish();
            }
        });

        mIvNowInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WeatherIconDialog weatherIconDialog = new WeatherIconDialog(NowWxActivity.this);
                weatherIconDialog.show();

            }
        });

        mTvBmForcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse(getString(R.string.forcast_url));
                Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(it);

            }
        });

    }

    // 푸시알림 설정
    private void initSwitchLayout(final WorkManager workManager) {

        navigationView.getMenu().findItem(R.id.menu_alarm).setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.menu_alarm).getActionView()).setChecked(PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_NOTIFICATION_KEY));


        ((Switch) navigationView.getMenu().findItem(R.id.menu_alarm).getActionView())
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(isChecked) {
                        Toast.makeText(NowWxActivity.this, "Checked", Toast.LENGTH_SHORT).show();

                        //알람 등록
                        boolean isChannelCreated = NotificationHelper.isNotificationChannelCreated(getApplicationContext());
                        if (isChannelCreated) {
                            PreferenceHelper.setBoolean(getApplicationContext(), SHARED_PREF_NOTIFICATION_KEY, true);
                            NotificationHelper.setScheduledNotification(workManager);
                        } else {
                            NotificationHelper.createNotificationChannel(getApplicationContext());
                        }
                    } else {
                        Toast.makeText(NowWxActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                        //알람 해제

                        PreferenceHelper.setBoolean(getApplicationContext(), SHARED_PREF_NOTIFICATION_KEY, false);
                        workManager.cancelAllWork();


                    }
                });

//        navigationView.getMenu().findItem(R.id.menu_ad_delete).setActionView(new Switch(this));
//        ((Switch) navigationView.getMenu().findItem(R.id.menu_ad_delete).getActionView()).setChecked(PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_AD_KEY));
//
//        ((Switch) navigationView.getMenu().findItem(R.id.menu_ad_delete).getActionView())
//                .setOnCheckedChangeListener((buttonView, isChecked) -> {
//                    if(isChecked) {
//                        Toast.makeText(NowWxActivity.this, "광고 제거", Toast.LENGTH_SHORT).show();
//                        //광고 안보임
//                        PreferenceHelper.setBoolean(getApplicationContext(), SHARED_PREF_AD_KEY, true);
//                        admob(true);
//                    } else {
//                        Toast.makeText(NowWxActivity.this, "광고 활성화", Toast.LENGTH_SHORT).show();
//                        //광고 보임
//
//                        PreferenceHelper.setBoolean(getApplicationContext(), SHARED_PREF_AD_KEY, false);
//                        admob(false);
//                    }
//                });


//        navigationView.getMenu().findItem(R.id.menu_share).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                Intent share = new Intent(getBaseContext(), WhoActivity.class);
//                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(share);
//
//                return false;
//            }
//        });

//        navigationView.getMenu().findItem(R.id.menu_email).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                Intent email = new Intent(Intent.ACTION_SEND);
//                email.setType("plain/text");
//                String[] address = {"weatherkok@gmail.com"};
//                email.putExtra(Intent.EXTRA_EMAIL, address);
//                email.putExtra(Intent.EXTRA_SUBJECT, "(안드로이드) 날씨콕 - 문의하기");
//                email.putExtra(Intent.EXTRA_TEXT,"하고 싶은말 : ");
//                startActivity(email);
//
//                return true;
//            }
//        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.toolbar_list:{
                Intent intent = new Intent(NowWxActivity.this, WxNowListActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void decorTop(){


    }

    private void decorBottom() {

        setListWx(scheduleList.getScheduleArrayList().get(0));

//        mScheduleList = getBookMarkListFromSp();
//
//        mRvBmWxList = (RecyclerView) findViewById(R.id.rv_bookmark_wx_list);
//        tvBmNoList = findViewById(R.id.tv_bm_weather_no_list);
//        mIvNowInfo = findViewById(R.id.iv_now_info);
//
//        if(mScheduleList==null||mScheduleList.getScheduleArrayList()==null||mScheduleList.getScheduleArrayList().size()==0) {
//            tvBmNoList.setVisibility(View.VISIBLE);
//            mRvBmWxList.setVisibility(View.GONE);
//        } else {
//            mBmAdapter = new NowWxActivityAdapter(this, mScheduleList);
//            tvBmNoList.setVisibility(View.GONE);
//            mRvBmWxList.setVisibility(View.VISIBLE);
//
//            mRvBmWxList.setLayoutManager(new LinearLayoutManager(this));
//            mRvBmWxList.setAdapter(mBmAdapter);
//        }

    }

    private void setListWx(Schedule schedule) {

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
            //Rv높이 설정


            mRvBmWxList.setLayoutManager(new LinearLayoutManager(this));
            mRvBmWxList.setAdapter(mBmAdapter);
        }

    }

    private ScheduleList getBookMarkListFromSp() {

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

    private void initCenterView() {
        //스케쥴에서 가장 일찍 다가올 스케쥴을 여기에 덮는다.

        rlBmWxCtr = findViewById(R.id.rl_now_weather_center);
        tvBmWxPlace = findViewById(R.id.tv_now_weather_location);
        tvBmWxCondition = findViewById(R.id.tv_now_weather_condition);
        tvBmWxTemperature = findViewById(R.id.tv_now_weather_temperature);
        tvBmWxTempMaxMin = findViewById(R.id.tv_now_weather_max_min);
        tvGotoNow = findViewById(R.id.tv_bm_weather_go);
        tvGoToFcstWeb = findViewById(R.id.tv_now_weather_forecast);
        mLlWxPage = findViewById(R.id.ll_weather_page);
    }

    public void decorCenter(){

        String today =getFutureDay("yyyyMMdd",0);

        //tvBmWxDate.setText(today + " (" + getDateDay(today) + ")");

        //tvBmWxPlace.setText(getGpsPosition());

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentHour = curHour.format(dateToday);

        scheduleList = getCurPlaceWxFromSp();

        String where = scheduleList.getScheduleArrayList().get(0).getScheduleData().getPlace();

        where = removeAdminArea(where);

        tvBmWxPlace.setText(where);

        //현재위치의 날씨 정보 가져와서 preference에 currentPlaceWx
        ScheduleData scheduleData = scheduleList.getScheduleArrayList().get(0).getScheduleData();

        currentHour=makingStrHour(currentHour);

        setNowWxCond(scheduleData, currentHour);

        //현재시간 출력하여 오전 오후 나누기

        //현재온도 적용
        //단기예보 response에서 현재 시간 비교해서 TMP 변수 클래스에 만들어서 넣어두기

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

    private String makingStrHour(String currentHh) {
        int currHh = Integer.parseInt(currentHh);


        if(currHh<10){
            currentHh = "0" + String.valueOf(currHh);
        }

        currentHh = currentHh + "00";

        return currentHh;

    }

    private void clearCurPlaceWxInSp() {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();
        ScheduleList scheduleList = new ScheduleList();
        //Preference에 정보 객체 저장하기
        //JSON으로 변환
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //초기화
        //editor.remove(year + month);

        editor.putString("currentPlace", jsonString);
        editor.commit();
        //저장완료

    }

    private void setNowWxCond(ScheduleData scheduleData,String strCurHH){
        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        String wx= "맑음";

        for (int i = 0; i < scheduleData.getFcst().getTempToday().size(); i++) {
            if (scheduleData.getFcst().getTempToday().get(i).getTimeHhmm().equals(strCurHH)) {
                tvBmWxTemperature.setText(scheduleData.getFcst().getTempToday().get(i).getTemperature() + "\u00B0");

                tvBmWxTempMaxMin.setText("최고 " + scheduleData.getFcst().getTempList().getItem().get(0).getTaMax0() + "\u00B0 최저 "
                        + scheduleData.getFcst().getTempList().getItem().get(0).getTaMin0() + "\u00B0");
            }
        }


        for(int i=0;i<scheduleData.getFcst().getWxToday().size();i++){

            if(scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)){

                if(scheduleData.getFcst().getWxToday().get(i).getRainType()==0) {

                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        tvBmWxCondition.setText("맑음");
                        setBackgroundWxImage("맑음");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        tvBmWxCondition.setText("구름많음");
                        setBackgroundWxImage("구름많음");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        tvBmWxCondition.setText("흐림");
                        setBackgroundWxImage("흐림");
                    }


                }else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        tvBmWxCondition.setText(R.string.rain);
                        setBackgroundWxImage("비");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        tvBmWxCondition.setText(R.string.rain_snow);
                        setBackgroundWxImage("비/눈");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        tvBmWxCondition.setText(R.string.snow);
                        setBackgroundWxImage("눈");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        tvBmWxCondition.setText(R.string.shower);
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

        ViewGroup.LayoutParams params = mLlWxPage.getLayoutParams();

        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        mLlWxPage.setLayoutParams(params);

    }

    private ScheduleList getCurPlaceWxFromSp() {

        //Preference에 날씨 정보 객체 불러오기

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null일 경우 처리할것.
        String loaded = pref.getString("currentPlace", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference에 저장된 데이터 class 형태로 불러오기 완료

        return scheduleList;

    }

    private String getGpsPosition(){
        String result="";
        //현재위치 좌표 받아오기기
        double latitude  = 0;
        double longitude = 0;
        mGpsTracker = new GpsTracker(mNowWxContext);
        Location currentLocation = mGpsTracker.getLocation();
        latitude = currentLocation.getLatitude();
        longitude = currentLocation.getLongitude();

        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> gList = null;
        try {
            gList = geocoder.getFromLocation(latitude,longitude,8);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "setMaskLocation() - 서버에서 주소변환시 에러발생");
            // Fragment1 으로 강제이동 시키기
        }
        if (gList != null) {
            if (gList.size() == 0) {
                Toast.makeText(getBaseContext(), " 현재위치에서 검색된 주소정보가 없습니다. ", Toast.LENGTH_SHORT).show();

            } else {

                Address address = gList.get(0);
                String sido = address.getAdminArea();       // 경기도
                String gugun = address.getSubLocality();    // 성남시
                String emd = address.getThoroughfare();     //금곡동
                Log.i(TAG, address.toString());
                Log.i(TAG,sido + gugun);

                result = arrangeGpsResults(address);

            }
        }

        return result;
    }

    private String arrangeGpsResults(Address address){

        String gps="";
        ArrayList<String> temp = new ArrayList<>();

        temp.add(address.getAdminArea());
        temp.add(address.getSubAdminArea());
        temp.add(address.getLocality());
        temp.add(address.getSubLocality());
        temp.add(address.getThoroughfare().substring(0,2));

        temp.removeAll(Arrays.asList("", null));

        for(int i=0;i<temp.size();i++){
            if(!(i==temp.size()-1)){
                gps = gps + temp.get(i) + " ";
            }else {
                gps = gps + temp.get(i);
            }
        }

        return gps;
    }

    private Date getToday(){
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private ScheduleData getFirstDateSchedule(ScheduleList scheduleList) {

        ArrayList<Integer> temp = new ArrayList<>();
        //지난 스케쥴 제거
        scheduleList = removePastSchedule(scheduleList);



        for(int i =0;i<scheduleList.getScheduleArrayList().size();i++) {
            temp.add(Integer.valueOf(scheduleList.getScheduleArrayList().get(i).getScheduleData().getScheduledDate()));
        }
        //가장 최근 날짜
        int minDate = Collections.min(temp);

        minDate = getIndexFirestdateSchedule(minDate, temp);

        return scheduleList.getScheduleArrayList().get(minDate).getScheduleData();

    }

    private int getIndexFirestdateSchedule(int minDate, ArrayList<Integer> temp) {

        String strMinDate = String.valueOf(minDate);

        for(int i=0;i<temp.size();i++){
            if((temp.get(i)).equals(strMinDate)) return i;
        }

        return 0;
    }

    private ScheduleList removePastSchedule(ScheduleList scheduleList) {

        String today = getFutureDay("yyyyMMdd", 0);
        int intToday = Integer.parseInt(today);

        for(int i =0;i<scheduleList.getScheduleArrayList().size();i++) {
            if(intToday > Integer.valueOf(scheduleList.getScheduleArrayList().get(i).getScheduleData().getScheduledDate())){
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

        mIvNowInfo = findViewById(R.id.iv_now_info);

        mTvBmForcast = findViewById(R.id.tv_now_weather_forecast);

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

    public String getDateDay(String date) {

        String day = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date nDate = null;
        try {
            nDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }
        return day;
    }

    public void refreshList(){
        mBmAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        decorBottom();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        decorBottom();
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }else {
            super.onBackPressed();
        }
    }

}
