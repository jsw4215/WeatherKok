package com.devpilot.weatherkok.intro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.datalist.data.ScheduleData;
import com.devpilot.weatherkok.intro.eventBus.eventBus;
import com.devpilot.weatherkok.main.MainActivity;
import com.devpilot.weatherkok.src.BaseActivity;
import com.devpilot.weatherkok.weather.NaverService;
import com.devpilot.weatherkok.weather.NowWxActivity;
import com.devpilot.weatherkok.weather.WeatherActivity;
import com.devpilot.weatherkok.weather.WeatherService;
import com.devpilot.weatherkok.weather.interfaces.NaverContract;
import com.devpilot.weatherkok.weather.interfaces.WeatherContract;
import com.devpilot.weatherkok.weather.models.LatLon;
import com.devpilot.weatherkok.weather.models.midTemp.MidTempResponse;
import com.devpilot.weatherkok.weather.models.midTemp.TempItem;
import com.devpilot.weatherkok.weather.models.midTemp.TempItems;
import com.devpilot.weatherkok.weather.models.midWx.WxItem;
import com.devpilot.weatherkok.weather.models.midWx.WxItems;
import com.devpilot.weatherkok.weather.models.midWx.WxResponse;
import com.devpilot.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.devpilot.weatherkok.weather.models.shortsExpectation.todayTemp;
import com.devpilot.weatherkok.weather.models.shortsExpectation.todayWx;
import com.devpilot.weatherkok.weather.models.xy;
import com.devpilot.weatherkok.weather.utils.LatLonCalculator;
import com.devpilot.weatherkok.weather.utils.NaverData;
import com.devpilot.weatherkok.weather.utils.WxKokDataPresenter;
import com.devpilot.weatherkok.when.models.Schedule;
import com.devpilot.weatherkok.when.models.ScheduleList;
import com.devpilot.weatherkok.when.utils.CalenderInfoPresenter;
import com.devpilot.weatherkok.where.WhereActivity;
import com.devpilot.weatherkok.where.utils.GpsTracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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

public class IntroActivity extends BaseActivity implements WeatherContract.ActivityView, NaverContract {
    private static final String TAG = IntroActivity.class.getSimpleName();

    WxKokDataPresenter mWxKokDataPresenter;
    CalenderInfoPresenter mCal;
    CustomAnimationDialog progressDoalog;
    GpsTracker mGpsTracker;
    Context mContext;
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ArrayList<String> mTargetPlaces = new ArrayList<>();
    ArrayList<String> mBookMarkPlaces = new ArrayList<>();
    ArrayList<String> mBookMarkCodeList = new ArrayList<>();
    ArrayList<String> mBookMarkTempCodeList = new ArrayList<>();
    ArrayList<String> mCodeList = new ArrayList<>();
    ArrayList<String> mTempCodeList = new ArrayList<>();
    ArrayList<String> temp = new ArrayList<>();
    ArrayList<String> tempCode = new ArrayList<>();
    ArrayList<String> tempTemperatureCode = new ArrayList<>();
    String mToday;
    String mReqToday;
    ArrayList<xy> xyList;
    int mNumOfSchedule;
    //0?????? ?????????, 1?????? ????????????, 2?????? ???????????? ??????
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
        //?????? ?????? ??????

        progressDoalog = new CustomAnimationDialog(this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("????????????!");
        progressDoalog.setTitle("?????? ???????????????!");
        //progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        setContentView(R.layout.activity_splash);
        mToday = calculateToday();

            boolean isPermissionPopupFirst = pref.getBoolean(SHARED_IS_PERMISSION_FIRST, false);
            if(isPermissionPopupFirst) {
                checkAppAuth("android.permission.ACCESS_FINE_LOCATION", ACCESS_FINE_LOCATION);
                //doAfterAuthCheck(ACCESS_FINE_LOCATION);
            } else {
                //????????? ??????
                showPermissionDialogView();
            }

    }

    CustomAnimationDialog customAnimationDialog2;

