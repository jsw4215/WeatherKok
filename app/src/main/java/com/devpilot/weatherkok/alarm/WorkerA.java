package com.devpilot.weatherkok.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.datalist.data.ScheduleData;
import com.devpilot.weatherkok.weather.NaverService;
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
import com.devpilot.weatherkok.weather.models.shortsExpectation.todayWx;
import com.devpilot.weatherkok.weather.models.xy;
import com.devpilot.weatherkok.weather.utils.LatLonCalculator;
import com.devpilot.weatherkok.weather.utils.NaverData;
import com.devpilot.weatherkok.when.models.Schedule;
import com.devpilot.weatherkok.when.models.ScheduleList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.devpilot.weatherkok.alarm.Constants.A_MORNING_EVENT_TIME;
import static com.devpilot.weatherkok.alarm.Constants.A_NIGHT_EVENT_TIME;
import static com.devpilot.weatherkok.alarm.Constants.KOREA_TIMEZONE;
import static com.devpilot.weatherkok.alarm.Constants.NOTIFICATION_INTERVAL_HOUR;
import static com.devpilot.weatherkok.alarm.Constants.WORK_A_NAME;
import static com.devpilot.weatherkok.intro.IntroActivity.getFutureDay;
import static com.devpilot.weatherkok.weather.WeatherActivity.PREFERENCE_KEY;


public class WorkerA extends Worker implements WeatherContract.ActivityView, NaverContract {

    private static final String TAG = "WorkerA";
    Context mContext;
    ArrayList<String> mCodeList = new ArrayList<>();
    ArrayList<String> mTargetPlaces = new ArrayList<>();
    ArrayList<String> mTempCodeList = new ArrayList<>();
    String mReqToday;
    String mToday;
    ScheduleList mScheduleList;
    ArrayList<xy> xyList;
    boolean finish = false;

    public WorkerA(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if(finish) {

            settingNotification();

        }else {

            mScheduleList = getScheduleFromSp();

            mToday = calculateToday();

            mTargetPlaces = getPlaceOfSchedule(0);
            mCodeList = manageDataForMidWxExpectation(mTargetPlaces);
            for (int i = 0; i < mTargetPlaces.size(); i++) {
                mTempCodeList.add(getTempPlaceCode(mTargetPlaces.get(i)));
            }

            int hh = getHourHH();
            String HH = "0600";
            mReqToday = mToday + "0600";

            if (hh < 6) {
                mReqToday = getFutureDay("yyyyMMdd", -1) + "1800";
            } else if (hh < 18) {
                mReqToday = getFutureDay("yyyyMMdd", 0) + "0600";
            } else {
                mReqToday = getFutureDay("yyyyMMdd", 0) + "1800";
            }

            initXYForShorts();

            int diff = -1;

            if (mCodeList.size() > 0) {

                for (int i = 0; i < mCodeList.size(); i++) {
                    diff = getDiffDay(i);
                    if (diff > 2) {
                        midWxService(mCodeList.get(i), mReqToday, i, 0);
                    } else {
                        startShorts(i);
                    }

                }

            }

        }

        return Result.success();

    }

    private void settingNotification() {


        NotificationHelper mNotificationHelper = new NotificationHelper(getApplicationContext());
        long currentMillis = Calendar.getInstance(TimeZone.getTimeZone(KOREA_TIMEZONE), Locale.KOREA).getTimeInMillis();

        // ?????? ??????(08:00-09:00, 20:00-21:00)??? ??????????????? ?????? ??????
        Calendar eventCal = NotificationHelper.getScheduledCalender(A_MORNING_EVENT_TIME);
        long morningNotifyMinRange = eventCal.getTimeInMillis();

        eventCal.add(Calendar.HOUR_OF_DAY, NOTIFICATION_INTERVAL_HOUR);
        long morningNotifyMaxRange = eventCal.getTimeInMillis();

        eventCal.set(Calendar.HOUR_OF_DAY, A_NIGHT_EVENT_TIME);
        long nightNotifyMinRange = eventCal.getTimeInMillis();

        eventCal.add(Calendar.HOUR_OF_DAY, NOTIFICATION_INTERVAL_HOUR);
        long nightNotifyMaxRange = eventCal.getTimeInMillis();

        // ?????? ????????? ?????? ?????? ????????? ???????????????
        boolean isMorningNotifyRange = morningNotifyMinRange <= currentMillis && currentMillis <= morningNotifyMaxRange;
        // ?????? ????????? ?????? ?????? ????????? ???????????????
        boolean isNightNotifyRange = nightNotifyMinRange <= currentMillis && currentMillis <= nightNotifyMaxRange;
        // ?????? ????????? ?????? ????????? ????????????
        boolean isEventANotifyAvailable = isMorningNotifyRange || isNightNotifyRange;


        if (isEventANotifyAvailable) {
            // ?????? ????????? ?????? ????????? ???????????? ?????? ??????
            mNotificationHelper.createNotification(WORK_A_NAME);
        } else {
            // ??? ?????? ?????? ?????? ?????? A ????????? ?????? ??????????????? notificationDelay ???????????? ????????? ??????
            long notificationDelay = NotificationHelper.getNotificationDelay(WORK_A_NAME);
            OneTimeWorkRequest workRequest =
                    new OneTimeWorkRequest.Builder(WorkerB.class)
                            .setInitialDelay(notificationDelay, TimeUnit.MILLISECONDS)
                            .build();
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
        }


    }

