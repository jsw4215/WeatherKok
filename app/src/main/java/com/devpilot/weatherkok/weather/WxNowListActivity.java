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


    private Date getToday() {
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private void makeCurrentView(ScheduleList scheduleList) {

        String today = getFutureDay("yyyyMMdd",0);

        String day = getDateDay(today);

        String month = today.substring(4,6);
        //09 -> 9로 바꾸기
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

    public static String getFutureDay(String pattern, int gap) {
        DateFormat dtf = new SimpleDateFormat(pattern);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, gap);
        return dtf.format(cal.getTime());
    }

    private String makingStrHour() {

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
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
            //오늘이면 시간비교를 해서 해당 시간의 날씨예보를 담을것

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


        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        //비가온다면, 비모양 안오면 날씨
        for (int i = 0; i < scheduleData.getFcst().getWxToday().size(); i++) {

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)) {
                //강수형태를 봤더니
                    //비가 안오면,
                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                        //하늘상태를 찾아야함 같은 시간의
                        Log.i(TAG, "오늘 현재");
                        if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                            mIvNowAm.setImageDrawable(sun);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                            mIvNowAm.setImageDrawable(cloudy);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                            mIvNowAm.setImageDrawable(wind);
                        }
                    } else {

                        if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                            //비
                            mIvNowAm.setImageDrawable(rain);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                            //비눈
                            mIvNowAm.setImageDrawable(snowRain);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                            //눈
                            mIvNowAm.setImageDrawable(snow);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                            //소나기
                            mIvNowAm.setImageDrawable(shower);
                        }
                    }
            }
        }

        String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm();
        Log.i(TAG, "오늘 오후 : " + wx);
        //기본날씨
        if (wx.equals("맑음")) {

            mIvNowPm.setImageDrawable(sun);
        } else if (wx.equals("구름많음")) {
            mIvNowPm.setImageDrawable(cloudy);
        } else if (wx.equals("흐림")) {
            mIvNowPm.setImageDrawable(wind);
        } else if (wx.contains("비")) {
            mIvNowPm.setImageDrawable(rain);
        } else if (wx.contains("눈")) {
            mIvNowPm.setImageDrawable(snow);
        } else if (wx.contains("소나기")) {
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

                //삭제처리

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

        //Preference에 정보 객체 저장하기
        //JSON으로 변환
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //초기화
        //editor.remove(year + month);

        editor.putString("bookMark", jsonString);
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


}
