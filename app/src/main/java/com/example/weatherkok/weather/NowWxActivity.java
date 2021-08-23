package com.example.weatherkok.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.intro.IntroActivity;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.utils.NowWxActivityAdapter;
import com.example.weatherkok.weather.utils.WxKokDataPresenter;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.where.utils.GpsTracker;
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
    NowWxActivityAdapter mBmAdapter;
    String mScheduledDate;
    GpsTracker mGpsTracker;
    public static Context mNowWxContext;
    ImageView mIvBmWxAdd;
    TextView tvBmNoList;

    //날씨 정보를 얻는 서비스를 시작하고, 받아와서 정리된 데이터를 가져와 뿌리는 역할만 하는 곳
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_weather);
        mNowWxContext = getBaseContext();

        Log.i(TAG, "weather");
        //스케쥴에 따른 날씨 정보 불러오기 및 preference에 데이터 저장

        initCenterView();

        initView();

        decorCenter();

        decorBottom();

        decorTop();

        mIvBmWxAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NowWxActivity.this, WxNowListActivity.class);
                intent.putExtra("from","now");
                startActivity(intent);
            }
        });

        tvGotoNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(NowWxActivity.this, IntroActivity.class);
                intent.putExtra("from","goToWeather");
                startActivity(intent);

            }
        });

    }

    private void decorTop(){
        mIvBmWxAdd = findViewById(R.id.iv_bm_weather_add);

        mIvBmWxAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NowWxActivity.this, WxListActivity.class);
                startActivity(intent);
            }
        });

    }

    private void decorBottom() {

        mScheduleList = getBookMarkListFromSp();

        mRvBmWxList = (RecyclerView) findViewById(R.id.rv_bookmark_wx_list);
        tvBmNoList = findViewById(R.id.tv_bm_weather_no_list);

        if(mScheduleList==null||mScheduleList.getScheduleArrayList()==null||mScheduleList.getScheduleArrayList().size()==0) {
            tvBmNoList.setVisibility(View.VISIBLE);
            mRvBmWxList.setVisibility(View.GONE);
        } else {
            mBmAdapter = new NowWxActivityAdapter(this, mScheduleList);
            tvBmNoList.setVisibility(View.GONE);
            mRvBmWxList.setVisibility(View.VISIBLE);

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

        rlBmWxCtr = findViewById(R.id.rl_bm_weather_center);
        tvBmWxDate = findViewById(R.id.tv_bm_weather_dates);
        tvBmWxPlace = findViewById(R.id.tv_bm_weather_location);
        tvBmWxCondition = findViewById(R.id.tv_bm_weather_condition);
        tvBmWxTemperature = findViewById(R.id.tv_bm_weather_temperature);
        tvBmWxTempMaxMin = findViewById(R.id.tv_bm_weather_max_min);
        tvGotoNow = findViewById(R.id.tv_now_weather_go);
        tvGoToFcstWeb = findViewById(R.id.tv_bm_weather_forecast);
    }

    private void decorCenter(){

        String today =getFutureDay("yyyyMMdd",0);

        tvBmWxDate.setText(today + " (" + getDateDay(today) + ")");

        tvBmWxPlace.setText(getGpsPosition());

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentHour = curHour.format(dateToday);

        ScheduleList scheduleList = new ScheduleList();

        scheduleList = getCurPlaceWxFromSp();

        //현재위치의 날씨 정보 가져와서 preference에 currentPlaceWx
        ScheduleData scheduleData = scheduleList.getScheduleArrayList().get(0).getScheduleData();

        currentHour=makingStrHour(currentHour);

        setNowWxCond(scheduleData, currentHour);

        //현재시간 출력하여 오전 오후 나누기

        //현재온도 적용
        //단기예보 response에서 현재 시간 비교해서 TMP 변수 클래스에 만들어서 넣어두기

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

}
