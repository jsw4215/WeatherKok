package com.example.weatherkok.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.utils.WxNowListAdapter;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.where.WhereActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

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
    boolean mTrashChecker = false;
    ArrayList<Boolean> mDelList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_now_bookmarklist);

        initView();

        ScheduleList currentPlace = getValueWithKeyFromSp("currentPlace");

        mScheduleList = getValueWithKeyFromSp("bookMark");

        makeCurrentView(currentPlace);

        if(mScheduleList!=null&&mScheduleList.getScheduleArrayList()!=null&&mScheduleList.getScheduleArrayList().size()!=0) {
            makeRv(mScheduleList);
        }

        listeners();
    }

    private Date getToday() {
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private void makeCurrentView(ScheduleList scheduleList) {

        String today = getFutureDay("yyyyMMdd",0);

        mTvDate.setText(today);

        mTvPlace.setText(scheduleList.getScheduleArrayList().get(0).getScheduleData().getPlace());

        String hh = makingStrHour();
        findScheduleDateWxData(scheduleList.getScheduleArrayList().get(0).getScheduleData(),true,0, hh);

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
        }

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

        mTvDate = findViewById(R.id.tv_bm_weather_dates_now);

        mTvPlace = findViewById(R.id.tv_bm_weather_location_now);

        mIvNowAm = findViewById(R.id.iv_bm_weather_am_now);

        mIvNowPm = findViewById(R.id.iv_bm_weather_pm_now);

        mRvBml = findViewById(R.id.rv_bmlist);

        mTvBmWxListAdd = findViewById(R.id.tv_bmlist_add_place);

        mTvBmWxListDelete = findViewById(R.id.tv_bmlist_deletion);

        mIvBmTrash = findViewById(R.id.iv_bml_trashcan);

        mIvBmTrash.setVisibility(View.GONE);
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


}