    private void midWxService(String placeCode, String Date, int num, int type) {
        //????????????
        //?????? ?????? ?????????, ?????????
        //String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        String key = mContext.getString(R.string.mid_weather_key);
        int numOfRows = 10;
        int pageNo = 1;
        String dataType = "JSON";
        String regId = placeCode;
        String tmFc;
        tmFc = Date;

        if(type==0) {
            regId = mCodeList.get(num);
          }
//        else if(type==1){
//            regId = mBookMarkCodeList.get(num);
//        }

        WeatherService weatherService = new WeatherService(this,mContext);
        weatherService.getMidLandFcst(key, numOfRows, pageNo, dataType, regId, tmFc, num, type);
    }

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

    private void setScheduleDataInToSp(ScheduleList scheduleList,int type) {

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if(type==0) {
            Collections.sort(scheduleList.getScheduleArrayList());
        }
        if(scheduleList!=null&&scheduleList.getScheduleArrayList()!=null&&scheduleList.getScheduleArrayList().size()!=0) {
            scheduleList = removePastSchedule(scheduleList);
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

    private ScheduleList getScheduleFromSp() {

        //Preference??? ?????? ?????? ?????? ????????????

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null??? ?????? ????????????.
        String loaded = pref.getString("schedule", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference??? ????????? ????????? class ????????? ???????????? ??????

        return scheduleList;

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

    private Date getToday() {
        // ????????? ????????? ?????? ?????????.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    @Override
    public void validateSuccess(boolean isSuccess, WxResponse wxResponse, int num, int type) {

        checkWxChanged(wxResponse, num);


    }

    private void initXYForShorts() {

        xyList = new ArrayList<xy>();

        xyList = getXYlist(mTargetPlaces);


    }

    private void startShorts(int i){
        //????????? nx, ny ?????????
        shortWxService(calculateToday(), "0500", xyList.get(i), i);
    }

    private void shortWxService(String Date, String time, xy data, int position) {
        //????????????
        //?????? ?????? ?????????, ?????????
        //Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1??? 8???) 10?????? api?????? ??????
        //String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        String key = mContext.getString(R.string.shorts_weather_key);
        int numOfRows = 800;
        int pageNo = 1;
        String dataType = "JSON";
        String baseDate = Date;

        int hh = getHourHH();
        String HH = "0500";

        if(hh<5){
            baseDate = getFutureDay("yyyyMMdd",-1);
            time = "1700";
        }else if(hh<17){
            baseDate = getFutureDay("yyyyMMdd",0);
            time = "0500";
        }else {
            baseDate = getFutureDay("yyyyMMdd",0);
            time = "1700";
        }

        WeatherService weatherService = new WeatherService(this, mContext);
        weatherService.getShortFcst(key, numOfRows, pageNo, dataType, baseDate, time, data, position, 0);
    }

    private ArrayList<xy> getXYlist(ArrayList<String> mTargetPlaces) {
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
            naverApi(mTargetPlaces.get(toNaver.get(i)),0,toNaver.get(i));
        }

        return temp;

    }

    private void naverApi(String s, int type, int position) {

        String id = mContext.getString(R.string.naver_client_id);
        String secret = mContext.getString(R.string.naver_client_secret);
        String address = s;

        NaverService naverService = new NaverService(this, mContext);
        naverService.getNaverGeo(id, secret, s, type, position);

    }

    private Map<String, Object> getXYWithCalculator(String s) {

        LatLonCalculator latLonCalculator = new LatLonCalculator();

        LatLon latLon = latLonCalculator.getLatLonWithAddr(s, mContext);

        Map<String, Object> result = latLonCalculator.getGridxy(latLon.getLat(), latLon.getLon());

        return result;
    }

    private int getDiffDay(int pos){

        int diff= 0;

        Schedule schedule = mScheduleList.getScheduleArrayList().get(pos);

        String scheduledDate = schedule.getScheduleData().getScheduledDate();

        diff = (int) howFarFromToday(scheduledDate);

        return diff;
    }


    private void checkWxChanged(WxResponse wxResponse, int pos) {

        //1. ?????? ????????? ???????????? ????????????.
        //2. ?????? ???????????? wx???????????? wxResponse??? ????????????.
        //3. ????????? ????????? ????????? ????????? ????????? ??????????????? ??????.
        Schedule schedule = mScheduleList.getScheduleArrayList().get(pos);

        Schedule newWx = new Schedule();

        newWx.getScheduleData().getFcst().setWxList(wxResponse.getBody().getItems());

        String scheduledDate = schedule.getScheduleData().getScheduledDate();

        int gap = (int) howFarFromToday(scheduledDate);

        int hour = getHourHH();

        //????????? ????????? ?????? ????????? ????????????
        WxData savedData = findScheduleDateWxData(schedule.getScheduleData(), gap, hour);
        WxData newData = findScheduleDateWxData(newWx.getScheduleData(), gap, hour);

        if(savedData.getAm()==newData.getAm()&&savedData.getPm()==newData.getPm()){
            return;
        }else {

            finish = true;
            doWork();
        }

        //????????????????????? ???????????? ?????? ????????? ????????????.
        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
        //??????
        //?????? ?????? ?????????, ???, ?????????,???, ???/???
        //?????? ?????????,



    }

    private void checkWxChangedForShorts(ShortsResponse shortsResponse, int position) {

        //1. ?????? ????????? ???????????? ????????????.
        //2. ?????? ???????????? wx???????????? wxResponse??? ????????????.
        //3. ????????? ????????? ????????? ????????? ????????? ??????????????? ??????.
        Schedule schedule = mScheduleList.getScheduleArrayList().get(position);

        Schedule newWx = new Schedule();

        ScheduleData scheduleData = setShortsDataIntoPref(shortsResponse, "",position,0);

        String scheduledDate = schedule.getScheduleData().getScheduledDate();

        int gap = (int) howFarFromToday(scheduledDate);

        int hour = getHourHH();

        //????????? ????????? ?????? ????????? ????????????
        WxData savedData = findScheduleDateWxData(schedule.getScheduleData(), gap, hour);
        WxData newData = findScheduleDateWxData(scheduleData, gap, hour);

        if(savedData.getAm()==newData.getAm()&&savedData.getPm()==newData.getPm()){
            return;
        }else {
            finish = true;
            doWork();
        }


    }

    private ScheduleData setShortsDataIntoPref(ShortsResponse shortsResponse, String addrResult, int position, int type) {

        //???????????? ?????? ??????, ?????? ????????? + ????????? ?????? ??????
        ScheduleData scheduleData = new ScheduleData();

        //?????? ?????? ?????????
        scheduleData = checkWxShorts(shortsResponse, scheduleData);

        return scheduleData;

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
    public void validateShortSuccess(boolean isSuccess, ShortsResponse shortsResponse, String lat, String lon, int position, int type) {
        checkWxChangedForShorts(shortsResponse, position);
    }

    @Override
    public void validateFailure(String message, String regId, int num, int type) {

    }

    @Override
    public void validateShortFailure(String message, xy data, int position, int type) {

    }

    @Override
    public void validateMidTempSuccess(MidTempResponse midTempResponse, int num, int type) {

    }

    @Override
    public void validateMidTempFailure(String message, String regId, int num, int type) {

    }

    private long howFarFromToday(String dateCompared) {

        Calendar getToday = Calendar.getInstance();
        getToday.setTime(new Date()); //?????? ??????

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyyMMdd").parse(dateCompared);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i(TAG, "?????? ????????? ?????? ????????????.");
        }
        Calendar cmpDate = Calendar.getInstance();
        cmpDate.setTime(date); //?????? ??????

        long diffSec = (cmpDate.getTimeInMillis() - getToday.getTimeInMillis()) / 1000;
        long diffDays = diffSec / (24 * 60 * 60); //????????? ??????

        return diffDays;

    }

    private int getHourHH() {

        Date dateToday = getToday();

        //???,???,?????? ?????? ??????
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        return inthour;

    }

    private WxData findScheduleDateWxData(ScheduleData scheduleData, int diffDays, int currHh) {

        WxData wxData = new WxData();

        String strCurHH = "";

        if(currHh<10){
            strCurHH = "0" + String.valueOf(currHh);
        }

        strCurHH = strCurHH + "00";

        //0:??????, 1: ??????, 2: ???, 3: ?????????, 4: ???, 5: ??????, 6: ???/???

        if(diffDays==0){
            //???????????? ??????????????? ?????? ?????? ????????? ??????????????? ?????????

            wxData = setNowWxCond(scheduleData, strCurHH);

        }else if(diffDays==1){
            //????????????????????? ???????????? ?????? ????????? ????????????.
            //POP ????????????
            //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
            //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
            //??????
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,

            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am();
            Log.i(TAG, "1?????? ?????? : " + wx);

            wxData = setAmWxData(wx, wxData);

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm();
            Log.i(TAG, "1?????? ?????? : " + wx);

            wxData = setPmWxData(wx, wxData);

        }else if(diffDays==2){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Am();
            Log.i(TAG, "2 ?????? : " + wx);
            //????????????
            wxData = setAmWxData(wx, wxData);


            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm();
            Log.i(TAG, "2 ?????? : " + wx);

            wxData = setPmWxData(wx, wxData);


        }
        else if(diffDays==3){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Am();
            Log.i(TAG, "3 ?????? : " + wx);

            wxData = setAmWxData(wx, wxData);

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Pm();
            Log.i(TAG, "3 ?????? : " + wx);

            wxData = setPmWxData(wx, wxData);

        }
        else if(diffDays==4){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Am();
            //????????????

            wxData = setAmWxData(wx, wxData);

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Pm();
            //????????????
            wxData = setPmWxData(wx, wxData);

        }
        else if(diffDays==5){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Am();
            //????????????
            wxData = setAmWxData(wx, wxData);

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Pm();
            //????????????

            wxData = setPmWxData(wx, wxData);

        }
        else if(diffDays==6){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Am();
            //????????????

            wxData = setAmWxData(wx, wxData);

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Pm();
            //????????????

            wxData = setPmWxData(wx, wxData);

        }
        else if(diffDays==7){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Am();
            //????????????

            wxData = setAmWxData(wx, wxData);

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Pm();
            //????????????

            wxData = setPmWxData(wx, wxData);

        }
        else if(diffDays==8){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf8();
            //????????????
            wxData = setAmWxData(wx, wxData);
            wxData = setPmWxData(wx, wxData);


        }else if(diffDays==9){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf9();
            //????????????
            wxData = setAmWxData(wx, wxData);
            wxData = setPmWxData(wx, wxData);
        }
        else if(diffDays==10){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf10();
            //????????????
            wxData = setAmWxData(wx, wxData);
            wxData = setPmWxData(wx, wxData);
        }


        return wxData;
    }

    private WxData setPmWxData(String wx, WxData wxData) {

        if (wx.equals("??????")) {
            wxData.setPm(0);
        } else if (wx.equals("????????????")) {
            wxData.setPm(1);
        } else if (wx.equals("??????")) {
            wxData.setPm(5);
        } else if (wx.contains("???")) {
            wxData.setPm(4);
        } else if (wx.contains("???")) {
            wxData.setPm(2);
        } else if (wx.contains("?????????")) {
            wxData.setPm(3);
        }

        return wxData;

    }

    private WxData setAmWxData(String wx, WxData wxData) {

        if (wx.equals("??????")) {
            wxData.setAm(0);
        } else if (wx.equals("????????????")) {
            wxData.setAm(1);
        } else if (wx.equals("??????")) {
            wxData.setAm(5);
        } else if (wx.contains("???")) {
            wxData.setAm(4);
        } else if (wx.contains("???")) {
            wxData.setAm(2);
        } else if (wx.contains("?????????")) {
            wxData.setAm(3);
        }

        return wxData;

    }

    private WxData setNowWxCond(ScheduleData scheduleData, String strCurHH){

        //0 ??????, 1 ??????, 2 ???, 3 ?????????, 4 ???, 5 ??????, 6 ??????
        WxData newWx = new WxData();

        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_gray);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_rain_snow);

        //????????????????????? ???????????? ?????? ????????? ????????????.
        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
        //???????????????, ????????? ????????? ??????
        for(int i=0;i<scheduleData.getFcst().getWxToday().size();i++){

            if(scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)){

                if(scheduleData.getFcst().getWxToday().get(i).getRainType()==0) {
                    Log.i(TAG, "?????? ??????");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        newWx.setAm(0);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        newWx.setAm(1);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        newWx.setAm(5);
                    }

                }else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //???
                        newWx.setAm(4);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //??????
                        newWx.setAm(6);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //???
                        newWx.setAm(2);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //?????????
                        newWx.setAm(3);
                    }
                }
            }


        }

        String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm();
        Log.i(TAG, "?????? ?????? : " + wx);
        //????????????
        if (wx.equals("??????")) {

            newWx.setPm(0);
        } else if (wx.equals("????????????")) {
            newWx.setPm(1);
        } else if (wx.equals("??????")) {
            newWx.setPm(5);
        } else if (wx.contains("???")) {
            newWx.setPm(4);
        } else if (wx.contains("???")) {
            newWx.setPm(2);
        } else if (wx.contains("?????????")) {
            newWx.setPm(3);
        }

        return newWx;

    }


    @Override
    public void naverSuccess(boolean isSuccess, NaverData naverData, int type, int position) {
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
        shortWxService(calculateToday(), "0500",tempObj, position);

    }

    @Override
    public void naverFailure(String message) {

    }
}