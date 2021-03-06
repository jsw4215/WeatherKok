package com.devpilot.weatherkok.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.alarm.PreferenceHelper;
import com.devpilot.weatherkok.datalist.data.ScheduleData;
import com.devpilot.weatherkok.src.BaseActivity;
import com.devpilot.weatherkok.weather.utils.WxNowListAdapter;
import com.devpilot.weatherkok.when.models.Schedule;
import com.devpilot.weatherkok.when.models.ScheduleList;
import com.devpilot.weatherkok.where.WhereActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import static com.devpilot.weatherkok.alarm.Constants.SHARED_PREF_AD_KEY;

public class WxNowListActivity extends BaseActivity {
    private static final String TAG = WxNowListActivity.class.getSimpleName();
    RecyclerView mRvBml;
    WxNowListAdapter mBmlAdapter;
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ScheduleList mScheduleList;
    TextView mTvBmWxListDelete;
    TextView mTvBmWxListAdd;
    ImageView mIvBackArrow;
    ImageView mIvBmTrash;
    ImageView mIvNowAm;
    ImageView mIvNowPm;
    TextView mTvPlace;
    TextView mTvDate;
    TextView mTvNowDegrees;
    boolean mTrashChecker = false;
    ArrayList<Boolean> mDelList = new ArrayList<>();
    private AdView mAdview;
    FrameLayout adContainerView;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_now_bookmarklist);

        boolean key = PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_AD_KEY);

        initView();

        ScheduleList currentPlace = getValueWithKeyFromSp("currentPlace");

        mScheduleList = getValueWithKeyFromSp("bookMark");

        makeCurrentView(currentPlace);

        if(mScheduleList!=null&&mScheduleList.getScheduleArrayList()!=null&&mScheduleList.getScheduleArrayList().size()!=0) {
            makeRv(mScheduleList);
        }

        listeners();

        admob(key);
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


    private Date getToday() {
        // ????????? ????????? ?????? ?????????.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private void makeCurrentView(ScheduleList scheduleList) {

        String today = getFutureDay("yyyyMMdd",0);

        String day = getDateDay(today);

        String month = today.substring(4,6);
        //09 -> 9??? ?????????
        int intMonth = Integer.parseInt(month);
        month = String.valueOf(intMonth);

        String date = today.substring(6);

        int intDate = Integer.parseInt(date);
        date = String.valueOf(intDate);

        today = month + " / " + date + "(" + day + ")";

        String where = scheduleList.getScheduleArrayList().get(0).getScheduleData().getPlace();

        mTvPlace.setText(where);

        String hh = makingStrHour();
        findScheduleDateWxData(scheduleList.getScheduleArrayList().get(0).getScheduleData(),true,0, hh);

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

    public static String getFutureDay(String pattern, int gap) {
        DateFormat dtf = new SimpleDateFormat(pattern);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, gap);
        return dtf.format(cal.getTime());
    }

    private String makingStrHour() {

        Date dateToday = getToday();

        //???,???,?????? ?????? ??????
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
        String currentHh = curHour.format(dateToday);

        int currHh = Integer.parseInt(currentHh);

        if(currHh<10){
            currentHh = "0" + String.valueOf(currHh);
        }

        currentHh = currentHh + "00";

        return currentHh;

    }

    private void findScheduleDateWxData(ScheduleData scheduleData, boolean checker, int diffDays, String strCurHH) {

        if (diffDays == 0) {
            //???????????? ??????????????? ?????? ?????? ????????? ??????????????? ?????????

            setNowWxCond(scheduleData, strCurHH);
            
            setNowTempCond(scheduleData, strCurHH);
        }

    }

    private void setNowTempCond(ScheduleData scheduleData, String strCurHH) {


        int tempMax = scheduleData.getFcst().getTempList().getItem().get(0).getTaMax0();
        int tempMin = scheduleData.getFcst().getTempList().getItem().get(0).getTaMin0();
        
        mTvNowDegrees.setText(tempMax + "\u00B0" + " / "
                + tempMin + "\u00B0");
        
    }

    private void setNowWxCond(ScheduleData scheduleData, String strCurHH) {

        Drawable sun = getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = getResources().getDrawable(R.drawable.ic_gray);
        Drawable snowRain = getResources().getDrawable(R.drawable.ic_rain_snow);


        //????????????????????? ???????????? ?????? ????????? ????????????.
        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
        //???????????????, ????????? ????????? ??????
        for (int i = 0; i < scheduleData.getFcst().getWxToday().size(); i++) {

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)) {
                //??????????????? ?????????
                    //?????? ?????????,
                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                        //??????????????? ???????????? ?????? ?????????
                        Log.i(TAG, "?????? ??????");
                        if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                            mIvNowAm.setImageDrawable(sun);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                            mIvNowAm.setImageDrawable(cloudy);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                            mIvNowAm.setImageDrawable(wind);
                        }
                    } else {

                        if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                            //???
                            mIvNowAm.setImageDrawable(rain);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                            //??????
                            mIvNowAm.setImageDrawable(snowRain);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                            //???
                            mIvNowAm.setImageDrawable(snow);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                            //?????????
                            mIvNowAm.setImageDrawable(shower);
                        }
                    }
            }
        }

        String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm();
        Log.i(TAG, "?????? ?????? : " + wx);
        //????????????
        if (wx.equals("??????")) {

            mIvNowPm.setImageDrawable(sun);
        } else if (wx.equals("????????????")) {
            mIvNowPm.setImageDrawable(cloudy);
        } else if (wx.equals("??????")) {
            mIvNowPm.setImageDrawable(wind);
        } else if (wx.contains("???")) {
            mIvNowPm.setImageDrawable(rain);
        } else if (wx.contains("???")) {
            mIvNowPm.setImageDrawable(snow);
        } else if (wx.contains("?????????")) {
            mIvNowPm.setImageDrawable(shower);
        }

    }


    private void initView(){

        mTvPlace = findViewById(R.id.tv_bm_weather_location_now);

        mIvNowAm = findViewById(R.id.iv_bm_weather_am_now);

        mIvNowPm = findViewById(R.id.iv_bm_weather_pm_now);

        mRvBml = findViewById(R.id.rv_bmlist);

        mTvBmWxListAdd = findViewById(R.id.tv_bmlist_add_place);

        mTvBmWxListDelete = findViewById(R.id.tv_bmlist_deletion);

        mIvBmTrash = findViewById(R.id.iv_bml_trashcan);

        mIvBmTrash.setVisibility(View.GONE);

        mIvBackArrow = findViewById(R.id.iv_bml_back_arrow_top_bar);
        
        mTvNowDegrees = findViewById(R.id.tv_now_weather_degrees);
    }

    private ScheduleList getValueWithKeyFromSp(String key) {

        //Preference??? ?????? ?????? ?????? ????????????

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null??? ?????? ????????????.
        String loaded = pref.getString(key, "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference??? ????????? ????????? class ????????? ???????????? ??????

        return scheduleList;

    }


    private void listeners(){

        mTvBmWxListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), WhereActivity.class);
                intent.putExtra("from","nowWx");
                startActivity(intent);
            }
        });

        mTvBmWxListDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mBmlAdapter!=null&&mScheduleList!=null) {

                    mIvBmTrash.setVisibility(View.VISIBLE);

                    mTvBmWxListDelete.setVisibility(View.GONE);

                    mTrashChecker = true;

                    mBmlAdapter.notifyDataSetChanged();

                }

            }
        });

        mIvBmTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //????????????

                mIvBmTrash.setVisibility(View.GONE);

                mTvBmWxListDelete.setVisibility(View.VISIBLE);

                deleteList();

                mTrashChecker = false;

                makeRv(mScheduleList);

                setScheduleDataInToSp(mScheduleList);

            }
        });

        mIvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        Iterator it = mScheduleList.getScheduleArrayList().iterator();

        while(it.hasNext()) {
            Schedule schedule = (Schedule) it.next();

            if(schedule.getYear()!=null&&schedule.getYear().equals("delete")){
                it.remove();
            }
        }


    }

    public boolean ismTrashChecker() {
        return mTrashChecker;
    }

    private void makeRv(ScheduleList scheduleList){

            mBmlAdapter = new WxNowListAdapter(WxNowListActivity.this, scheduleList);

            mRvBml.setLayoutManager(new LinearLayoutManager(this));
            mRvBml.setAdapter(mBmlAdapter);
            mBmlAdapter.notifyDataSetChanged();

    }

    private void setScheduleDataInToSp(ScheduleList scheduleList) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        //Preference??? ?????? ?????? ????????????
        //JSON?????? ??????
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //?????????
        //editor.remove(year + month);

        editor.putString("bookMark", jsonString);
        editor.commit();
        //????????????

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


}