    private void startWhereApi() {
        customAnimationDialog2 = new CustomAnimationDialog(this);
        customAnimationDialog2.show();
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

        //Preference??? ?????? ?????? ????????????
        //JSON?????? ??????
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //?????????
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
        }, 100); // 1??? ?????? hd handler ??????  3000ms = 3???
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
        }, 100); // 1??? ?????? hd handler ??????  3000ms = 3???
        Log.i(TAG, "start progress4");
    }

    private void initDummy() {
        mCal = new CalenderInfoPresenter(getApplication());
        mCal.makeDummySchedule();

    }

    //??????????????????!! ?????? ????????? ???????????? preference??? ??????

    //1. ???????????? ????????? ????????? ????????????.
    //2. ?????? ???????????? api??? ????????????.
    //3. ????????? ???????????? ????????????.

    public void getScheduleDateWxApi() {
        Log.i(TAG, "getScheduleDateWxApi");
        int typeOfData =0;

        //?????? ????????????
        mTargetPlaces = getPlaceOfSchedule(typeOfData);
        mTargetPlaces = getPlaceOfSchedule(typeOfData);


        //????????? ???????????? ?????? ?????? ????????????
        mCodeList = manageDataForMidWxExpectation(mTargetPlaces);
        for(int i=0;i<mTargetPlaces.size();i++) {
            mTempCodeList.add(getTempPlaceCode(mTargetPlaces.get(i)));
        }


        mNumOfSchedule = mCodeList.size();

        //????????? api??? ????????? ????????? ????????????

        mReqToday = mToday + "0600";


        if(mCodeList.size()>0) {
            midWxService(mCodeList.get(0), mReqToday, 0, typeOfData);
        } else {

            lastFunctionIntro();
        }
        //???????????? ????????? ?????? ??????
        //??????????????? retrofit null ????????? ????????? ???????????? ??????????????? ?????? ?????????????????? ???????????? ??? ????????? ????????? ???????????????

    }

    public void getCurrentPlaceWxApi(){
        Log.i(TAG, "getCurrentPlaceWxApi");

        String address = getGpsPosition();

        temp.add(address);

        int typeOfData = 2;

        Schedule schedule = new Schedule();

        schedule.getScheduleData().setPlace(address);

        ScheduleList scheduleList = new ScheduleList();

        ArrayList<Schedule> scheduleArray = new ArrayList<>();

        scheduleArray.add(schedule);

        scheduleList.setScheduleArrayList(scheduleArray);

        scheduleList.getScheduleArrayList().get(0).setWhere(address);

        setScheduleDataInToSp(scheduleList, typeOfData);

        //????????? ???????????? ?????? ?????? ????????????
        tempCode = manageDataForMidWxExpectation(temp);

        //?????? ?????? ??????
        for(int i=0;i<temp.size();i++) {
            tempTemperatureCode.add(getTempPlaceCode(temp.get(i)));
        }

        //????????? api??? ????????? ????????? ????????????

        mToday = calculateToday();

        mReqToday = mToday + "0600";

        midWxService(tempCode.get(0), mReqToday, 0, typeOfData);

        //sendXyShortsCurrWx(temp, mTypeOfData);
    }

    public void getNowWxApi() {
        Log.i(TAG, "getNowWxApi");

        int typeOfData = 1;
        //?????? ????????????
        ScheduleList scheduleList = getBookMarkPlace();

        if(scheduleList!=null) {
            scheduleList = removeWrongAddress(scheduleList, typeOfData);

            ArrayList<Schedule> list = scheduleList.getScheduleArrayList();

            for (int i = 0; i < list.size(); i++) {
                mBookMarkPlaces.add(list.get(i).getWhere());
            }
        }

        if(mBookMarkPlaces.size()==0) {
            finishChecker=true;
            return;
        }
        //????????? ???????????? ?????? ?????? ????????????
        mBookMarkCodeList = manageDataForMidWxExpectation(mBookMarkPlaces);
        for(int i=0;i<mBookMarkPlaces.size();i++) {
            mBookMarkTempCodeList.add(getTempPlaceCode(mBookMarkPlaces.get(i)));
        }

        //????????? api??? ????????? ????????? ????????????

        mToday = calculateToday();

        mReqToday = mToday + "0600";

        //sendXyShortsCurrWx(mBookMarkPlaces, mTypeOfData);

        if(mBookMarkCodeList.size()>0) {
            midWxService(mBookMarkCodeList.get(0), mReqToday, 0, typeOfData);
        } else {
            progressDoalog.dismiss();
            lastFunctionIntro();
        }

        //???????????? ????????? ?????? ??????
        //??????????????? retrofit null ????????? ????????? ???????????? ??????????????? ?????? ?????????????????? ???????????? ??? ????????? ????????? ???????????????

        Log.i(TAG, "fin.");

    }

    private ScheduleList removeWrongAddress(ScheduleList scheduleList, int type){
        //????????? ?????? ????????? ?????? ??????

        if(scheduleList!=null&&scheduleList.getScheduleArrayList()!=null&&scheduleList.getScheduleArrayList().size()>0) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                String[] splited = scheduleList.getScheduleArrayList().get(i).getWhere().split(" ");
                if (splited.length == 1 || scheduleList.getScheduleArrayList().get(i).getWhere().equals("")) {
                    scheduleList.getScheduleArrayList().remove(i);
                }
            }

            setScheduleDataInToSp(scheduleList, type);
        }

        return scheduleList;
    }

    private ScheduleList getBookMarkPlace() {

        ScheduleList scheduleList = new ScheduleList();
        //????????? ????????? ????????? ????????????
        scheduleList = getBmPlaceFromSp();

        if(scheduleList!=null&&scheduleList.getScheduleArrayList()!=null&&scheduleList.getScheduleArrayList().size()!=0) {
            scheduleList = checkEmptyData(scheduleList, 1);
            scheduleList = removeWrongAddress(scheduleList, 1);
        }

        return scheduleList;

    }

    private ScheduleList checkEmptyData(ScheduleList scheduleList,int type) {

        if(scheduleList!=null&&scheduleList.getScheduleArrayList()!=null) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().equals("")) {
                    scheduleList.getScheduleArrayList().remove(i);
                }
            }
        }

        setScheduleDataInToSp(scheduleList, type);

        return scheduleList;

    }

    private ScheduleList getBmPlaceFromSp() {

        //Preference??? ?????? ?????? ?????? ????????????

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null??? ?????? ????????????.
        String loaded = pref.getString("bookMark", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference??? ????????? ????????? class ????????? ???????????? ??????

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

    //??????????????? ?????? ?????? ?????????

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
        //????????? api ??????
        for(int i=0;i<toNaver.size();i++) {
            naverApi(mTargetPlaces.get(toNaver.get(i)),type,toNaver.get(i));
        }

        return temp;

    }

    private void sendXyShortsCurrWx(ArrayList<String> addresses, int type) {

        xyList = new ArrayList<xy>();


        xyList = getXYlist(addresses, type);

        //????????? nx, ny ?????????
        //????????????
        for (int i = 0; i < xyList.size(); i++) {
            shortWxService(calculateToday(), "0500", xyList.get(i), i, type);
        }
    }

    private void sendDateForShortsWx(int type) {

        xyList = new ArrayList<xy>();


        if(type==0) {
            xyList = getXYlist(mTargetPlaces, type);
        }else if(type==1){
            xyList = getXYlist(mBookMarkPlaces, type);
        }else if(type==2){
            xyList = getXYlist(temp, type);
        }

        //????????? nx, ny ?????????
        //????????????
        for (int i = 0; i < xyList.size(); i++) {
            shortWxService(calculateToday(), "0500", xyList.get(i), i, type);
        }
    }

    // ????????? ??????????????? ???????????? ????????????
    // ????????? ????????? ?????????
    // api??????
    private ArrayList<String> getPlaceOfSchedule(int type) {

        ScheduleList scheduleList = getScheduleFromSp();

        scheduleList = checkEmptyData(scheduleList, type);
        scheduleList = removeWrongAddress(scheduleList,type);

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

    private String getGpsPosition(){
        String result="";
        //???????????? ?????? ???????????????
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

    private void setScheduleDataInToSp(ScheduleList scheduleList,int type) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if(scheduleList!=null&&scheduleList.getScheduleArrayList()!=null&&scheduleList.getScheduleArrayList().size()!=0) {
            scheduleList = removePastSchedule(scheduleList);
            if(type==0) {
                Collections.sort(scheduleList.getScheduleArrayList());
            }
        }

        Gson gson = new GsonBuilder().create();

        //Preference??? ?????? ?????? ????????????
        //JSON?????? ??????
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //?????????
        //editor.remove(year + month);
        if(type==0) {
            editor.putString("schedule", jsonString);
        }else if (type==1){
            editor.putString("bookMark", jsonString);
        }else if(type==2){
            editor.putString("currentPlace", jsonString);
        }

        editor.commit();
        //????????????
    }

    private ScheduleList removePastSchedule(ScheduleList scheduleList) {

        String today = getFutureDay("yyyyMMdd", 0);
        int intToday = Integer.parseInt(today);
        //???????????? ??????

        for(int i =0;i<scheduleList.getScheduleArrayList().size();i++) {
            if(!TextUtils.isEmpty(scheduleList.getScheduleArrayList().get(i).getScheduleData().getScheduledDate())
                    &&intToday > Integer.valueOf(scheduleList.getScheduleArrayList().get(i).getScheduleData().getScheduledDate())){
                scheduleList.getScheduleArrayList().remove(i);
            }
        }

        return scheduleList;

    }


    //?????????????????? ????????????, ???????????? ???????????? ????????????

    //?????????????????? ?????? ?????? ??????, ????????? ???????????? ????????????

    //???????????? ??????

    //???????????? ??????

    //???????????????~ ?????? preference ??????


    //API??????

    private Map<String, Object> getXYWithCalculator(String s) {

        LatLonCalculator latLonCalculator = new LatLonCalculator();

        LatLon latLon = latLonCalculator.getLatLonWithAddr(s, mContext);

        Map<String, Object> result = latLonCalculator.getGridxy(latLon.getLat(), latLon.getLon());

        return result;
    }

    private void naverApi(String s, int type, int position) {

        String id = getString(R.string.naver_client_id);
        String secret = getString(R.string.naver_client_secret);

        String address = s;

        NaverService naverService = new NaverService(this, mContext);
        naverService.getNaverGeo(id, secret, s, type, position);

    }

    private Date getToday() {
        // ????????? ????????? ?????? ?????????.
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

        //???,???,?????? ?????? ??????
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
        String currentYear = curYearFormat.format(date);

        int temp = Integer.parseInt(curMonthFormat.format(date));
        String currentMonth = String.valueOf(temp);
        if (temp < 10) {
            currentMonth = "0" + currentMonth;
        }
        String currentDay = curDayFormat.format(date);
        //??????????????? ??????
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
        //????????????
        //?????? ?????? ?????????, ?????????
        //String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        String key = getString(R.string.mid_weather_key);
        int numOfRows = 10;
        int pageNo = 1;
        String dataType = "JSON";
        String regId = placeCode;
        String tmFc;
        tmFc = Date;

        if(type==0) {
            regId = mCodeList.get(num);
        }else if(type==1){
            regId = mBookMarkCodeList.get(num);
        }

        WeatherService weatherService = new WeatherService(this,mContext);
        weatherService.getMidLandFcst(key, numOfRows, pageNo, dataType, regId, tmFc, num, type);
    }

    private void shortWxService(String Date, String time, xy data, int position, int type) {
        //????????????
        //?????? ?????? ?????????, ?????????
        //Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1??? 8???) 10?????? api?????? ??????
        //String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        String key = getString(R.string.shorts_weather_key);
        int numOfRows = 800;
        int pageNo = 1;
        String dataType = "JSON";
        String baseDate = Date;
        String baseTime = "0500";

        WeatherService weatherService = new WeatherService(this, mContext);
        weatherService.getShortFcst(key, numOfRows, pageNo, dataType, baseDate, time, data, position, type);
    }

    private void midTempService(String placeCode, String Date, int num, int type) {
        //????????????
        //?????? ?????? ?????????, ?????????
        //Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1??? 8???) 10?????? api?????? ??????
        //String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        String key = getString(R.string.mid_weather_key);
        int numOfRows = 10;
        int pageNo = 1;
        String dataType = "JSON";
        String regId = placeCode;
        String tmFc;
        tmFc = Date;

        if(type==0) {
            regId = mTempCodeList.get(num);
        }else if(type==1){
            regId = mBookMarkTempCodeList.get(num);
        }else if(type==2){
            regId = tempTemperatureCode.get(num);
        }

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

        if (place.startsWith("??????") || place.startsWith("??????") || place.startsWith("?????????")) {
            Code = "11B00000";
        } else if (place.startsWith("?????????")) {
            if(place.contains("??????")||place.contains("??????")||place.contains("??????")
                    ||place.contains("??????")||place.contains("??????")||place.contains("??????")||place.contains("??????"))
            {//??????
               Code = "11D20000";}else {
                //??????
                Code = "11D10000";
            }
        } else if (place.startsWith("??????") || place.startsWith("??????") || place.startsWith("????????????")) {
            Code = "11C20000";
        } else if (place.startsWith("????????????")) {
            Code = "11C10000";
        } else if (place.startsWith("??????") || place.startsWith("????????????")) {
            Code = "11F20000";
        } else if (place.startsWith("????????????")) {
            Code = "11F10000";
        } else if (place.startsWith("??????") || place.startsWith("????????????")) {
            Code = "11H10000";
        } else if (place.startsWith("??????") || place.startsWith("??????") || place.startsWith("????????????")) {
            Code = "11H20000";
        } else if (place.startsWith("??????")) {
            Code = "11G00000";
        }

        return Code;

    }

    private String getTempPlaceCode(String place) {

        String[] places = new String[] {"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????"
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
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")) {
                    //??????????????? ?????? ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //?????? ????????? ?????? ??????(??????) ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11D10000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")
                        && (scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????"))) {
                    //??????????????? ?????? ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //?????? ????????? ?????? ??????(??????) ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11D20000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")
                        && (scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().contains("??????"))) {
                    //??????????????? ?????? ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //?????? ????????? ????????? ????????? ?????? ??????(??????) ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11C20000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("????????????")) {
                    //??????????????? ?????? ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //?????? ????????? ?????? ??????(??????) ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11C10000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("????????????")) {
                    //??????????????? ?????? ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //?????? ????????? ?????? ??????(??????) ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11F20000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("????????????")) {
                    //??????????????? ?????? ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //?????? ????????? ?????? ??????(??????) ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11F10000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("????????????")) {
                    //??????????????? ?????? ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //?????? ????????? ?????? ??????(??????) ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11H10000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("????????????")) {
                    //??????????????? ?????? ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //?????? ????????? ?????? ??????(??????) ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11H20000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")
                        || scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("????????????")) {
                    //??????????????? ?????? ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //?????? ????????? ?????? ??????(??????) ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        } else if (code.equals("11G00000")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("??????")) {
                    //??????????????? ?????? ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());
                    //?????? ????????? ?????? ??????(??????) ??????
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setWxCheckday(mToday);
                }
            }
        }

        return scheduleList;

    }

    private ScheduleList mergingMidTempAndSchedule(ScheduleList scheduleList, MidTempResponse midTempResponse) {

        String code = midTempResponse.getResponse().getBody().getItems().getItem().get(0).getRegId();
        String place = "";

        String[] places = new String[] {"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????",	"?????????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"??????",	"?????????"
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
                        //??????????????? ?????? ??????
                        scheduleList.getScheduleArrayList().get(j).getScheduleData().getFcst()
                                .setTempList(midTempResponse.getResponse().getBody().getItems());
                        //?????? ????????? ?????? ??????(??????) ??????
                        scheduleList.getScheduleArrayList().get(j).getScheduleData().getFcst().setTempCheckDay(mToday);
                    }
                }
            }
        }

        return scheduleList;

    }

    //???????????? ????????? ????????? ???????????? ???????????? preference??? ?????? ??????
    private void midWxIntoPref(WxResponse wxResponse, int num, int type) {

        ScheduleList tempSchedule = new ScheduleList();

        if(type==0) {
            tempSchedule = getScheduleFromSp();
        }else if(type==1){
            tempSchedule = getBmPlaceFromSp();
        }else if(type==2){
            tempSchedule = getCurPlaceWxFromSp();
        }

        //??????????????? ???????????? ?????? ?????????
        tempSchedule = mergingWxDataAndSchedule(tempSchedule, wxResponse);

        setScheduleDataInToSp(tempSchedule, type);

        if(type==2){
            midTempService(tempTemperatureCode.get(0), mReqToday, 0, type);
        }

        if(type==0) {
            if ((num + 1) < mCodeList.size()) {
                midWxService(mCodeList.get(num + 1), mReqToday, num + 1, type);
            } else {
                midTempService(mTempCodeList.get(0), mReqToday, 0, type);
            }
        }else if(type==1){
            if ((num + 1) < mBookMarkCodeList.size()) {
                midWxService(mBookMarkCodeList.get(num + 1), mReqToday, num + 1, type);
            } else {
                midTempService(mBookMarkTempCodeList.get(0), mReqToday, 0, type);
            }
        }



    }

    private void midTempIntoPref(MidTempResponse midTempResponse, int num, int type) {

        ScheduleList tempSchedule = new ScheduleList();

        if(type==0) {
            tempSchedule = getScheduleFromSp();
        }else if(type==1){
            tempSchedule = getBmPlaceFromSp();
        }else if(type==2){
            tempSchedule = getCurPlaceWxFromSp();
        }

        //??????????????? ???????????? ?????? ?????????
        tempSchedule = mergingMidTempAndSchedule(tempSchedule, midTempResponse);

        setScheduleDataInToSp(tempSchedule, type);

        if(type==0) {
            if ((num + 1) < mTempCodeList.size()) {
                midTempService(mTempCodeList.get(num + 1), mReqToday, num + 1, type);
            } else {
                //???????????? ??????????????????
                sendDateForShortsWx(type);
            }
        }else if(type==1){
            if ((num + 1) < mBookMarkTempCodeList.size()) {
                midTempService(mBookMarkTempCodeList.get(num + 1), mReqToday, num + 1, type);
            } else {
                //???????????? ??????????????????
                sendDateForShortsWx(type);
            }
        }else if(type==2){
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

    //???????????? ?????? 3-10??? ?????? ??????
    @Override
    public void validateSuccess(boolean isSuccess, WxResponse wxResponse, int num, int type) {

        Log.i(TAG, "???????????? ?????? : " + wxResponse.getBody().getItems().getItem().get(0).getRegId());
        Log.i(TAG, "data?????? : " + wxResponse.getBody().getItems().getItem().get(0).getWf4Am());
        midWxIntoPref(wxResponse, num, type);

    }

    @Override
    public void validateShortSuccess(boolean isSuccess, ShortsResponse shortsResponse, String lat, String lon, int position, int type) {


        Log.i(TAG, "???????????? ??????!!" + shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstTime());
        //lat,lon??? ????????????

        //????????? ??????
        LatLonCalculator calculator = new LatLonCalculator();
        Address address = calculator.getAddressWithLatLon(lat, lon, mContext);
        String addrResult = arrangeAddressResults(address);

        receiveCnt++;

        setShortsDataIntoPref(shortsResponse, addrResult, position, type);

        //???????????? ?????? ??????
        //????????? ????????????

    }

    private ScheduleData checkTempMaxMin(ShortsResponse shortsResponse, ScheduleData scheduleData) {
        //?????? ?????? ?????? ????????????,

        String tomorrow = getFutureDay("yyyyMMdd", 1);
        String dayAfterTmr = getFutureDay("yyyyMMdd", 2);
        ArrayList<Integer> tempList = new ArrayList<>();
        ArrayList<Integer> tmrList = new ArrayList<>();
        ArrayList<Integer> datList = new ArrayList<>();
        ArrayList<todayTemp> todayTempList = new ArrayList<>();
        todayTemp todTmpObj = new todayTemp();


        for (int i = 0; i < shortsResponse.getResponse().getBody().getItems().getItem().size(); i++) {
            //??????
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(mToday)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();
                int intTime = Integer.parseInt(time);
                //??????
                //if (intTime < 1200) {

                //?????? ????????? ??????
                if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("TMP")) {
                    todTmpObj.setTemperature(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue());
                    todTmpObj.setTimeHhmm(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime());
                    todayTempList.add(todTmpObj);
                    todTmpObj = new todayTemp();
                    tempList.add(Integer.valueOf(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));

                }
                //} else {
                //?????? ????????? ??????
                //}

            }

            //??????
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(tomorrow)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();

                //????????? ??????
                if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("TMP")) {

                    tmrList.add(Integer.valueOf(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));

                }

            }

            //??????
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(dayAfterTmr)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();

                //????????? ??????
                if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("TMP")) {

                    datList.add(Integer.valueOf(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));

                }

            }

        }
        //????????? ?????? ??????
        scheduleData.getFcst().setTempToday(todayTempList);
        //?????? ?????? ???????????? ??????
        scheduleData = setMinMaxTemp(scheduleData, tempList, tmrList, datList);

        return scheduleData;

    }

    //3????????? ?????? ???????????? ?????? scheduleData
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

        //???????????? ?????? ??????, ?????? ????????? + ????????? ?????? ??????
        ScheduleData scheduleData = new ScheduleData();

        scheduleData = checkTempMaxMin(shortsResponse, scheduleData);

        //?????? ?????? ?????????
        scheduleData = checkWxShorts(shortsResponse, scheduleData);

        //response??? ???????????? position ?????? ??? ??????
        ScheduleList scheduleList = new ScheduleList();
        //null?????? initialize
        scheduleList = initialzeSl(scheduleList);

        if(type==0) {
            scheduleList = getScheduleFromSp();
        }else if(type==1){
            scheduleList = getBmPlaceFromSp();
        }else if(type==2){
            scheduleList = getCurPlaceWxFromSp();
        }

        scheduleList = matchingAdderess(scheduleList, addrResult, scheduleData, position);

        setScheduleDataInToSp(scheduleList, type);

        if(type==1){
            if(mBookMarkPlaces.size()==0){
                if(receiveCnt >= mBookMarkPlaces.size()){
                    lastFunctionIntro();
                }
            }else if(type==1){
                if(receiveCnt+1 > mBookMarkPlaces.size()){
                    lastFunctionIntro();
                }
            }
        }

        if(type==0) {
            if (type == 0 || mTargetPlaces.size() == 0) {
                if (receiveCnt >= mTargetPlaces.size()) {
                    lastFunctionIntro();
                }
            } else if (type == 0) {
                if (receiveCnt > mTargetPlaces.size()) {
                    lastFunctionIntro();
                }
            }
        }

        if(type==2&&finishChecker==true){
            lastFunctionIntro();
        }
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

    //???????????? ?????? ???????????? ?????? ????????? ???????????? ????????? ?????? ?????????.
    private ScheduleList matchingAdderess(ScheduleList scheduleList, String addrResult, ScheduleData scheduleData, int i) {


        TempItem dum = new TempItem();
        WxItem dum2 = new WxItem();
        TempItems dum3 = new TempItems();
        WxItems dum4 = new WxItems();


        if(scheduleList.getScheduleArrayList()!=null&&!(scheduleList.getScheduleArrayList().size()==0)) {
            //???????????? ????????? ???????????? ????????? ???????????????~ ?????? ???????????? ??????
            Log.i(TAG, "matchingPosition Algo");
            //???????????? ????????? ?????? ????????? ??????????????? ???????????? ???????????? ????????? ??????, ????????? ???????????? ??????????????? ??? ????????? ??????. ?????? 6??? ?????? 18???
            //???????????? ????????? ??????, ????????? ???????????? ????????? ???????????? ???????????? ?????????.

            //????????? get(0) ????????? ??????????????? ????????? ????????????. ??????????????? ????????? ?????? ???????????????
            if (scheduleList.getScheduleArrayList().get(i) == null) {
                scheduleList.getScheduleArrayList().get(i).setScheduleData(scheduleData);
            }else {
                //???????????? ?????????
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

                if (scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempToday() == null
                        || scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempToday().size() == 0) {
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempToday(scheduleData.getFcst().getTempToday());
                }

                //??????????????? ?????? ????????? ?????? ???????????? ?????? ??????????????? ??????????????????.
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

                //??????????????????
                //????????????
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt0AmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt0AmType()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt0PmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt0PmType()
                );

                //????????????
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt0Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt0Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt0Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt0Pm()
                );

                //????????????
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setWf0Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getWf0Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setWf0Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm()
                );

                //????????????
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt1AmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1AmType()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt1PmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1PmType()
                );

                //????????????
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt1Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt1Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm()
                );

                //????????????
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setWf1Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setWf1Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm()
                );

                //????????????
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt2AmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2AmType()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt2PmType(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2PmType()
                );

                //????????????
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt2Am(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getWxList().getItem().get(0).setRnSt2Pm(
                        scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm()
                );

                //????????????
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

        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)

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
            //??????
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(date)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();

                temp.setTimeHhmm(time);

                int intTime = Integer.parseInt(time);
                //??????
                if (intTime < 1200) {

                    //?????? ????????? ????????????, ????????????, ????????????
                    //todayWx = 1 : ????????????, 2: ????????????, 3: ????????????
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //???????????? = ???????????? rnst??? ??????
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt0Am(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                                //????????? ????????? ???????????? ????????????,??????, ??????????????? ??? temp??? ???????????? ??????
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
                        //???????????? = type?????? ?????? ??????
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
                        //???????????? = ???????????? ????????????
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Am("????????????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Am("??????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Am("??????");
                        }
                    }

                } else {
                    //??????
                    //????????????, ????????????, ????????????
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //????????????
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
                        //????????????
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
                        //???????????? = ???????????? ????????????
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Pm("????????????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Pm("??????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Pm("??????");
                        }
                    }
                }
            }
            //????????? ????????? ??????

        }

        scheduleData.getFcst().setWxToday(todayWxList);

        return scheduleData;
    }

    public ScheduleData shortsWxTmrAlgorithm(ShortsResponse shortsResponse, ScheduleData scheduleData, String date) {

        WxItem dum = new WxItem();
        scheduleData.getFcst().getWxList().getItem().add(dum);

        for (int i = 0; i < shortsResponse.getResponse().getBody().getItems().getItem().size(); i++) {
            //??????
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(date)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();
                int intTime = Integer.parseInt(time);
                //??????
                if (intTime < 1200) {

                    //?????? ????????? ????????????, ????????????, ????????????
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //???????????? = ???????????? rnst??? ??????
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt1Am(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //???????????? = type?????? ?????? ??????
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt1AmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        //???????????? = ???????????? ????????????
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Am("????????????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Am("??????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Am("??????");
                        }
                    }

                } else {
                    //?????? ????????? ??????
                    //?????? ????????? ????????????, ????????????, ????????????
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //????????????
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt1Pm(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //????????????
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt1PmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        //???????????? = ???????????? ????????????
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Pm("????????????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Pm("??????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf1Pm("??????");
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
            //??????
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(date)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();
                int intTime = Integer.parseInt(time);
                //??????
                if (intTime < 1200) {

                    //?????? ????????? ????????????, ????????????, ????????????
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //???????????? = ???????????? rnst??? ??????
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt2Am(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //???????????? = type?????? ?????? ??????
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt2AmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        //???????????? = ???????????? ????????????
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Am("????????????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Am("??????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Am("??????");
                        }
                    }

                } else {
                    //?????? ????????? ??????
                    //?????? ????????? ????????????, ????????????, ????????????
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //????????????
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt2Pm(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //????????????
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt2PmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        //???????????? = ???????????? ????????????
                        if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("3")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Pm("????????????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("4")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Pm("??????");
                        } else if ((shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstValue()).equals("1")) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf2Pm("??????");
                        }
                    }
                }
            }

        }
        return scheduleData;
    }


    @Override
    public void validateFailure(String message, String regId, int num, int type) {

        Log.e(TAG, "???????????? ??????");
        //????????? api??? ????????? ????????? ????????????
        boolean checker = checkAMPM();
        //????????? ???????????? ?????? ?????? ???????????? ??? 12??? ???????????? ?????? ????????? ?????? ???????????? ??????
        //midChecker??? ???????????? ?????? ????????? ??????????????? ?????? ?????? ??????
        if(midChecker) {
            Log.e(TAG, "???????????? ????????????1"+midChecker);
            midWxService(regId, getFutureDay("yyyyMMdd", 0) + "0600", num, type);
            midChecker=false;
        }else{
            if (checker) {
                Log.e(TAG, "???????????? ????????????2"+midChecker);
                midWxService(regId, getFutureDay("yyyyMMdd", 0) + "1800", num, type);
                midChecker=true;
            } else {
                Log.e(TAG, "???????????? ????????????3"+midChecker);
                midWxService(regId, getFutureDay("yyyyMMdd", -1) + "1800", num, type);
            }
        }
    }

    @Override
    public void validateShortFailure(String message, xy data, int position, int type) {
        Log.e(TAG, "???????????? ?????? ??? : " + position);
        boolean checker = checkAMPM();
        //????????? ???????????? ?????? ?????? ???????????? ??? 12??? ???????????? ?????? ????????? ?????? ???????????? ??????
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

        Log.e(TAG, "???????????? ??????");
        midTempIntoPref(midTempResponse, num, type);
    }

    @Override
    public void validateMidTempFailure(String message, String regId, int num, int type) {
        Log.e(TAG, "???????????? ??????");
        boolean checker = checkAMPM();
        //????????? ???????????? ?????? ?????? ???????????? ??? 12??? ???????????? ?????? ????????? ?????? ???????????? ??????
        if(midTempChecker) {
            Log.e(TAG, "???????????? ????????????1"+midTempChecker);
            midWxService(regId, getFutureDay("yyyyMMdd", 0) + "0600", num, type);
            midTempChecker=false;
        }else{
            if (checker) {
                Log.e(TAG, "???????????? ????????????2"+midTempChecker);

                midTempService(regId, getFutureDay("yyyyMMdd", 0) + "1800", num, type);
                midTempChecker = true;
            } else {
                Log.e(TAG, "???????????? ????????????3"+midTempChecker);

                midTempService(regId, getFutureDay("yyyyMMdd", -1) + "1800", num, type);
            }
        }
    }

    private boolean checkAMPM() {

        Date dateToday = getToday();

        //???,???,?????? ?????? ??????
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        //???????????? ???????????? ?????? ?????? ?????????
        if (inthour < 12||inthour==24) {
            //??????
            return false;
        } else {
            //??????
            return true;
        }

    }

    public void lastFunctionIntro() {
        Log.i(TAG, "????????? api ?????????");

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        checkFirstStartApp = pref.getBoolean("firstStart",true);

        if(checkFirstStartApp) {
            startActivity(new Intent(getApplication(), MainActivity.class)); //????????? ?????? ???, ChoiceFunction ??????
            IntroActivity.this.finish();
            //return;
        }

        progressDoalog.dismiss();

        Intent intent = getIntent();
        String from = "";
        receiveCnt=0;
        if(!TextUtils.isEmpty(intent.getStringExtra("from"))){
            from = intent.getStringExtra("from");
        }

        if(from.equals("goToWeather")&&mNumOfSchedule>=0){
            startActivity(new Intent(getApplication(), WeatherActivity.class)); //????????? ?????? ???, ChoiceFunction ??????
            IntroActivity.this.finish();
        }
        else if(from.equals("goToNow")){
            startActivity(new Intent(getApplication(), NowWxActivity.class)); //????????? ?????? ???, ChoiceFunction ??????
            IntroActivity.this.finish();
        }else {
            startActivity(new Intent(getApplication(), WeatherActivity.class)); //????????? ?????? ???, ChoiceFunction ??????
            IntroActivity.this.finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusCheck(eventBus event){
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("firstStart",event.firstChecker);
        editor.apply();

        lastFunctionIntro();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);

    }

    @Override
    public void onBackPressed() {
        //?????? ????????? ???????????? ???????????? ???????????? ?????? ???????????? ???
    }

    private void clearSchedule(int type) {

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
        if(type==0) {
            editor.putString("schedule", jsonString);
        }else if (type==1){
            editor.putString("bookMark", jsonString);
        }else if(type==2){
            editor.putString("currentPlace", jsonString);
        }

        editor.commit();
        //????????????
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

//    private final static int APP_PERMISSIONS_REQ_MIC = 1000;
//    private final static int APP_PERMISSIONS_REQ_STORAGE = 1100;
//    private final static int APP_PERMISSIONS_REQ_PHONE = 1200;
    private final static int ACCESS_COARSE_LOCATION = 1300;
    private final static int ACCESS_FINE_LOCATION = 1400;

    private void checkAppAuth(final String a_PermissionReq, final int a_ReqCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionResult = checkSelfPermission(a_PermissionReq);

            if (permissionResult == PackageManager.PERMISSION_DENIED) {

                showPermissionDialogView();

            } else {
                //????????? ?????? ???
                doAfterAuthCheck(a_ReqCode);
            }

        } else {
            //??? ??????
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

            if (a_ReqCode == ACCESS_FINE_LOCATION) {
            checkAppAuth("android.permission.ACCESS_COARSE_LOCATION", ACCESS_COARSE_LOCATION);
        } else if (a_ReqCode == ACCESS_COARSE_LOCATION){
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
            // ?????? ?????? : ???????????? ??????????????? ??????????????? ???????????? ??? ????????? ???????????????
            finish();
        }
    }

    private void showPermissionDialogView() {
        //Dialog ??????
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

                //?????? ?????????
                //checkAppAuth("android.permission.ACCESS_FINE_LOCATION", ACCESS_FINE_LOCATION);
                //?????? ??????
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, ACCESS_FINE_LOCATION);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void naverSuccess(boolean isSuccess, NaverData naverData,int type, int position) {
        LatLon resultNaver = new LatLon();
        //????????? ???????????????.
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

        //????????? nx, ny ?????????
        //????????????
            shortWxService(calculateToday(), "0500",tempObj, position, type);

    }

    @Override
    public void naverFailure(String message) {
        Log.i(TAG, "naverFailure");
    }
}
