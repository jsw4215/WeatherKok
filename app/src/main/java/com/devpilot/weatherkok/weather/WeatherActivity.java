package com.devpilot.weatherkok.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

<<<<<<< HEAD:app/src/main/java/com/devpilot/weatherkok/weather/WeatherActivity.java
import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.alarm.NotificationHelper;
import com.devpilot.weatherkok.alarm.PreferenceHelper;
import com.devpilot.weatherkok.datalist.data.ScheduleData;
import com.devpilot.weatherkok.intro.IntroActivity;
import com.devpilot.weatherkok.src.BaseActivity;
import com.devpilot.weatherkok.weather.utils.WeatherActivityAdapter;
import com.devpilot.weatherkok.weather.utils.WxKokDataPresenter;
import com.devpilot.weatherkok.weather.webview.WebViewActivity;
import com.devpilot.weatherkok.when.models.Schedule;
import com.devpilot.weatherkok.when.models.ScheduleList;
import com.devpilot.weatherkok.who.kakao.kotlin.WhoActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
=======
import com.example.weatherkok.R;
import com.example.weatherkok.alarm.NotificationHelper;
import com.example.weatherkok.alarm.PreferenceHelper;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.intro.IntroActivity;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.utils.WeatherActivityAdapter;
import com.example.weatherkok.weather.utils.WxKokDataPresenter;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.who.kakao.kotlin.WhoActivity;
>>>>>>> bf5f93840116430a1a1916dde84260ef469ebeed:app/src/main/java/com/example/weatherkok/weather/WeatherActivity.java
import com.google.android.material.navigation.NavigationView;
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

import static com.devpilot.weatherkok.alarm.Constants.SHARED_PREF_AD_KEY;
import static com.devpilot.weatherkok.alarm.Constants.SHARED_PREF_NOTIFICATION_KEY;

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
    DrawerLayout mDrawerLayout;
    ImageView mIvBmMenu;
    NavigationView navigationView;
    Toolbar mTbWxMain;
    ActionBar mActionBar;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    LinearLayout mLlWxPage;
    ScheduleList mExceptTheFirstScheduleList;
    int mFirstScheduleIdx;
    TextView mTvBmForcast;
    private AdView mAdview;
    FrameLayout adContainerView;
    LinearLayout mCtrNoSch;
    LinearLayout mLlGoToNow;

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


        boolean key = PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_AD_KEY);

        initView();

        initCenterView();

        if(mScheduleList!=null&&mScheduleList.getScheduleArrayList()!=null&&mScheduleList.getScheduleArrayList().size()!=0) {
            //지난 스케쥴을 삭제하고, 가장 빠른일자를 골라내는 함수를 한 뒤,
            decorCenter();
        }
        //아래의 나머지 스케쥴 리스트를 연동
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

        tvGotoNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WeatherActivity.this, IntroActivity.class);
                intent.putExtra("from","goToNow");
                startActivity(intent);
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                finish();
            }
        });

        mLlGoToNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WeatherActivity.this, IntroActivity.class);
                intent.putExtra("from","goToNow");
                startActivity(intent);
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                finish();
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
                Intent intent = new Intent(WeatherActivity.this, WxListActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // 푸시알림 설정
    private void initSwitchLayout(final WorkManager workManager) {

        navigationView.getMenu().findItem(R.id.menu_alarm).setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.menu_alarm).getActionView()).setChecked(PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_NOTIFICATION_KEY));

        ((Switch) navigationView.getMenu().findItem(R.id.menu_alarm).getActionView())
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(isChecked) {
                        Toast.makeText(WeatherActivity.this, "Checked", Toast.LENGTH_SHORT).show();

                        //알람 등록
                        boolean isChannelCreated = NotificationHelper.isNotificationChannelCreated(getApplicationContext());
                        if (isChannelCreated) {
                            PreferenceHelper.setBoolean(getApplicationContext(), SHARED_PREF_NOTIFICATION_KEY, true);
                            NotificationHelper.setScheduledNotification(workManager);
                        } else {
                            NotificationHelper.createNotificationChannel(getApplicationContext());
                        }
                    } else {
                        Toast.makeText(WeatherActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                        //알람 해제

                        PreferenceHelper.setBoolean(getApplicationContext(), SHARED_PREF_NOTIFICATION_KEY, false);
                        workManager.cancelAllWork();

                    }
                });

