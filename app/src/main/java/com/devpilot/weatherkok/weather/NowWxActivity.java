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

    //?????? ????????? ?????? ???????????? ????????????, ???????????? ????????? ???????????? ????????? ????????? ????????? ?????? ???
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_now_weather);
        mNowWxContext = getBaseContext();

        boolean key = PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_AD_KEY);

        Log.i(TAG, "weather");
        //???????????? ?????? ?????? ?????? ???????????? ??? preference??? ????????? ??????

        initCenterView();

        initView();

        decorCenter();

        decorBottom();

        decorTop();

        admob(key);

        //??????
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

    // ???????????? ??????
    private void initSwitchLayout(final WorkManager workManager) {

        navigationView.getMenu().findItem(R.id.menu_alarm).setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.menu_alarm).getActionView()).setChecked(PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_NOTIFICATION_KEY));


        ((Switch) navigationView.getMenu().findItem(R.id.menu_alarm).getActionView())
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(isChecked) {
                        Toast.makeText(NowWxActivity.this, "Checked", Toast.LENGTH_SHORT).show();

                        //?????? ??????
                        boolean isChannelCreated = NotificationHelper.isNotificationChannelCreated(getApplicationContext());
                        if (isChannelCreated) {
                            PreferenceHelper.setBoolean(getApplicationContext(), SHARED_PREF_NOTIFICATION_KEY, true);
                            NotificationHelper.setScheduledNotification(workManager);
                        } else {
                            NotificationHelper.createNotificationChannel(getApplicationContext());
                        }
                    } else {
                        Toast.makeText(NowWxActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                        //?????? ??????

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
//                        Toast.makeText(NowWxActivity.this, "?????? ??????", Toast.LENGTH_SHORT).show();
//                        //?????? ?????????
//                        PreferenceHelper.setBoolean(getApplicationContext(), SHARED_PREF_AD_KEY, true);
//                        admob(true);
//                    } else {
//                        Toast.makeText(NowWxActivity.this, "?????? ?????????", Toast.LENGTH_SHORT).show();
//                        //?????? ??????
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
//                email.putExtra(Intent.EXTRA_SUBJECT, "(???????????????) ????????? - ????????????");
//                email.putExtra(Intent.EXTRA_TEXT,"?????? ????????? : ");
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

            MobileAds.initialize(this, new OnInitializationCompleteListener() { //?????? ?????????
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

        //???????????? ????????? ??????
        mRvBmWxList = (RecyclerView) findViewById(R.id.rv_bookmark_wx_list);
        tvBmNoList = findViewById(R.id.tv_bm_weather_no_list);

        if(schedule==null||schedule.getScheduleData().getFcst().getWxToday()==null) {
            tvBmNoList.setVisibility(View.VISIBLE);
            mRvBmWxList.setVisibility(View.GONE);
        } else {
            mBmAdapter = new SingleWxAdapter(schedule, this);
            tvBmNoList.setVisibility(View.GONE);
            mRvBmWxList.setVisibility(View.VISIBLE);
            //Rv?????? ??????


            mRvBmWxList.setLayoutManager(new LinearLayoutManager(this));
            mRvBmWxList.setAdapter(mBmAdapter);
        }

    }

    private ScheduleList getBookMarkListFromSp() {

        //Preference??? ?????? ?????? ?????? ????????????

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null??? ?????? ????????????.
        String loaded = pref.getString("bookMark", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference??? ????????? ????????? class ????????? ???????????? ??????

        return scheduleList;

    }

    private void initCenterView() {
        //??????????????? ?????? ?????? ????????? ???????????? ????????? ?????????.

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

        //???,???,?????? ?????? ??????
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
        String currentHour = curHour.format(dateToday);

        scheduleList = getCurPlaceWxFromSp();

        String where = scheduleList.getScheduleArrayList().get(0).getScheduleData().getPlace();

        where = removeAdminArea(where);

        tvBmWxPlace.setText(where);

        //??????????????? ?????? ?????? ???????????? preference??? currentPlaceWx
        ScheduleData scheduleData = scheduleList.getScheduleArrayList().get(0).getScheduleData();

        currentHour=makingStrHour(currentHour);

        setNowWxCond(scheduleData, currentHour);

        //???????????? ???????????? ?????? ?????? ?????????

        //???????????? ??????
        //???????????? response?????? ?????? ?????? ???????????? TMP ?????? ???????????? ???????????? ????????????

    }

    private String removeAdminArea(String location) {

        String[] splited = location.split(" ");
        String temp2="";

        if(splited[0].startsWith("??????")){
            //????????????
            if(splited[1].startsWith("??????")) {

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

            if(splited[1].endsWith("???")) {

            }else{
                temp2 = splited[0] + " " + temp2;
            }
        }

        return temp2;

    }

    private String setAdminString(String s) {

        if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="?????????";
        }else if(s.startsWith("??????")){
            s="??????";
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
        //Preference??? ?????? ?????? ????????????
        //JSON?????? ??????
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //?????????
        //editor.remove(year + month);

        editor.putString("currentPlace", jsonString);
        editor.commit();
        //????????????

    }

    private void setNowWxCond(ScheduleData scheduleData,String strCurHH){
        //????????????????????? ???????????? ?????? ????????? ????????????.
        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
        String wx= "??????";

        for (int i = 0; i < scheduleData.getFcst().getTempToday().size(); i++) {
            if (scheduleData.getFcst().getTempToday().get(i).getTimeHhmm().equals(strCurHH)) {
                tvBmWxTemperature.setText(scheduleData.getFcst().getTempToday().get(i).getTemperature() + "\u00B0");

                tvBmWxTempMaxMin.setText("?????? " + scheduleData.getFcst().getTempList().getItem().get(0).getTaMax0() + "\u00B0 ?????? "
                        + scheduleData.getFcst().getTempList().getItem().get(0).getTaMin0() + "\u00B0");
            }
        }


        for(int i=0;i<scheduleData.getFcst().getWxToday().size();i++){

            if(scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)){

                if(scheduleData.getFcst().getWxToday().get(i).getRainType()==0) {

                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        tvBmWxCondition.setText("??????");
                        setBackgroundWxImage("??????");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        tvBmWxCondition.setText("????????????");
                        setBackgroundWxImage("????????????");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        tvBmWxCondition.setText("??????");
                        setBackgroundWxImage("??????");
                    }


                }else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //???
                        tvBmWxCondition.setText(R.string.rain);
                        setBackgroundWxImage("???");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //??????
                        tvBmWxCondition.setText(R.string.rain_snow);
                        setBackgroundWxImage("???/???");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //???
                        tvBmWxCondition.setText(R.string.snow);
                        setBackgroundWxImage("???");
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //?????????
                        tvBmWxCondition.setText(R.string.shower);
                        setBackgroundWxImage("???");
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

        if(wx.contains("??????")){
            mLlWxPage.setBackground(sunny);
        }else if(wx.contains("??????")){
            mLlWxPage.setBackground(cloudy);
        }else if(wx.contains("??????")){
            mLlWxPage.setBackground(gray);
        }else if(wx.equals("???/???")){
            mLlWxPage.setBackground(snowRain);
        }else if(wx.contains("???")){
            mLlWxPage.setBackground(rain);
        }else if(wx.contains("???")){
            mLlWxPage.setBackground(snow);
        }else if(wx.contains("?????????")){
            mLlWxPage.setBackground(shower);
        }

        ViewGroup.LayoutParams params = mLlWxPage.getLayoutParams();

        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        mLlWxPage.setLayoutParams(params);

    }

    private ScheduleList getCurPlaceWxFromSp() {

        //Preference??? ?????? ?????? ?????? ????????????

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null??? ?????? ????????????.
        String loaded = pref.getString("currentPlace", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference??? ????????? ????????? class ????????? ???????????? ??????

        return scheduleList;

    }

    private String getGpsPosition(){
        String result="";
        //???????????? ?????? ???????????????
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
            Log.e("TAG", "setMaskLocation() - ???????????? ??????????????? ????????????");
            // Fragment1 ?????? ???????????? ?????????
        }
        if (gList != null) {
            if (gList.size() == 0) {
                Toast.makeText(getBaseContext(), " ?????????????????? ????????? ??????????????? ????????????. ", Toast.LENGTH_SHORT).show();

            } else {

                Address address = gList.get(0);
                String sido = address.getAdminArea();       // ?????????
                String gugun = address.getSubLocality();    // ?????????
                String emd = address.getThoroughfare();     //?????????
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
        // ????????? ????????? ?????? ?????????.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private ScheduleData getFirstDateSchedule(ScheduleList scheduleList) {

        ArrayList<Integer> temp = new ArrayList<>();
        //?????? ????????? ??????
        scheduleList = removePastSchedule(scheduleList);



        for(int i =0;i<scheduleList.getScheduleArrayList().size();i++) {
            temp.add(Integer.valueOf(scheduleList.getScheduleArrayList().get(i).getScheduleData().getScheduledDate()));
        }
        //?????? ?????? ??????
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

        //Preference??? ?????? ?????? ?????? ????????????

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null??? ?????? ????????????.
        String loaded = pref.getString("schedule", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference??? ????????? ????????? class ????????? ???????????? ??????

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
                day = "???";
                break;
            case 2:
                day = "???";
                break;
            case 3:
                day = "???";
                break;
            case 4:
                day = "???";
                break;
            case 5:
                day = "???";
                break;
            case 6:
                day = "???";
                break;
            case 7:
                day = "???";
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
