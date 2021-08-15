package com.example.weatherkok.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.weatherkok.R;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.interfaces.WeatherContract;
import com.example.weatherkok.weather.models.LatLon;
import com.example.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.example.weatherkok.weather.models.midTemp.MidTempResponse;
import com.example.weatherkok.weather.models.midWx.WxResponse;
import com.example.weatherkok.weather.utils.LatLonCalculator;
import com.example.weatherkok.weather.utils.WxKokDataPresenter;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class WeatherActivity extends BaseActivity{
    String TAG = "WeatherActivity";
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ArrayList<String> mTargetPlaces = new ArrayList<>();
    ArrayList<String> mCodeList = new ArrayList<>();
    WxKokDataPresenter mWxKokDataPresenter;

    //날씨 정보를 얻는 서비스를 시작하고, 받아와서 정리된 데이터를 가져와 뿌리는 역할만 하는 곳

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_weather);

        Log.i(TAG, "weather");

        mWxKokDataPresenter = new WxKokDataPresenter(getBaseContext());

        mWxKokDataPresenter.getScheduleDateWxApi();

//        //주소 가져오기
//        mTargetPlaces = getPlaceOfSchedule();
//        //주소로 중기예보 지역 코드 가져오기
//        mCodeList = manageDataForMidWxExpectation(mTargetPlaces);
//        //코드로 api를 이용해 데이터 가져오기
//        midWxService(mCodeList.get(1), calculateToday()+"0600");
//        //중기예보 데이터 도착 확인
//
//        //중기 기온
//        midTempService(mCodeList.get(1), calculateToday()+"0600");
//
//
//        Map<String, Object> xy = getXYWithCalculator(mTargetPlaces.get(0));
//
//
//        String nx = String.valueOf(xy.get("x"));
//        String ny = String.valueOf(xy.get("y"));
//        //주소로 nx, ny 구하기
//        //단기예보
//        shortWxService(calculateToday(), nx, ny);


    }