<<<<<<< HEAD:app/src/main/java/com/devpilot/weatherkok/weather/WeatherActivity.java
//        navigationView.getMenu().findItem(R.id.menu_ad_delete).setActionView(new Switch(this));
//        ((Switch) navigationView.getMenu().findItem(R.id.menu_ad_delete).getActionView()).setChecked(PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_AD_KEY));
//
//        ((Switch) navigationView.getMenu().findItem(R.id.menu_ad_delete).getActionView())
//                .setOnCheckedChangeListener((buttonView, isChecked) -> {
//                    if(isChecked) {
//                        Toast.makeText(WeatherActivity.this, "광고 제거", Toast.LENGTH_SHORT).show();
//                        //광고 안보임
//                            PreferenceHelper.setBoolean(getApplicationContext(), SHARED_PREF_AD_KEY, true);
//                            admob(true);
//
//                    } else {
//                        Toast.makeText(WeatherActivity.this, "광고 활성화", Toast.LENGTH_SHORT).show();
//                        //광고 보임
//                            PreferenceHelper.setBoolean(getApplicationContext(), SHARED_PREF_AD_KEY, false);
//                            admob(false);
//
//                    }
//                });

//            navigationView.getMenu().findItem(R.id.menu_share).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//                    Intent share = new Intent(getBaseContext(), WhoActivity.class);
//                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    startActivity(share);
//
//                    return false;
//                }
//            });

//            navigationView.getMenu().findItem(R.id.menu_email).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//                    Intent email = new Intent(Intent.ACTION_SEND);
//                    email.setType("plain/text");
//                    String[] address = {"weatherkok@gmail.com"};
//                    email.putExtra(Intent.EXTRA_EMAIL, address);
//                    email.putExtra(Intent.EXTRA_SUBJECT, "(안드로이드) 날씨콕 - 문의하기");
//                    email.putExtra(Intent.EXTRA_TEXT,"하고 싶은말 : ");
//                    startActivity(email);
//
//                    return true;
//                }
//            });

        navigationView.getMenu().findItem(R.id.menu_terms).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent share = new Intent(getBaseContext(), WebViewActivity.class);
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(share);

                return false;
            }
        });

=======
            navigationView.getMenu().findItem(R.id.menu_share).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Intent share = new Intent(getBaseContext(), WhoActivity.class);
                    finish();
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(share);

                    return false;
                }
            });

            navigationView.getMenu().findItem(R.id.menu_email).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("plain/text");
                    String[] address = {"weatherkok@gmail.com"};
                    email.putExtra(Intent.EXTRA_EMAIL, address);
                    email.putExtra(Intent.EXTRA_SUBJECT, "(안드로이드) 날씨콕 - 문의하기");
                    email.putExtra(Intent.EXTRA_TEXT,"하고 싶은말 : ");
                    startActivity(email);

                    return true;
                }
            });
