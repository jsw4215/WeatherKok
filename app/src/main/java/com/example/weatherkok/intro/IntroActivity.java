package com.example.weatherkok.intro;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.datalist.data.wxdata.Wx;
import com.example.weatherkok.main.MainActivity;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.NaverService;
import com.example.weatherkok.weather.NowWxActivity;
import com.example.weatherkok.weather.WeatherActivity;
import com.example.weatherkok.weather.WeatherService;
import com.example.weatherkok.weather.interfaces.NaverContract;
import com.example.weatherkok.weather.interfaces.WeatherContract;
import com.example.weatherkok.weather.models.LatLon;
import com.example.weatherkok.weather.models.midTemp.MidTempResponse;
import com.example.weatherkok.weather.models.midTemp.TempItem;
import com.example.weatherkok.weather.models.midTemp.TempItems;
import com.example.weatherkok.weather.models.midWx.WxItem;
import com.example.weatherkok.weather.models.midWx.WxItems;
import com.example.weatherkok.weather.models.midWx.WxResponse;
import com.example.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.example.weatherkok.weather.models.shortsExpectation.todayTemp;
import com.example.weatherkok.weather.models.shortsExpectation.todayWx;
import com.example.weatherkok.weather.models.xy;
import com.example.weatherkok.weather.utils.LatLonCalculator;
import com.example.weatherkok.weather.utils.NaverData;
import com.example.weatherkok.weather.utils.WxKokDataPresenter;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.when.utils.CalenderInfoPresenter;
import com.example.weatherkok.where.WhereActivity;
import com.example.weatherkok.where.utils.GpsTracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class IntroActivity extends BaseActivity implements WeatherContract.ActivityView, NaverContract {
    private static final String TAG = IntroActivity.class.getSimpleName();

    WxKokDataPresenter mWxKokDataPresenter;
    CalenderInfoPresenter mCal;
    ProgressDialog progressDoalog;
    GpsTracker mGpsTracker;
    Context mContext;
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ArrayList<String> mTargetPlaces = new ArrayList<>();
    ArrayList<String> mCodeList = new ArrayList<>();
    ArrayList<String> mTempCodeList = new ArrayList<>();
    String mToday;
    String mReqToday;
    ArrayList<xy> xyList;
    int mNumOfSchedule;
    //0이면 날씨콕, 1이면 즐겨찾기, 2이면 현재위치 날씨
    int mTypeOfData;
    boolean midChecker=true;
    boolean midTempChecker=true;
    boolean shortChecker=true;
    boolean finishChecker=false;
    int receiveCnt=0;
    boolean checkFirstStartApp=false;
    WhereActivity mWhereActivity;
    public static String SHARED_INTRO_CHECK = "introCheck";
    public static final String SHARED_IS_PERMISSION_FIRST = "isPermissionFirst";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = getBaseContext();
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        //권한 알림 팝업

        progressDoalog = new ProgressDialog(this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Its loading....");
        progressDoalog.setTitle("ProgressDialog Spinner example");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        setContentView(R.layout.activity_splash);
        mToday = calculateToday();

            boolean isPermissionPopupFirst = pref.getBoolean(SHARED_IS_PERMISSION_FIRST, false);
            if(isPermissionPopupFirst) {
                checkAppAuth("android.permission.RECORD_AUDIO", APP_PERMISSIONS_REQ_MIC);
            } else {
                showPermissionDialogView();
            }
    }

    private void startWhereApi() {
        mWhereActivity = new WhereActivity(mContext);
        checkFirstStartApp=getFirstStartCheck();
        mWhereActivity.setFirstTimeCheck(checkFirstStartApp);
        if(checkFirstStartApp) {
            mWhereActivity.firstConnectionWhereApi();
        }
    }

    private boolean getFirstStartCheck() {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        checkFirstStartApp = pref.getBoolean("firstStart",true);

        return checkFirstStartApp;
    }

    private void clearMonth() {

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
            editor.putString("202109", jsonString);


        editor.commit();


    }

    private void startSync() {
        Log.i(TAG, "start progress");
        progressDoalog.show();
        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "start progress2");
                        getScheduleDateWxApi();
                Log.i(TAG, "start progress3");
            }
        }, 100); // 1초 후에 hd handler 실행  3000ms = 3초
        Log.i(TAG, "start progress4");


    }

    private void startNowWx() {
        Log.i(TAG, "start progress");
        progressDoalog.show();
        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "start progress2");
                getCurrentPlaceWxApi();

                getNowWxApi();
                Log.i(TAG, "start progress3");
            }
        }, 100); // 1초 후에 hd handler 실행  3000ms = 3초
        Log.i(TAG, "start progress4");


    }

    private void initDummy() {
        mCal = new CalenderInfoPresenter(getApplication());
        mCal.makeDummySchedule();

    }

    //스케쥴장소별!! 날씨 데이터 단기예보 preference에 저장

    //1. 스케쥴을 검색해 장소를 가져온다.
    //2. 장소 기준으로 api를 연동한다.
    //3. 도착한 데이터를 리턴한다.

    public void getScheduleDateWxApi() {
        Log.i(TAG, "getScheduleDateWxApi");
        //주소 가져오기
        mTargetPlaces = getPlaceOfSchedule();
        //주소로 중기예보 지역 코드 가져오기
        mCodeList = manageDataForMidWxExpectation(mTargetPlaces);
        for(int i=0;i<mTargetPlaces.size();i++) {
            mTempCodeList.add(getTempPlaceCode(mTargetPlaces.get(i)));
        }


        mNumOfSchedule = mCodeList.size();

        //코드로 api를 이용해 데이터 가져오기

        mReqToday = mToday + "0600";

        mTypeOfData =0;

        if(mCodeList.size()>0) {
            midWxService(mCodeList.get(0), mReqToday, 0, mTypeOfData);
        } else {
            progressDoalog.dismiss();
            lastFunctionIntro();
        }
        //중기예보 데이터 도착 확인
        //중기예보의 retrofit null 아니면 그대로 보내는거 단기예보가 먼저 출발해버리면 흐트러질 수 있으니 나중에 생각해볼것


        Log.i(TAG, "fin.");

    }

    public void getCurrentPlaceWxApi(){
        Log.i(TAG, "getCurrentPlaceWxApi");

        String address = getGpsPosition();

        ArrayList<String> temp = new ArrayList<>();
        temp.add(address);

        mTypeOfData = 2;

        sendXyShortsCurrWx(temp, mTypeOfData);
    }

    public void getNowWxApi() {
        Log.i(TAG, "getNowWxApi");

        //주소 가져오기
        mTargetPlaces = getBookMarkPlace();

        if(mTargetPlaces.size()==0) {
            finishChecker=true;
            return;
        }
        //주소로 중기예보 지역 코드 가져오기
        mCodeList = manageDataForMidWxExpectation(mTargetPlaces);
        for(int i=0;i<mTargetPlaces.size();i++) {
            mTempCodeList.add(getTempPlaceCode(mTargetPlaces.get(i)));
        }

        //코드로 api를 이용해 데이터 가져오기

        mToday = calculateToday();

        mReqToday = mToday + "0600";
        mTypeOfData = 1;

        sendXyShortsCurrWx(mTargetPlaces, mTypeOfData);

        //중기예보 데이터 도착 확인
        //중기예보의 retrofit null 아니면 그대로 보내는거 단기예보가 먼저 출발해버리면 흐트러질 수 있으니 나중에 생각해볼것

        Log.i(TAG, "fin.");

    }

    private ArrayList<String> getBookMarkPlace() {

        ScheduleList scheduleList = new ScheduleList();
                scheduleList = getBmPlaceFromSp();
        ArrayList<String> place = new ArrayList<>();

        if(scheduleList!=null) {
            ArrayList<Schedule> list = scheduleList.getScheduleArrayList();

            for (int i = 0; i < list.size(); i++) {
                place.add(list.get(i).getWhere());
            }
        }
        return place;

    }

    private ScheduleList getBmPlaceFromSp() {

        //Preference에 날씨 정보 객체 불러오기

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null일 경우 처리할것.
        String loaded = pref.getString("bookMark", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference에 저장된 데이터 class 형태로 불러오기 완료

        scheduleList=deleteCheck(scheduleList);

        return scheduleList;
    }

    private ScheduleList deleteCheck(ScheduleList scheduleList){
        if(scheduleList!=null&&scheduleList.getScheduleArrayList()!=null
        &&scheduleList.getScheduleArrayList().size()>0) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().equals("delete")) {
                    scheduleList.getScheduleArrayList().remove(i);
                }
            }
        }
        return scheduleList;
    }

    //단기예보는 그때 그때 쏴야함

    private ArrayList<xy> getXYlist(ArrayList<String> mTargetPlaces, int type) {
        ArrayList<Integer> toNaver = new ArrayList<>();
        ArrayList<xy> temp = new ArrayList<>();
        xy tempObj = new xy();
        for (int i = 0; i < mTargetPlaces.size(); i++) {
            Map<String, Object> xyMap = getXYWithCalculator(mTargetPlaces.get(i));
            String nx = String.valueOf(xyMap.get("x"));
            Double tempX = Double.valueOf(nx);
            if(tempX<0){
                toNaver.add(i);
                continue;
            }
            String ny = String.valueOf(xyMap.get("y"));
            String lat = String.valueOf(xyMap.get("lat"));
            String lon = String.valueOf(xyMap.get("lng"));

            tempObj.setX(nx);
            tempObj.setY(ny);
            tempObj.setLon(lon);
            tempObj.setLat(lat);

            temp.add(tempObj);

        }
        //네이버 api 처리
        for(int i=0;i<toNaver.size();i++) {
            naverApi(mTargetPlaces.get(toNaver.get(i)),type,toNaver.get(i));
        }

        return temp;

    }

    private void sendXyShortsCurrWx(ArrayList<String> addresses, int type) {

        xyList = new ArrayList<xy>();


        xyList = getXYlist(addresses, type);

        //주소로 nx, ny 구하기
        //단기예보
        for (int i = 0; i < xyList.size(); i++) {
            shortWxService(calculateToday(), "0500", xyList.get(i), i, type);
        }
    }

    private void sendDateForShortsWx(int type) {

        xyList = new ArrayList<xy>();

        xyList = getXYlist(mTargetPlaces, type);

        //주소로 nx, ny 구하기
        //단기예보
        for (int i = 0; i < xyList.size(); i++) {
            shortWxService(calculateToday(), "0500", xyList.get(i), i, type);
        }
    }

    // 등록된 스케쥴에서 위치정보 가져와서
    // 도단위 시단위 찾아서
    // api연결
    private ArrayList<String> getPlaceOfSchedule() {

        ScheduleList scheduleList = getScheduleFromSp();
        ArrayList<String> place = new ArrayList<>();

        if(scheduleList!=null&&scheduleList.getScheduleArrayList()!=null) {
            ArrayList<Schedule> list = scheduleList.getScheduleArrayList();


            for (int i = 0; i < list.size(); i++) {
                place.add(list.get(i).getWhere());
            }
        }
        return place;
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

    private String getGpsPosition(){
        String result="";
        //현재위치 좌표 받아오기기
        double latitude  = 0;
        double longitude = 0;
        mGpsTracker = new GpsTracker(mContext);
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

    private void setScheduleDataInToSp(ScheduleList scheduleList,int type) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if(type==4) {
            Collections.sort(scheduleList.getScheduleArrayList());
        }

        scheduleList = removePastSchedule(scheduleList);

        Gson gson = new GsonBuilder().create();

        //Preference에 정보 객체 저장하기
        //JSON으로 변환
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //초기화
        //editor.remove(year + month);
        if(type==0) {
            editor.putString("schedule", jsonString);
        }else if (type==1){
            editor.putString("bookMark", jsonString);
        }else if(type==2){
            editor.putString("currentPlace", jsonString);
        }

        editor.commit();
        //저장완료
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


    //단기예보에서 최고기온, 최저기온 뽑아내는 알고리즘

    //단기예보에서 현재 날씨 오전, 오후로 결정짓는 알고리즘

    //중기예보 저장

    //중기기온 저장

    //현재위치의~ 반복 preference 저장


    //API연동

    private Map<String, Object> getXYWithCalculator(String s) {

        LatLonCalculator latLonCalculator = new LatLonCalculator();

        LatLon latLon = latLonCalculator.getLatLonWithAddr(s, mContext);

        Map<String, Object> result = latLonCalculator.getGridxy(latLon.getLat(), latLon.getLon());

        return result;
    }

    private void naverApi(String s, int type, int position) {

        String id = "stitoiw5z3";
        String secret = "U0T84fCEXuNS1BZ0K4NsckKRjpSWpeu95YKBTT3F";
        String address = s;

        NaverService naverService = new NaverService(this, mContext);
        naverService.getNaverGeo(id, secret, s, type, position);

    }

    private Date getToday() {
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private String manageAddress(String address) {

        List<String> splitAddr = Arrays.asList(address.split(" "));

        ArrayList<String> temp = new ArrayList<>();

        for (int i = 0; i < splitAddr.size(); i++) {
            temp.add(splitAddr.get(i));
        }

        temp.remove(temp.size() - 1);

        address = "";
        for (int i = 0; i < temp.size(); i++) {

            if (!(i == (temp.size() - 1))) {
                address = address + temp.get(i) + " ";
            } else {
                address = address + temp.get(i);
            }
        }
        return address;
    }

    private String calculateToday() {
        Date date = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentYear = curYearFormat.format(date);

        int temp = Integer.parseInt(curMonthFormat.format(date));
        String currentMonth = String.valueOf(temp);
        if (temp < 10) {
            currentMonth = "0" + currentMonth;
        }
        String currentDay = curDayFormat.format(date);
        //오늘일자에 저장
        //currentMonth="07";
        return currentYear + currentMonth + currentDay;
    }

    public static String getFutureDay(String pattern, int gap) {
        DateFormat dtf = new SimpleDateFormat(pattern);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, gap);
        return dtf.format(cal.getTime());
    }

    private void midWxService(String placeCode, String Date, int num, int type) {
        //중기예보
        //지역 정보 도단위, 시단위
        String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        int numOfRows = 10;
        int pageNo = 1;
        String dataType = "JSON";
        String regId = placeCode;
        String tmFc;
        tmFc = Date;
        regId = mCodeList.get(num);
        WeatherService weatherService = new WeatherService(this,mContext);
        weatherService.getMidLandFcst(key, numOfRows, pageNo, dataType, regId, tmFc, num, type);
    }

    private void shortWxService(String Date, String time, xy data, int position, int type) {
        //단기예보
        //지역 정보 도단위, 시단위
        //Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회) 10분뒤 api사용 가능
        String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        int numOfRows = 800;
        int pageNo = 1;
        String dataType = "JSON";
        String baseDate = Date;
        String baseTime = "0500";

        WeatherService weatherService = new WeatherService(this, mContext);
        weatherService.getShortFcst(key, numOfRows, pageNo, dataType, baseDate, time, data, position, type);
    }

    private void midTempService(String placeCode, String Date, int num, int type) {
        //단기예보
        //지역 정보 도단위, 시단위
        //Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회) 10분뒤 api사용 가능
        String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        int numOfRows = 10;
        int pageNo = 1;
        String dataType = "JSON";
        String regId = placeCode;
        String tmFc;
        tmFc = Date;
        regId = mTempCodeList.get(num);
        WeatherService weatherService = new WeatherService(this, mContext);
        weatherService.getMidTemp(key, numOfRows, pageNo, dataType, regId, tmFc, num, type);
    }

    private ArrayList<String> manageDataForMidWxExpectation(ArrayList<String> arrayList) {


        ArrayList<String> codeList = new ArrayList<>();

        for (int i = 0; i < arrayList.size(); i++) {
            codeList.add(getPlaceCode(arrayList.get(i)));
        }

        if(codeList.size()!=0) {
            Log.i(TAG, "code : " + codeList.get(0) + " " + arrayList.get(0));
            //Log.i(TAG, "code : " + codeList.get(1) + " " + arrayList.get(1));
        }

        return codeList;
    }

    private String getPlaceCode(String place) {

        String Code = "";

        if (place.startsWith("서울") || place.startsWith("인천") || place.startsWith("경기도")) {
            Code = "11B00000";
        } else if (place.startsWith("강원도")) {
            if(place.contains("고성")||place.contains("속초")||place.contains("양양")
                    ||place.contains("강릉")||place.contains("동해")||place.contains("삼척")||place.contains("태백"))
            {//영동
               Code = "11D20000";}else {
                //영서
                Code = "11D10000";
            }
        } else if (place.startsWith("대전") || place.startsWith("세종") || place.startsWith("충청남도")) {
            Code = "11C20000";
        } else if (place.startsWith("충청북도")) {
            Code = "11C10000";
        } else if (place.startsWith("광주") || place.startsWith("전라남도")) {
            Code = "11F20000";
        } else if (place.startsWith("전라북도")) {
            Code = "11F10000";
        } else if (place.startsWith("대구") || place.startsWith("경상북도")) {
            Code = "11H10000";
        } else if (place.startsWith("부산") || place.startsWith("울산") || place.startsWith("경상남도")) {
            Code = "11H20000";
        } else if (place.startsWith("제주")) {
            Code = "11G10000";
        }

        return Code;

    }

    private String getTempPlaceCode(String place) {

        String[] places = new String[] {"가평",	"강릉",	"강진",	"강화",	"거제",	"거창",	"경산",	"경주",	"계룡",	"고령",	"고산",	"고성",	"고양",	"고창",	"고흥",	"곡성",	"공주",	"과천",	"광명",	"광양",	"광주",	"괴산",	"구례",	"구리",	"구미",	"군산",	"군위",	"군포",	"금산",	"김제",	"김천",	"김포",	"김해",	"나주",	"남양주",	"남원",	"남해",	"논산",	"단양",	"담양",	"당진",	"대관령",	"대구",	"대전",	"독도",	"동두천",	"동해",	"목포",	"무안",	"무주",	"문경",	"밀양",	"백령도",	"보령",	"보성",	"보은",	"봉화",	"부산",	"부안",	"부여",	"부천",	"사천",	"산청",	"삼척",	"상주",	"서귀포",	"서산",	"서울",	"서천",	"성남",	"성산",	"성주",	"성판악",	"세종",	"속초",	"수원",	"순창",	"순천",	"시흥",	"신안",	"아산",	"안동",	"안산",	"안성",	"안양",	"양구",	"양산",	"양양",	"양주",	"양평",	"여수",	"여주",	"연천",	"영광",	"영덕",	"영동",	"영암",	"영양",	"영월",	"영주",	"영천",	"예산",	"예천",	"오산",	"옥천",	"완도",	"완주",	"용인",	"울릉도",	"울산",	"울진",	"원주",	"음성",	"의령",	"의성",	"의왕",	"의정부",	"이어도",	"이천",	"익산",	"인제",	"인천",	"임실",	"장성",	"장수",	"장흥",	"전주",	"정선",	"정읍",	"제주",	"제천",	"증평",	"진도",	"진안",	"진주",	"진천",	"창녕",	"창원",	"천안",	"철원",	"청도",	"청송",	"청양",	"청주",	"추자도",	"추풍령",	"춘천",	"충주",	"칠곡",	"태백",	"태안",	"통영",	"파주",	"평창",	"평택",	"포천",	"포항",	"하남",	"하동",	"함안",	"함양",	"함평",	"합천",	"해남",	"홍성",	"홍천",	"화성",	"화순",	"화천",	"횡성",	"흑산도"
        };

        ArrayList placeList = new ArrayList(Arrays.asList(places));

        String[] Codes = new String[] {
                "11A00101",	"11B10101",	"11B10102",	"11B10103",	"11B20101",	"11B20102",	"11B20201",	"11B20202",	"11B20203",	"11B20204",	"11B20301",	"11B20302",	"11B20304",	"11B20305",	"11B20401",	"11B20402",	"11B20403",	"11B20404",	"11B20501",	"11B20502",	"11B20503",	"11B20504",	"11B20601",	"11B20602",	"11B20603",	"11B20604",	"11B20605",	"11B20606",	"11B20609",	"11B20610",	"11B20611",	"11B20612",	"11B20701",	"11B20702",	"11B20703",	"11C10101",	"11C10102",	"11C10103",	"11C10201",	"11C10202",	"11C10301",	"11C10302",	"11C10303",	"11C10304",	"11C10401",	"11C10402",	"11C10403",	"11C20101",	"11C20102",	"11C20103",	"11C20104",	"11C20201",	"11C20202",	"11C20301",	"11C20302",	"11C20303",	"11C20401",	"11C20402",	"11C20403",	"11C20404",	"11C20501",	"11C20502",	"11C20601",	"11C20602",	"11D10101",	"11D10102",	"11D10201",	"11D10202",	"11D10301",	"11D10302",	"11D10401",	"11D10402",	"11D10501",	"11D10502",	"11D10503",	"11D20201",	"11D20301",	"11D20401",	"11D20402",	"11D20403",	"11D20501",	"11D20601",	"11D20602",	"11E00101",	"11E00102",	"11F10201",	"11F10202",	"11F10203",	"11F10204",	"11F10301",	"11F10302",	"11F10303",	"11F10401",	"11F10402",	"11F10403",	"11F20301",	"11F20302",	"11F20303",	"11F20304",	"11F20401",	"11F20402",	"11F20403",	"11F20404",	"11F20501",	"11F20502",	"11F20503",	"11F20504",	"11F20505",	"11F20601",	"11F20602",	"11F20603",	"11F20701",	"11G00101",	"11G00201",	"11G00302",	"11G00401",	"11G00501",	"11G00601",	"11G00800",	"11H10101",	"11H10102",	"11H10201",	"11H10202",	"11H10301",	"11H10302",	"11H10303",	"11H10401",	"11H10402",	"11H10403",	"11H10501",	"11H10502",	"11H10503",	"11H10601",	"11H10602",	"11H10603",	"11H10604",	"11H10605",	"11H10701",	"11H10702",	"11H10703",	"11H10704",	"11H10705",	"11H20101",	"11H20102",	"11H20201",	"11H20301",	"11H20304",	"11H20401",	"11H20402",	"11H20403",	"11H20404",	"11H20405",	"11H20501",	"11H20502",	"11H20503",	"11H20601",	"11H20602",	"11H20603",	"11H20604",	"11H20701",	"11H20703",	"11H20704",	"21F10501",	"21F10502",	"21F10601",	"21F10602",	"21F20101",	"21F20102",	"21F20201",	"21F20801",	"21F20802",	"21F20803",	"21F20804"
        };

        String Code ="";

        ArrayList codeList = new ArrayList(Arrays.asList(Codes));

        for(int i=0;i<placeList.size();i++) {
            if (place.contains((CharSequence) placeList.get(i))) {
                Code = String.valueOf(codeList.get(i));
            }
        }

        return Code;

    }

    private ScheduleList mergingWxDataAndSchedule(ScheduleList scheduleList, WxResponse wxResponse) {

        String code = wxResponse.getBody().getItems().getItem().get(0).getRegId();
        String place = "";

        if (code.equals("11B00000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("서울")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("인천")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("경기")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11D10000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("강원")
                        && (scheduleList.getScheduleArrayList().get(i).getWhere().contains("철원")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("화천")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("양구")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("춘천")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("홍천")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("횡성")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("원주")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("영월"))) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11D20000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("강원")
                        && (scheduleList.getScheduleArrayList().get(i).getWhere().contains("고성")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("인제")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("속초")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("양양")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("강릉")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("평창")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("정선")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("동해")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("태백")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("삼척"))) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //해당 위치에 날씨를 조사한 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11C20000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("대전")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("세종")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("충청남도")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11C10000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("충청북도")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11C20000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("광주")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("전라남도")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11F10000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("전라북도")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11H10000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("대구")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("경상북도")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11H20000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("부산")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("울산")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("경상남도")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11G10000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("제주")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        }

        return scheduleList;

    }

    private ScheduleList mergingMidTempAndSchedule(ScheduleList scheduleList, MidTempResponse midTempResponse) {

        String code = midTempResponse.getResponse().getBody().getItems().getItem().get(0).getRegId();
        String place = "";

        String[] places = new String[] {"가평",	"강릉",	"강진",	"강화",	"거제",	"거창",	"경산",	"경주",	"계룡",	"고령",	"고산",	"고성",	"고양",	"고창",	"고흥",	"곡성",	"공주",	"과천",	"광명",	"광양",	"광주",	"괴산",	"구례",	"구리",	"구미",	"군산",	"군위",	"군포",	"금산",	"김제",	"김천",	"김포",	"김해",	"나주",	"남양주",	"남원",	"남해",	"논산",	"단양",	"담양",	"당진",	"대관령",	"대구",	"대전",	"독도",	"동두천",	"동해",	"목포",	"무안",	"무주",	"문경",	"밀양",	"백령도",	"보령",	"보성",	"보은",	"봉화",	"부산",	"부안",	"부여",	"부천",	"사천",	"산청",	"삼척",	"상주",	"서귀포",	"서산",	"서울",	"서천",	"성남",	"성산",	"성주",	"성판악",	"세종",	"속초",	"수원",	"순창",	"순천",	"시흥",	"신안",	"아산",	"안동",	"안산",	"안성",	"안양",	"양구",	"양산",	"양양",	"양주",	"양평",	"여수",	"여주",	"연천",	"영광",	"영덕",	"영동",	"영암",	"영양",	"영월",	"영주",	"영천",	"예산",	"예천",	"오산",	"옥천",	"완도",	"완주",	"용인",	"울릉도",	"울산",	"울진",	"원주",	"음성",	"의령",	"의성",	"의왕",	"의정부",	"이어도",	"이천",	"익산",	"인제",	"인천",	"임실",	"장성",	"장수",	"장흥",	"전주",	"정선",	"정읍",	"제주",	"제천",	"증평",	"진도",	"진안",	"진주",	"진천",	"창녕",	"창원",	"천안",	"철원",	"청도",	"청송",	"청양",	"청주",	"추자도",	"추풍령",	"춘천",	"충주",	"칠곡",	"태백",	"태안",	"통영",	"파주",	"평창",	"평택",	"포천",	"포항",	"하남",	"하동",	"함안",	"함양",	"함평",	"합천",	"해남",	"홍성",	"홍천",	"화성",	"화순",	"화천",	"횡성",	"흑산도"
        };

        ArrayList placeList = new ArrayList(Arrays.asList(places));

        String[] Codes = new String[] {
                "11A00101",	"11B10101",	"11B10102",	"11B10103",	"11B20101",	"11B20102",	"11B20201",	"11B20202",	"11B20203",	"11B20204",	"11B20301",	"11B20302",	"11B20304",	"11B20305",	"11B20401",	"11B20402",	"11B20403",	"11B20404",	"11B20501",	"11B20502",	"11B20503",	"11B20504",	"11B20601",	"11B20602",	"11B20603",	"11B20604",	"11B20605",	"11B20606",	"11B20609",	"11B20610",	"11B20611",	"11B20612",	"11B20701",	"11B20702",	"11B20703",	"11C10101",	"11C10102",	"11C10103",	"11C10201",	"11C10202",	"11C10301",	"11C10302",	"11C10303",	"11C10304",	"11C10401",	"11C10402",	"11C10403",	"11C20101",	"11C20102",	"11C20103",	"11C20104",	"11C20201",	"11C20202",	"11C20301",	"11C20302",	"11C20303",	"11C20401",	"11C20402",	"11C20403",	"11C20404",	"11C20501",	"11C20502",	"11C20601",	"11C20602",	"11D10101",	"11D10102",	"11D10201",	"11D10202",	"11D10301",	"11D10302",	"11D10401",	"11D10402",	"11D10501",	"11D10502",	"11D10503",	"11D20201",	"11D20301",	"11D20401",	"11D20402",	"11D20403",	"11D20501",	"11D20601",	"11D20602",	"11E00101",	"11E00102",	"11F10201",	"11F10202",	"11F10203",	"11F10204",	"11F10301",	"11F10302",	"11F10303",	"11F10401",	"11F10402",	"11F10403",	"11F20301",	"11F20302",	"11F20303",	"11F20304",	"11F20401",	"11F20402",	"11F20403",	"11F20404",	"11F20501",	"11F20502",	"11F20503",	"11F20504",	"11F20505",	"11F20601",	"11F20602",	"11F20603",	"11F20701",	"11G00101",	"11G00201",	"11G00302",	"11G00401",	"11G00501",	"11G00601",	"11G00800",	"11H10101",	"11H10102",	"11H10201",	"11H10202",	"11H10301",	"11H10302",	"11H10303",	"11H10401",	"11H10402",	"11H10403",	"11H10501",	"11H10502",	"11H10503",	"11H10601",	"11H10602",	"11H10603",	"11H10604",	"11H10605",	"11H10701",	"11H10702",	"11H10703",	"11H10704",	"11H10705",	"11H20101",	"11H20102",	"11H20201",	"11H20301",	"11H20304",	"11H20401",	"11H20402",	"11H20403",	"11H20404",	"11H20405",	"11H20501",	"11H20502",	"11H20503",	"11H20601",	"11H20602",	"11H20603",	"11H20604",	"11H20701",	"11H20703",	"11H20704",	"21F10501",	"21F10502",	"21F10601",	"21F10602",	"21F20101",	"21F20102",	"21F20201",	"21F20801",	"21F20802",	"21F20803",	"21F20804"
        };

        String Code ="";

        ArrayList codeList = new ArrayList(Arrays.asList(Codes));

        for(int i=0;i<codeList.size();i++) {
            if (code.equals(codeList.get(i))) {
                for (int j = 0; j < scheduleList.getScheduleArrayList().size(); j++) {
                    if (scheduleList.getScheduleArrayList().get(j).getWhere().contains(String.valueOf(placeList.get(i)))) {
                        //해당위치에 날씨 삽입
                        scheduleList.getScheduleArrayList().get(j).getScheduleData().getFcst()
                                .setTempList(midTempResponse.getResponse().getBody().getItems());
                        //해당 위치에 오늘 날짜(기준) 삽입
                        scheduleList.getScheduleArrayList().get(j).getScheduleData().getFcst().setTempCheckDay(mToday);
                    }
                }
            }
        }

        return scheduleList;

    }

    //중기예보 날씨와 스케쥴 데이터를 병합하여 preference에 다시 저장
    private void midWxIntoPref(WxResponse wxResponse, int num, int type) {

        ScheduleList tempSchedule = new ScheduleList();

        if(type==0) {
            tempSchedule = getScheduleFromSp();
        }else if(type==1){
            tempSchedule = getBmPlaceFromSp();
        }

        //날씨정보와 중기예보 정보 합치기
        tempSchedule = mergingWxDataAndSchedule(tempSchedule, wxResponse);

        setScheduleDataInToSp(tempSchedule, type);

        if ((num + 1) < mCodeList.size()) {
            midWxService(mCodeList.get(num + 1), mReqToday, num + 1, type);
        } else {
            midTempService(mTempCodeList.get(0), mReqToday, 0, type);
        }
    }

    private void midTempIntoPref(MidTempResponse midTempResponse, int num, int type) {

        ScheduleList tempSchedule = new ScheduleList();

        if(type==0) {
            tempSchedule = getScheduleFromSp();
        }else if(type==1){
            tempSchedule = getBmPlaceFromSp();
        }

        //날씨정보와 중기예보 정보 합치기
        tempSchedule = mergingMidTempAndSchedule(tempSchedule, midTempResponse);

        setScheduleDataInToSp(tempSchedule, type);

        if ((num + 1) < mTempCodeList.size()) {
            midTempService(mTempCodeList.get(num + 1), mReqToday, num + 1, type);
        } else {
            //단기예보 데이터보내기
            sendDateForShortsWx(type);
        }

    }

    private String arrangeAddressResults(Address address) {

        String addr = "";
        ArrayList<String> temp = new ArrayList<>();

        temp.add(address.getAdminArea());
        temp.add(address.getSubAdminArea());
        temp.add(address.getLocality());
        temp.add(address.getSubLocality());
        if(!TextUtils.isEmpty(address.getThoroughfare())) {
            temp.add(address.getThoroughfare().substring(0, 2));
        }
        temp.removeAll(Arrays.asList("", null));

        for (int i = 0; i < temp.size(); i++) {
            if (!(i == temp.size() - 1)) {
                addr = addr + temp.get(i) + " ";
            } else {
                addr = addr + temp.get(i);
            }
        }

        return addr;
    }

    //중기예보 도착 3-10일 이후 예보
    @Override
    public void validateSuccess(boolean isSuccess, WxResponse wxResponse, int num, int type) {

        Log.i(TAG, "중기예보 성공 : " + wxResponse.getBody().getItems().getItem().get(0).getRegId());
        Log.i(TAG, "data도착 : " + wxResponse.getBody().getItems().getItem().get(0).getWf4Am());
        midWxIntoPref(wxResponse, num, type);

    }

    @Override
    public void validateShortSuccess(boolean isSuccess, ShortsResponse shortsResponse, String lat, String lon, int position, int type) {


        Log.i(TAG, "단기예보 도착!!" + shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstTime());
        //lat,lon을 가져오기

        //주소로 변환
        LatLonCalculator calculator = new LatLonCalculator();
        Address address = calculator.getAddressWithLatLon(lat, lon, mContext);
        String addrResult = arrangeAddressResults(address);

        receiveCnt++;

        setShortsDataIntoPref(shortsResponse, addrResult, position, type);

        //단기예보 로직 구현
        //데이터 집어넣기

    }

    private ScheduleData checkTempMaxMin(ShortsResponse shortsResponse, ScheduleData scheduleData) {
        //같은 날짜 애들 가져와서,

        String tomorrow = getFutureDay("yyyyMMdd", 1);
        String dayAfterTmr = getFutureDay("yyyyMMdd", 2);
        ArrayList<Integer> tempList = new ArrayList<>();
        ArrayList<Integer> tmrList = new ArrayList<>();
        ArrayList<Integer> datList = new ArrayList<>();
        ArrayList<todayTemp> todayTempList = new ArrayList<>();
        todayTemp todTmpObj = new todayTemp();


        for (int i = 0; i < shortsResponse.getResponse().getBody().getItems().getItem().size(); i++) {
            //오늘
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(mToday)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();
                int intTime = Integer.parseInt(time);
                //오전
                //if (intTime < 1200) {

                //오늘 오전의 기온
                if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("TMP")) {
                    todTmpObj.setTemperature(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue());
                    todTmpObj.setTimeHhmm(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime());
                    todayTempList.add(todTmpObj);
                    todTmpObj = new todayTemp();
                    tempList.add(Integer.valueOf(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));

                }
                //} else {
                //오늘 오후의 기온
                //}

            }

            //내일
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(tomorrow)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();

                //내일의 기온
                if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("TMP")) {

                    tmrList.add(Integer.valueOf(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));

                }

            }

            //모레
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(dayAfterTmr)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();

                //모레의 기온
                if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("TMP")) {

                    datList.add(Integer.valueOf(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));

                }

            }

        }
        //오늘의 기온 저장
        scheduleData.getFcst().setTempToday(todayTempList);
        //오늘 최대 최저기온 저장
        scheduleData = setMinMaxTemp(scheduleData, tempList, tmrList, datList);

        return scheduleData;

    }

    //3일간의 최대 최저기온 넣은 scheduleData
    private ScheduleData setMinMaxTemp(ScheduleData scheduleData, ArrayList<Integer> tempList, ArrayList<Integer> tmrList, ArrayList<Integer> datList) {

        int max = Collections.max(tempList);

        int min = Collections.min(tempList);

        TempItem item = new TempItem();

        item.setTaMax0(max);
        item.setTaMin0(min);

        max = Collections.max(tmrList);

        min = Collections.min(tmrList);

        item.setTaMax1(max);
        item.setTaMin1(min);

        max = Collections.max(datList);

        min = Collections.min(datList);

        item.setTaMax2(max);
        item.setTaMin2(min);

        scheduleData.getFcst().getTempList().getItem().add(item);

        return scheduleData;

    }

    private void setShortsDataIntoPref(ShortsResponse shortsResponse, String addrResult, int position, int type) {

        //단기예보 기온 최대, 최소 구하기 + 오늘의 기온 저장
        ScheduleData scheduleData = new ScheduleData();

        scheduleData = checkTempMaxMin(shortsResponse, scheduleData);

        //날씨 로직 돌리기
        scheduleData = checkWxShorts(shortsResponse, scheduleData);

        //response를 리스트의 position 매칭 후 삽입
        ScheduleList scheduleList = new ScheduleList();
        //null방지 initialize
        scheduleList = initialzeSl(scheduleList);

        if(type==0) {
            scheduleList = getScheduleFromSp();
        }else if(type==1){
            scheduleList = getBmPlaceFromSp();
        }

        scheduleList = matchingAdderess(scheduleList, addrResult, scheduleData, position);

        setScheduleDataInToSp(scheduleList, type);
        if(type==0||mTargetPlaces.size()==0) {
            if (receiveCnt >= mTargetPlaces.size()) {
                lastFunctionIntro();
            }
        }else{
            if(receiveCnt>mTargetPlaces.size()){
                lastFunctionIntro();
            }
        }
    }

    private ScheduleList initialzeSl(ScheduleList scheduleList) {

        ArrayList<Schedule> temp = new ArrayList<>();
        TempItems temp2 = new TempItems();
        WxItems temp3 = new WxItems();
        TempItem temp4= new TempItem();
        WxItem temp5 = new WxItem();
        Schedule schedule = new Schedule();
        scheduleList.setScheduleArrayList(temp);
        schedule.getScheduleData().getFcst().setTempList(temp2);
        schedule.getScheduleData().getFcst().setWxList(temp3);
        schedule.getScheduleData().getFcst().getTempList().getItem().add(temp4);
        schedule.getScheduleData().getFcst().getWxList().getItem().add(temp5);
        return scheduleList;
    }

    //스케쥴과 날씨 데이터를 주소 장소로 매칭하여 정보를 집어 넣는다.
    private ScheduleList matchingAdderess(ScheduleList scheduleList, String addrResult, ScheduleData scheduleData, int i) {


        TempItem dum = new TempItem();
        WxItem dum2 = new WxItem();
        TempItems dum3 = new TempItems();
        WxItems dum4 = new WxItems();


        if(scheduleList.getScheduleArrayList()!=null&&!(scheduleList.getScheduleArrayList().size()==0)) {
            //스케쥴의 주소가 데이터의 주소를 포함한다면~ 으로 알고리즘 선택
            Log.i(TAG, "matchingPosition Algo");
            //기상청이 하루에 두번 정보를 제공하므로 그시간에 돌아가게 만들면 되니, 여기서 중복체크 알고리즘을 할 필요는 없다. 아침 6시 저녁 18시
            //리스트에 데이터 추가, 기존의 데이터가 있으니 하나하나 집어넣어 줘야함.

            //여기에 get(0) 진입이 어려우므로 더미를 깔아준다. 불안정하니 나중에 다시 이해해볼것
            if (scheduleList.getScheduleArrayList().get(i) == null) {
                scheduleList.getScheduleArrayList().get(i).setScheduleData(scheduleData);
            }else {
                //현재날씨 널처리
                if (scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList() == null) {
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempList(scheduleData.getFcst().getTempList());
                } else if (scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem() == null ||
                        scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().size() == 0) {
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().add(scheduleData.getFcst().getTempList().getItem().get(0));
                }

                if (scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList() == null) {
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(scheduleData.getFcst().getWxList());
                } else if (scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem() == null ||
                        scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().size() == 0) {
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().add(scheduleData.getFcst().getWxList().getItem().get(0));
                }

                if (scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxToday() == null
                        || scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxToday().size() == 0) {
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxToday(scheduleData.getFcst().getWxToday());
                }

                //중기예보를 들고 들어올 경우 합집합을 위해 수작업으로 넣어줘야한다.
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMin0(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMin0()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMax0(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMax0()
                );
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMin1(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMin1()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMax1(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMax1()
                );
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMin2(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMin2()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMax2(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMax2()
                );

                //날씨정보세팅
                //강수타입
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt0AmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt0AmType()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt0PmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt0PmType()
                );

                //강수확률
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt0Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt0Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt0Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt0Pm()
                );

                //날씨정보
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setWf0Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getWf0Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setWf0Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm()
                );

                //강수타입
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt1AmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1AmType()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt1PmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1PmType()
                );

                //강수확률
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt1Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt1Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()
                );

                //날씨정보
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setWf1Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setWf1Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm()
                );

                //강수타입
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt2AmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2AmType()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt2PmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2PmType()
                );

                //강수확률
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt2Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt2Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()
                );

                //날씨정보
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setWf2Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getWf2Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setWf2Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm()
                );
            }
        }else {
            scheduleData.setPlace(addrResult);
            Schedule schedule = new Schedule();
            schedule.setScheduleData(scheduleData);
            scheduleList.getScheduleArrayList().add(schedule);
        }
        return scheduleList;
    }

    private ScheduleData checkWxShorts(ShortsResponse shortsResponse, ScheduleData scheduleData) {

        String tomorrow = getFutureDay("yyyyMMdd", 1);
        String dayAfterTmr = getFutureDay("yyyyMMdd", 2);
        ArrayList<Integer> tmrList = new ArrayList<>();
        ArrayList<Integer> datList = new ArrayList<>();

        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)

        scheduleData = shortsWxAlgorithm(shortsResponse, scheduleData, mToday);

        scheduleData = shortsWxTmrAlgorithm(shortsResponse, scheduleData, tomorrow);

        scheduleData = shortsWxDatAlgorithm(shortsResponse, scheduleData, dayAfterTmr);

        return scheduleData;


    }

    public ScheduleData shortsWxAlgorithm(ShortsResponse shortsResponse, ScheduleData scheduleData, String date) {

        WxItem dum = new WxItem();
        ArrayList<todayWx> todayWxList = new ArrayList<>();
        todayWx temp = new todayWx();
        scheduleData.getFcst().getWxList().getItem().add(dum);
        boolean rainPercent=false;
        boolean raintype=false;
        boolean sky=false;

        for (int i = 0; i < shortsResponse.getResponse().getBody().getItems().getItem().size(); i++) {
            //오늘
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(date)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();

                temp.setTimeHhmm(time);

                int intTime = Integer.parseInt(time);
                //오전
                if (intTime < 1200) {

                    //오늘 오전의 강수확률, 하늘상태, 강수형태
                    //todayWx = 1 : 강수확률, 2: 하늘상태, 3: 강수형태
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //강수확률 = 강수확률 rnst에 저장
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt0Am(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                                //오늘의 날씨를 시간별로 강수형태,확률, 하늘상태를 한 temp에 담아두는 로직
                                temp.setRainPercent(Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                                temp.setType(1);
                                rainPercent=true;
                                if(rainPercent&&raintype&&sky) {
                                    todayWxList.add(temp);
                                    temp = new todayWx();
                                    rainPercent=false;
                                    raintype=false;
                                    sky=false;
                                }
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //강수타입 = type으로 따로 저장
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt0AmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                        temp.setRainType(Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                        temp.setType(3);
                        raintype=true;
                        if(rainPercent&&raintype&&sky) {
                            todayWxList.add(temp);
                            temp = new todayWx();
                            rainPercent=false;
                            raintype=false;
                            sky=false;
                        }
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        temp.setWeather(Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                        temp.setType(2);
                        sky=true;
                        if(rainPercent&&raintype&&sky) {
                            todayWxList.add(temp);
                            temp = new todayWx();
                            rainPercent=false;
                            raintype=false;
                            sky=false;
                        }
                        //하늘상태 = 중기예보 날씨정보
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Am("구름많음");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Am("흐림");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Am("맑음");
                        }
                    }

                } else {
                    //오후
                    //강수확률, 하늘상태, 강수형태
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //강수확률
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt0Pm(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                        temp.setRainPercent(Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                        temp.setType(1);
                        rainPercent=true;
                        if(rainPercent&&raintype&&sky) {
                            todayWxList.add(temp);
                            temp = new todayWx();
                            rainPercent=false;
                            raintype=false;
                            sky=false;
                        }
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //강수타입
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt0PmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                        temp.setRainType(Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                        temp.setType(3);
                        raintype=true;
                        if(rainPercent&&raintype&&sky) {
                            todayWxList.add(temp);
                            temp = new todayWx();
                            rainPercent=false;
                            raintype=false;
                            sky=false;
                        }
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        temp.setWeather(Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                        temp.setType(2);
                        sky=true;
                        if(rainPercent&&raintype&&sky) {
                            todayWxList.add(temp);
                            temp = new todayWx();
                            rainPercent=false;
                            raintype=false;
                            sky=false;
                        }
                        //하늘상태 = 중기예보 날씨정보
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Pm("구름많음");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Pm("흐림");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Pm("맑음");
                        }
                    }
                }
            }
            //클래스 초기화 하고

        }

        scheduleData.getFcst().setWxToday(todayWxList);

        return scheduleData;
    }

    public ScheduleData shortsWxTmrAlgorithm(ShortsResponse shortsResponse, ScheduleData scheduleData, String date) {

        WxItem dum = new WxItem();
        scheduleData.getFcst().getWxList().getItem().add(dum);

        for (int i = 0; i < shortsResponse.getResponse().getBody().getItems().getItem().size(); i++) {
            //오늘
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(date)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();
                int intTime = Integer.parseInt(time);
                //오전
                if (intTime < 1200) {

                    //오늘 오전의 강수확률, 하늘상태, 강수형태
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //강수확률 = 강수확률 rnst에 저장
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt1Am(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //강수타입 = type으로 따로 저장
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt1AmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        //하늘상태 = 중기예보 날씨정보
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Am("구름많음");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Am("흐림");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Am("맑음");
                        }
                    }

                } else {
                    //오늘 오후의 기온
                    //오늘 오전의 강수확률, 하늘상태, 강수형태
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //강수확률
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt1Pm(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //강수타입
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt1PmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        //하늘상태 = 중기예보 날씨정보
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Pm("구름많음");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Pm("흐림");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Pm("맑음");
                        }
                    }
                }
            }

        }
        return scheduleData;
    }

    public ScheduleData shortsWxDatAlgorithm(ShortsResponse shortsResponse, ScheduleData scheduleData, String date) {

        WxItem dum = new WxItem();
        scheduleData.getFcst().getWxList().getItem().add(dum);

        for (int i = 0; i < shortsResponse.getResponse().getBody().getItems().getItem().size(); i++) {
            //오늘
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(date)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();
                int intTime = Integer.parseInt(time);
                //오전
                if (intTime < 1200) {

                    //오늘 오전의 강수확률, 하늘상태, 강수형태
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //강수확률 = 강수확률 rnst에 저장
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt2Am(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //강수타입 = type으로 따로 저장
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt2AmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        //하늘상태 = 중기예보 날씨정보
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Am("구름많음");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Am("흐림");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Am("맑음");
                        }
                    }

                } else {
                    //오늘 오후의 기온
                    //오늘 오전의 강수확률, 하늘상태, 강수형태
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //강수확률
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt2Pm(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //강수타입
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt2PmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        //하늘상태 = 중기예보 날씨정보
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Pm("구름많음");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Pm("흐림");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Pm("맑음");
                        }
                    }
                }
            }

        }
        return scheduleData;
    }


    @Override
    public void validateFailure(String message, String regId, int num, int type) {

        Log.e(TAG, "중기예보 실패");
        //코드로 api를 이용해 데이터 가져오기
        boolean checker = checkAMPM();
        //오후면 오늘꺼로 다시 시도 오전이면 밤 12시 넘어가서 그런 것이니 어제 저녁으로 시도
        //midChecker를 이용하여 통신 실패시 번갈아가며 오전 오후 시도
        if(midChecker) {
            Log.e(TAG, "중기예보 다시시도1"+midChecker);
            midWxService(regId, getFutureDay("yyyyMMdd", 0) + "0600", num, type);
            midChecker=false;
        }else{
            if (checker) {
                Log.e(TAG, "중기예보 다시시도2"+midChecker);
                midWxService(regId, getFutureDay("yyyyMMdd", 0) + "1800", num, type);
                midChecker=true;
            } else {
                Log.e(TAG, "중기예보 다시시도3"+midChecker);
                midWxService(regId, getFutureDay("yyyyMMdd", -1) + "1800", num, type);
            }
        }
    }

    @Override
    public void validateShortFailure(String message, xy data, int position, int type) {
        Log.e(TAG, "단기예보 실패 키 : " + position);
        boolean checker = checkAMPM();
        //오후면 오늘꺼로 다시 시도 오전이면 밤 12시 넘어가서 그런 것이니 어제 저녁으로 시도
        if(shortChecker){
            shortWxService(getFutureDay("yyyyMMdd", 0), "0500", data, position, type);
            shortChecker=false;
        }
        if (checker) {
            shortWxService(getFutureDay("yyyyMMdd", 0), "1700", data, position, type);
            shortChecker=true;
        } else {
            shortWxService(getFutureDay("yyyyMMdd", -1), "1700", data, position, type);
        }
    }

    @Override
    public void validateMidTempSuccess(MidTempResponse midTempResponse, int num, int type) {

        Log.e(TAG, "중기기온 성공");
        midTempIntoPref(midTempResponse, num, type);
    }

    @Override
    public void validateMidTempFailure(String message, String regId, int num, int type) {
        Log.e(TAG, "중기기온 실패");
        boolean checker = checkAMPM();
        //오후면 오늘꺼로 다시 시도 오전이면 밤 12시 넘어가서 그런 것이니 어제 저녁으로 시도
        if(midTempChecker) {
            Log.e(TAG, "중기기온 다시시도1"+midTempChecker);
            midWxService(regId, getFutureDay("yyyyMMdd", 0) + "0600", num, type);
            midTempChecker=false;
        }else{
            if (checker) {
                Log.e(TAG, "중기기온 다시시도2"+midTempChecker);

                midTempService(regId, getFutureDay("yyyyMMdd", 0) + "1800", num, type);
                midTempChecker = true;
            } else {
                Log.e(TAG, "중기기온 다시시도3"+midTempChecker);

                midTempService(regId, getFutureDay("yyyyMMdd", -1) + "1800", num, type);
            }
        }
    }

    private boolean checkAMPM() {

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        //현재시간 출력하여 오전 오후 나누기
        if (inthour < 12||inthour==24) {
            //오전
            return false;
        } else {
            //오후
            return true;
        }

    }

    public void lastFunctionIntro() {
        Log.i(TAG, "인트로 api 마무리");
        progressDoalog.dismiss();
        Intent intent = getIntent();
        String from = "";
        receiveCnt=0;
        if(!TextUtils.isEmpty(intent.getStringExtra("from"))){
            from = intent.getStringExtra("from");
        }

        if(from.equals("goToWeather")||mNumOfSchedule>0){
            startActivity(new Intent(getApplication(), WeatherActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            IntroActivity.this.finish();
        }
        else if(from.equals("goToNow")){
            startActivity(new Intent(getApplication(), NowWxActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            IntroActivity.this.finish();
        }
        else{
            startActivity(new Intent(getApplication(), MainActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            IntroActivity.this.finish();
        }

    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

    private void clearSchedule(int type) {

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
        if(type==0) {
            editor.putString("schedule", jsonString);
        }else if (type==1){
            editor.putString("bookMark", jsonString);
        }else if(type==2){
            editor.putString("currentPlace", jsonString);
        }

        editor.commit();
        //저장완료
    }

    private void goAppStart(){

        startWhereApi();

        //initDummy();

        Intent intent = getIntent();
        if(!TextUtils.isEmpty(intent.getStringExtra("from"))){

            if(intent.getStringExtra("from").equals("goToNow")){

                startNowWx();

            }

            if(intent.getStringExtra("from").equals("goToWeather")){

                startSync();

            }

        }else{
            //clearSchedule(0);
            //clearMonth();
            startSync();
        }

    }

    private final static int APP_PERMISSIONS_REQ_MIC = 1000;
    private final static int APP_PERMISSIONS_REQ_STORAGE = 1100;
    private final static int APP_PERMISSIONS_REQ_PHONE = 1200;
    private final static int ACCESS_COARSE_LOCATION = 1300;
    private final static int ACCESS_FINE_LOCATION = 1400;
    private final static int SEND_SMS = 1500;
    private final static int RECEIVE_SMS = 1600;

    private void checkAppAuth(final String a_PermissionReq, final int a_ReqCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionResult = checkSelfPermission(a_PermissionReq);

            if (permissionResult == PackageManager.PERMISSION_DENIED) {

                //사용자가 CALL_PHONE 권한을 한번이라도 거부한 적이 있는 지 조사한다.
                //거부한 이력이 한번이라도 있다면, true를 리턴한다.
                //최초로 권한을 요청할 때 권한을 Android OS 에 요청한다.
                if (shouldShowRequestPermissionRationale(a_PermissionReq)) {
                    requestPermissions(new String[]{a_PermissionReq}, a_ReqCode);
                } else {
                    //최초로 권한을 요청할 때 권한을 Android OS 에 요청한다.
                    requestPermissions(new String[]{a_PermissionReq}, a_ReqCode);
                }

            } else {
                //권한이 있을 때
                doAfterAuthCheck(a_ReqCode);
            }

        } else {
            //앱 시작
            goAppStart();
        }
    }

    /**
     * doAfterAuthCheck method
     *
     * checking one of the permissions has permitted.
     * @param a_ReqCode
     */
    private void doAfterAuthCheck(int a_ReqCode) {
        if(a_ReqCode == 1) {
            checkAppAuth("android.permission.RECORD_AUDIO", APP_PERMISSIONS_REQ_MIC);
        } else if (a_ReqCode == APP_PERMISSIONS_REQ_MIC) {
            checkAppAuth("android.permission.READ_PHONE_STATE", APP_PERMISSIONS_REQ_PHONE);
        } else if (a_ReqCode == APP_PERMISSIONS_REQ_PHONE) {
            checkAppAuth("android.permission.ACCESS_FINE_LOCATION", ACCESS_FINE_LOCATION);
        } else if (a_ReqCode == ACCESS_FINE_LOCATION) {
            checkAppAuth("android.permission.ACCESS_COARSE_LOCATION", ACCESS_COARSE_LOCATION);
        } else if (a_ReqCode == ACCESS_COARSE_LOCATION){
            checkAppAuth("android.permission.SEND_SMS", SEND_SMS);
        } else if (a_ReqCode == SEND_SMS){
            checkAppAuth("android.permission.RECEIVE_SMS", RECEIVE_SMS);
        } else if (a_ReqCode == RECEIVE_SMS) {
            goAppStart();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission is granted.
            doAfterAuthCheck(requestCode);
        } else {
            // 권한 거부 : 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
            finish();
        }
    }

    private void showPermissionDialogView() {
        //Dialog 생성
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View) inflater.inflate(R.layout.permission_notify_popup_view, null);
        final Dialog dialog = DialogView.getDefaultDialog(this, view);

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //ok click event
        TextView tv_ok = view.findViewById(R.id.tv_ok);

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putBoolean(SHARED_IS_PERMISSION_FIRST, true);
                editor.apply();

                checkAppAuth("android.permission.RECORD_AUDIO", APP_PERMISSIONS_REQ_MIC);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void naverSuccess(boolean isSuccess, NaverData naverData,int type, int position) {
        LatLon resultNaver = new LatLon();
        //두개가 바뀌어있다.
        resultNaver.setLat(Double.parseDouble(naverData.getAddresses().get(0).getY()));
        resultNaver.setLon(Double.parseDouble(naverData.getAddresses().get(0).getX()));

        LatLonCalculator latLonCalculator = new LatLonCalculator();

        Map<String, Object> xyMap = latLonCalculator.getGridxy(resultNaver.getLat(), resultNaver.getLon());

        xy tempObj = new xy();
        String nx = String.valueOf(xyMap.get("x"));
        String ny = String.valueOf(xyMap.get("y"));
        String lat = String.valueOf(xyMap.get("lat"));
        String lon = String.valueOf(xyMap.get("lng"));

        tempObj.setX(nx);
        tempObj.setY(ny);
        tempObj.setLon(lon);
        tempObj.setLat(lat);

        //주소로 nx, ny 구하기
        //단기예보
            shortWxService(calculateToday(), "0500",tempObj, position, type);


    }

    @Override
    public void naverFailure(String message) {
        Log.i(TAG, "naverFailure");
    }
}