//    private Map<String, Object> getXYWithCalculator(String s) {
//
//        LatLonCalculator latLonCalculator = new LatLonCalculator();
//
//        LatLon latLon = latLonCalculator.getLatLonWithAddr(mTargetPlaces.get(0),getBaseContext());
//
//        Map<String, Object> result = latLonCalculator.getGridxy(latLon.getLat(), latLon.getLon());
//
//        return result;
//    }
//
//    // 등록된 스케쥴에서 위치정보 가져와서
//    // 도단위 시단위 찾아서
//    // api연결
//    private ArrayList<String> getPlaceOfSchedule(){
//
//        ScheduleList scheduleList = getScheduleFromPreference();
//
//        ArrayList<Schedule> list = scheduleList.getScheduleArrayList();
//
//        ArrayList<String> place = new ArrayList<>();
//
//        for(int i =0;i<list.size();i++){
//            place.add(list.get(i).getWhere());
//        }
//
//        return place;
//    }
//
//    private Date getToday(){
//        // 오늘에 날짜를 세팅 해준다.
//        long now = System.currentTimeMillis();
//        final Date date = new Date(now);
//
//        return date;
//    }
//
//    private String calculateToday(){
//        Date date = getToday();
//
//        //연,월,일을 따로 저장
//        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
//        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
//        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);
//
//        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
//        String currentYear = curYearFormat.format(date);
//
//        int temp = Integer.parseInt(curMonthFormat.format(date));
//        String currentMonth = String.valueOf(temp);
//        if(temp<10) {
//            currentMonth = "0"+currentMonth;
//        }
//        String currentDay = curDayFormat.format(date);
//        //오늘일자에 저장
//        //currentMonth="07";
//       return currentYear+currentMonth+currentDay;
//    }
//
//    private void midWxService(String placeCode, String Date){
//        //중기예보
//        //지역 정보 도단위, 시단위
//        String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
//        int numOfRows = 10;
//        int pageNo = 1;
//        String dataType = "JSON";
//        String regId = placeCode;
//        String tmFc;
//        tmFc = Date;
//
//        WeatherService weatherService = new WeatherService(this);
//        weatherService.getMidLandFcst(key, numOfRows, pageNo, dataType, regId, tmFc);
//    }
//
//
//
//    private void midTempService(String placeCode, String Date){
//        //단기예보
//        //지역 정보 도단위, 시단위
//        //Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회) 10분뒤 api사용 가능
//        String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
//        int numOfRows = 10;
//        int pageNo = 1;
//        String dataType = "JSON";
//        String regId = placeCode;
//        String tmFc;
//        tmFc = Date;
//
//        WeatherService weatherService = new WeatherService(this);
//        weatherService.getMidTemp(key, numOfRows, pageNo, dataType, regId, tmFc);
//    }
//
//    private ArrayList<String> manageDataForMidWxExpectation(ArrayList<String> arrayList){
//
//
//        ArrayList<String> codeList = new ArrayList<>();
//
//        for(int i =0;i<arrayList.size();i++) {
//            codeList.add(getPlaceCode(arrayList.get(i)));
//        }
//
//        Log.i(TAG, "code : " + codeList.get(0) + " " + arrayList.get(0));
//        Log.i(TAG, "code : " + codeList.get(1) + " " + arrayList.get(1));
//
//
//        return codeList;
//    }
//
//    private String getPlaceCode(String place) {
//
//        String Code = "";
//
//        if(place.startsWith("서울")||place.startsWith("인천")||place.startsWith("경기도")){
//            Code = "11B00000";
//        } else if(place.startsWith("강원도영서")){
//            Code = "11D10000";
//        } else if(place.startsWith("강원도영동")){
//            Code = "11D20000";
//        } else if(place.startsWith("대전")||place.startsWith("세종")||place.startsWith("충청남도")){
//            Code = "11C20000";
//        } else if(place.startsWith("충청북도")){
//            Code = "11C10000";
//        } else if(place.startsWith("광주")||place.startsWith("전라남도")){
//            Code = "11F20000";
//        } else if(place.startsWith("전라북도")){
//            Code = "11F10000";
//        } else if(place.startsWith("대구")||place.startsWith("경상북도")){
//            Code = "11H10000";
//        } else if(place.startsWith("부산")||place.startsWith("울산")||place.startsWith("경상남도")){
//            Code = "11H20000";
//        }  else if(place.startsWith("제주")){
//            Code = "11G10000";
//        }
//
//        return Code;
//
//    }
//
//    private void getMidTemp(){
//
//        //중기기온 API
//
//    }
//
//    private void getShorts(){
//
//        //단기예보 API
//
//    }
//
//    private ScheduleList getScheduleFromPreference(){
//
//        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
//
//        //Preference에서 날씨 정보 객체 불러오기
//        Gson gson = new GsonBuilder().create();
//        String loaded = pref.getString("schedule","");
//
//        ScheduleList loadedFromSP = gson.fromJson(loaded, ScheduleList.class);
//
//        return loadedFromSP;
//    }
//    //중기예보 도착 3-10일 이후 예보
//    @Override
//    public void validateSuccess(boolean isSuccess, WxResponse wxResponse) {
//
//            Log.i(TAG, "data도착 : " + wxResponse.getBody().getItems().getItem().get(0).getRegId());
//            Log.i(TAG, "data도착 : " + wxResponse.getBody().getItems().getItem().get(0).getWf4Am());
//
//    }
//
//    //오늘부터 내일모레까지 예보
//    @Override
//    public void validateShortSuccess(boolean isSuccess, ShortsResponse shortsResponse,int nx, int ny) {
//
//        Log.i(TAG, "단기예보 도착!!" + shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstTime());
//
//
//
//    }
//
//
//    @Override
//    public void validateFailure(String message) {
//
//        Log.e(TAG, "중기예보 실패");
//        //코드로 api를 이용해 데이터 가져오기
//        midWxService(mCodeList.get(1), calculateToday()+"1800");
//    }
//
//    @Override
//    public void validateShortFailure(String message) {
//        Log.e(TAG, "단기예보 실패");
//
//
//    }
//
//    @Override
//    public void validateMidTempSuccess(MidTempResponse midTempResponse) {
//        Log.e(TAG, "중기기온 성공");
//    }
//
//    @Override
//    public void validateMidTempFailure(String message) {
//        Log.e(TAG, "중기기온 실패");
//    }

}