>>>>>>> bf5f93840116430a1a1916dde84260ef469ebeed:app/src/main/java/com/example/weatherkok/weather/WeatherActivity.java
    }

    private void decorBottom() {

        tvBmNoList = findViewById(R.id.tv_bm_weather_no_list);
        mRvBmWxList = (RecyclerView) findViewById(R.id.rv_bookmark_wx_list);

        if(mScheduleList==null||mScheduleList.getScheduleArrayList()==null||mScheduleList.getScheduleArrayList().size()==0) {
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
        mLlWxPage = findViewById(R.id.ll_weather_page);

        mCtrNoSch = findViewById(R.id.center_no_schedule_container);
        mLlGoToNow = findViewById(R.id.ll_go_to_now);

        checkNoSchedule();

    }

    private void checkNoSchedule() {

        if(mScheduleList==null||mScheduleList.getScheduleArrayList()==null||mScheduleList.getScheduleArrayList().size()==0){
//            Intent intent = new Intent(WeatherActivity.this, IntroActivity.class);
//            intent.putExtra("from","goToWeather");
//            startActivity(intent);

            rlBmWxCtr.setVisibility(View.GONE);
            mCtrNoSch.setVisibility(View.VISIBLE);

        }else{

            rlBmWxCtr.setVisibility(View.VISIBLE);
            mCtrNoSch.setVisibility(View.GONE);

        }

    }

    private void decorTop(){


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

        String where = schedule.getWhere();

        where=removeAdminArea(where);

        tvBmWxPlace.setText(where);

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
        mScheduleList.getScheduleArrayList().remove(mFirstScheduleIdx);
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

        String wx = "맑음";

        String strCurHH = "";

        if(currHh<10){
            strCurHH = "0" + String.valueOf(currHh);
        }else{
            strCurHH = String.valueOf(currHh);
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
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm());
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
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
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Am();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm());
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
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
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Am();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf3Pm());
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Pm();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt3Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
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
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Am();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf4Pm());
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Pm();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt4Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
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
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Am();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf5Pm());
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Pm();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt5Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
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
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Am();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf6Pm());
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Pm();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt6Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
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
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Am();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Am()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Am()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Am()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Am()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
                }

            }else {

                if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Pm()==0){
                    tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf7Pm());
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Pm();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Pm()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx = "비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Pm()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx = "비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Pm()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx = "눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt7Pm()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx = "소나기";
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
                    wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf8();
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt8()==1){
                    //비
                    tvBmWxCondition.setText(R.string.rain);
                    wx="비";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt8()==2){
                    //비눈
                    tvBmWxCondition.setText(R.string.rain_snow);
                    wx="비/눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt8()==3){
                    //눈
                    tvBmWxCondition.setText(R.string.snow);
                    wx="눈";
                }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt8()==4){
                    //소나기
                    tvBmWxCondition.setText(R.string.shower);
                    wx="소나기";
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
                wx=scheduleData.getFcst().getWxList().getItem().get(0).getWf9();
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt9()==1){
                //비
                tvBmWxCondition.setText(R.string.rain);
                wx="비";
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt9()==2){
                //비눈
                tvBmWxCondition.setText(R.string.rain_snow);
                wx="비/눈";
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt9()==3){
                //눈
                tvBmWxCondition.setText(R.string.snow);
                wx="눈";
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt9()==4){
                //소나기
                tvBmWxCondition.setText(R.string.shower);
                wx="소나기";
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
                wx=scheduleData.getFcst().getWxList().getItem().get(0).getWf10();
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt10()==1){
                //비
                tvBmWxCondition.setText(R.string.rain);
                wx="비";
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt10()==2){
                //비눈
                tvBmWxCondition.setText(R.string.rain_snow);
                wx="비/눈";
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt10()==3){
                //눈
                tvBmWxCondition.setText(R.string.snow);
                wx="눈";
            }else if(scheduleData.getFcst().getWxList().getItem().get(0).getRnSt10()==4){
                //소나기
                tvBmWxCondition.setText(R.string.shower);
                wx="소나기";
            }
        }
        setBackgroundWxImage(wx);

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

    private void setNowWxCond(ScheduleData scheduleData,String strCurHH){
        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)

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
                tvBmWxTemperature.setText(schedule.getScheduleData().getFcst().getTempToday().get(i).getTemperature() + "\u00B0");
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
        mFirstScheduleIdx = minDate;
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

        mTvBmForcast = findViewById(R.id.tv_bm_weather_forecast);

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
        if(mWxAdapter!=null) {
            mWxAdapter.notifyDataSetChanged();
        }
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