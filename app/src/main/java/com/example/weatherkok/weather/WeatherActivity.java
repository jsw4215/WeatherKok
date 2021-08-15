package com.example.weatherkok.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.utils.BookMarkAdapter;
import com.example.weatherkok.weather.utils.WxKokDataPresenter;
import com.example.weatherkok.when.models.ScheduleList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.DateFormat;
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
    ScheduleList mScheduleList;
    RecyclerView mRvBmWxList;
    BookMarkAdapter mBmAdapter;
    String mScheduledDate;
    ImageView mIvBmWxAdd;


    //날씨 정보를 얻는 서비스를 시작하고, 받아와서 정리된 데이터를 가져와 뿌리는 역할만 하는 곳
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_weather);

        Log.i(TAG, "weather");
        //스케쥴에 따른 날씨 정보 불러오기 및 preference에 데이터 저장
//        mWxKokDataPresenter = new WxKokDataPresenter(getBaseContext());

//        mWxKokDataPresenter.getScheduleDateWxApi();

        initCenterView();

        initView();

        decorCenter();

        decorBottom();
    }

    private void decorBottom() {

        mScheduleList.getScheduleArrayList().remove(0);

        mBmAdapter = new BookMarkAdapter(this, mScheduleList);
        mRvBmWxList = (RecyclerView) findViewById(R.id.rv_bookmark_wx_list);

        mRvBmWxList.setLayoutManager(new LinearLayoutManager(this));
        mRvBmWxList.setAdapter(mBmAdapter);


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
        mIvBmWxAdd = findViewById(R.id.iv_bm_weather_add);

    }

    private void decorTop(){

        mIvBmWxAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, BookMarkListActivity.class);
                startActivity(intent);
            }
        });



    }

    private void decorCenter(){

        mScheduleList = getScheduleFromSp();
        //가장 빠른일자의 스케쥴
        ScheduleData scheduleData = getFirstDateSchedule(mScheduleList);

        String date = scheduleData.getScheduledDate();
        //스케쥴 날짜 세팅
        setmScheduledDate(scheduleData.getScheduledDate());

        tvBmWxDate.setText(scheduleData.getScheduledDate() + " (" + scheduleData.getDay() + ")");

        tvBmWxPlace.setText(scheduleData.getPlace());

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("hh", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        //현재시간 출력하여 오전 오후 나누기
        if(inthour<12){
            tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf0Am());
        }else {
            tvBmWxCondition.setText(scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm());
        }
        //현재온도 적용
        //단기예보 response에서 현재 시간 비교해서 TMP 변수 클래스에 만들어서 넣어두기
        matchingCurTimeGetTemp(currentHour, scheduleData);


        tvBmWxTempMaxMin.setText("최고 " + scheduleData.getFcst().getTempList().getItem().get(0).getTaMax0() + "\\u00B0 최저 "
                + scheduleData.getFcst().getTempList().getItem().get(0).getTaMin0() + "\\u00B0");

    }

    private void matchingCurTimeGetTemp(String currentHour, ScheduleData scheduleData) {

        currentHour = currentHour + "00";

        for(int i = 0;i<scheduleData.getFcst().getTempToday().size();i++){
            if((scheduleData.getFcst().getTempToday().get(i)).equals(currentHour)){
                tvBmWxTemperature.setText(scheduleData.getFcst().getTempToday().get(i) + "\\u00B0");
            }
        }

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

        int maxIndex = Collections.max(temp);

        minDate = getIndexFirestdateSchedule(minDate, temp);

        return scheduleList.getScheduleArrayList().get(minDate).getScheduleData();

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
        scheduleList.getScheduleArrayList().get(0).getScheduleData().setScheduledDate("20210817");
        scheduleList.getScheduleArrayList().get(1).getScheduleData().setScheduledDate("20210826");

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

}