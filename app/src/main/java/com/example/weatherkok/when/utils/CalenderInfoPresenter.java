package com.example.weatherkok.when.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.weatherkok.when.CalendarService;
import com.example.weatherkok.when.interfaces.RestContract;
import com.example.weatherkok.when.models.ResponseParams;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.when.models.base.BaseDateInfo;
import com.example.weatherkok.when.models.base.BaseDateInfoList;
import com.example.weatherkok.when.models.single.ResponseSingle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

//달력의 기본정보를 DB(preference)에 생성하고 저장해주는 작업
public class CalenderInfoPresenter implements RestContract.ActivityView {
    private static final String TAG = CalenderInfoPresenter.class.getSimpleName();
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    String mYear;
    String mMonth;
    BaseDateInfoList mBaseDateInfoList = new BaseDateInfoList();
    ArrayList<BaseDateInfo> mDateInfoList;
    Context mContext;

    public CalenderInfoPresenter() {
    }

    public void initCal(String year, String month, Context context) {
        this.mContext = context;

        //clearList(year, month);
        //양력, 음력 날짜를 preference에 저장 + 요일, 1일을 위한 공백 추가
        setLunaOnThePreference(year, month);
        //해당 월의 공휴일을 저장
        arrangerHoliday(year, month);
        //처음에만 더미생성
        makeDummySchedule();

    }

    private BaseDateInfoList settingDay(BaseDateInfoList baseDateInfoList) {
        //gridview 요일 표시

        //어레이 앞에 추가한것임
        ArrayList<BaseDateInfo> listDay = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            //토 = 100 금101,목 102,...(어댑터에서 구분할 예정
            BaseDateInfo baseDateInfo = new BaseDateInfo();
            baseDateInfo.setDate(100 + i);
            listDay.add(baseDateInfo);
            baseDateInfo = new BaseDateInfo();
        }

        baseDateInfoList.getBaseDateInfoList().addAll(0, listDay);

        return baseDateInfoList;
    }

    private int check1stDayOfTheMonth(String year, String month) {

        Calendar calendar = Calendar.getInstance();
        int intYear = Integer.parseInt(year);
        int intMonth = Integer.parseInt(month);
        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        calendar.set(intYear, intMonth - 1, 1);
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
        Log.i(TAG, "check1st day of the month" + dayNum);
        return dayNum;

    }

    //1일이 무슨요일인지 계산해서 99를 넣어 어댑터에서 빈칸으로 만들 것
    private BaseDateInfoList setting1stDay(BaseDateInfoList baseDateInfoList, int dayNum) {

        BaseDateInfo baseDateInfo = new BaseDateInfo();
        baseDateInfo.setDate(99);

        //99 = 빈칸
        for (int i = 1; i < dayNum; i++) {
            baseDateInfoList.getBaseDateInfoList().add(0, baseDateInfo);
        }

        return baseDateInfoList;
    }

    private void clearList(String year, String month) {
        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(year + month);
        editor.commit();
    }

    private BaseDateInfoList getDateInfoFromSP(String year, String month) {

        //Preference에 날씨 정보 객체 불러오기

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        BaseDateInfoList baseDateInfoList = new BaseDateInfoList();

        //null일 경우 처리할것.
        String loaded = pref.getString(year + month, "");

        baseDateInfoList = gson.fromJson(loaded, BaseDateInfoList.class);
        //Preference에 저장된 데이터 class 형태로 불러오기 완료

        return baseDateInfoList;
    }

    private void setDateInfoToSP(BaseDateInfoList baseDateInfoList, String year, String month) {

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        //Preference에 정보 객체 저장하기
        //JSON으로 변환
        String jsonString = gson.toJson(baseDateInfoList, BaseDateInfoList.class);
        Log.i("jsonString : ", jsonString);

        //초기화
        //editor.remove(year + month);

        editor.putString(year + month, jsonString);
        editor.commit();
        //저장완료

    }

    //해당 년, 월 정보를 받아와서 그 달의 음력 날짜를 월,일 단위로 preference에 저장한다. 접근 key값은 ex)base201208
    //년 월 정보 가져오기, 해당 달은 몇일인지 31,30,29,28일 구한 뒤 mDateInfoList에 저장한다.
    public void setLunaOnThePreference(String year, String month) {
        //연, 월 데이터 저장
        mYear = year;
        mMonth = month;

        int intYear = Integer.parseInt(year);
        int intMonth = Integer.parseInt(month);

        //정보 불러와서 추가하기로 접근해야 한다.
        //BaseDateInfoList 클래스 형태로 저장 및 불러오기를 한다.

        //Preference에 날씨 정보 객체 불러오기
        Log.i(TAG, "year_month key : " + year + month);
        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //preference에 저장되어있는 날짜 정보 가져오기
        //preference가 null일 경우 처리할것
        BaseDateInfoList baseDateInfoList = new BaseDateInfoList();

        //null체크를 통과 못하니 하나 넣는다...
//        BaseDateInfoList forNull = new BaseDateInfoList();
//        BaseDateInfo forNullObj = new BaseDateInfo();
//        forNullObj.setNameOfDay("");
//        ArrayList<BaseDateInfo> tempList = new ArrayList<>();
//        tempList.add(forNullObj);
//        forNull.setBaseDateInfoList(tempList);
//
//        setDateInfoToSP(forNull, year, month);
//
        baseDateInfoList = getDateInfoFromSP(year, month);

        if (baseDateInfoList==null||!(baseDateInfoList.getBaseDateInfoList().size()>20)) {
            baseDateInfoList = new BaseDateInfoList();
            //null일시(최초로 불러올 시), 해당 리스트 메모리를 할당한다.

            //음력 계산 및 계산된 음력 날짜 리스트 생성
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.YEAR, intYear);
            calendar.set(Calendar.MONTH, intMonth - 1);

            ArrayList<String> lunaList = new ArrayList<>();
            Log.i(TAG, String.valueOf(Calendar.DAY_OF_MONTH));
            Log.i(TAG, String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1));
            Log.i(TAG, String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)));
            int a = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1;
            for (int i = 1; i < a; i++) {
                String date = String.valueOf(i);

                if (date.length() < 2) {
                    date = "0" + date;
                }
                //양력문자열 년월일 yyyymmdd 완성
                String temp = year + month + date;
                //음력 계산
                temp = LunaCalendar.Solar2Lunar(temp);
                temp = temp.substring(4);

                //구조체를 만들어 리스트에 넣는다.
                BaseDateInfo baseDateInfo = new BaseDateInfo();
                //
                baseDateInfo.setDate(Integer.parseInt(date));
                baseDateInfo.setLuna(temp);

                // i+1일 의 데이터가 들어간다 ex) 13일은 리스트의 12번째에 들어간다.(앞에 0 때문에)
                baseDateInfoList.getBaseDateInfoList().add(i - 1, baseDateInfo);
            }

            //1일 언제 시작하는지 계산 후 빈칸 추가
            int dayNum = check1stDayOfTheMonth(year, month);
            baseDateInfoList = setting1stDay(baseDateInfoList, dayNum);
            //요일 추가
            baseDateInfoList = settingDay(baseDateInfoList);

            //데이터 리스트 음력 추가 완료

            setDateInfoToSP(baseDateInfoList, year, month);
        }
    }

    public void arrangerHoliday(String year, String month) {

        getFromholidayApi(year, month);
    }

    //해당 년, 월 정보를 받아와서 그 달의 공휴일 정보를 공휴일 이름, 몇일인지 저장한다.
    //공휴일 Api로부터 데이터를 받아와서 공휴일 이름, 몇일인지 데이터를 구하고 해당 일자에 저장한다.
    public void setHolidayOnPreference() {

        int intYear = Integer.parseInt(mYear);
        int intMonth = Integer.parseInt(mMonth);

        //정보 불러와서 추가하기로 접근해야 한다.
        //BaseDateInfoList 클래스 형태로 저장 및 불러오기를 한다.

        //Preference에 날씨 정보 객체 불러오기

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //preference에 저장되어있는 날짜 정보 가져오기
        //preference가 null일 경우 처리할것
        BaseDateInfoList baseDateInfoList = getDateInfoFromSP(mYear, mMonth);

        //기존의 가져온 데이터에서 탐색을 해서,
        //해당 날짜가 같은 곳에 공휴일을 추가로 집어넣는다.
        //mBaseDateInfoList는 공휴일 정보 저장 리스트.
        for (int i = 0; i < mBaseDateInfoList.getBaseDateInfoList().size(); i++) {
            //받아온 공휴일 리스트에서 날짜를 가져온다.
            int temp = mBaseDateInfoList.getBaseDateInfoList().get(i).getDate();

            for (int j = 0; j < baseDateInfoList.getBaseDateInfoList().size(); j++) {
                //공휴일 날짜(temp) =
                if (temp == baseDateInfoList.getBaseDateInfoList().get(j).getDate()) {
                    baseDateInfoList.getBaseDateInfoList().get(j).setNameOfDay(mBaseDateInfoList.getBaseDateInfoList().get(i).getNameOfDay());
                }
            }

        }

        //데이터 리스트 음력 추가 완료

        setDateInfoToSP(baseDateInfoList, mYear, mMonth);

    }

    private void getFromholidayApi(String year, String month) {
        Log.i(TAG, "starting : getFromholidayApi");

        String serviceKey = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        String type = "json";

        CalendarService calendarService = new CalendarService(this);
        calendarService.getRestInfo(year, month, serviceKey, type);

    }


    @Override
    public void validateSuccess(boolean isSuccess, ResponseParams responseParams) {
        //데이터를 가져와서 preference 해당 위치에 넣기 - 이름, 날짜


        //클래스 하나 만들어서 데이터 저장시키고
        BaseDateInfo baseDateInfo = new BaseDateInfo();

        for (int i = 0; i < responseParams.getResponse().getBody().getItems().getItem().size(); i++) {
            //이름 넣고
            baseDateInfo.setNameOfDay(responseParams.getResponse().getBody().getItems().getItem().get(i).getDateName());
            //날짜 넣고
            int temp = responseParams.getResponse().getBody().getItems().getItem().get(i).getLocdate();
            temp %= 100;
            baseDateInfo.setDate(temp);
            //데이터 저장한 리스트 만들기
            mBaseDateInfoList.getBaseDateInfoList().add(baseDateInfo);
            baseDateInfo = new BaseDateInfo();
        }

        if (mBaseDateInfoList.getBaseDateInfoList().size() > 0) {
            setHolidayOnPreference();
        }

    }

    @Override
    public void validateSuccessSingle(boolean isSuccess, ResponseSingle responseSingle) {
        //데이터를 가져와서 preference 해당 위치에 넣기 - 이름, 날짜


        //클래스 하나 만들어서 데이터 저장시키고
        BaseDateInfo baseDateInfo = new BaseDateInfo();

        //이름 넣고
        baseDateInfo.setNameOfDay(responseSingle.getResponse().getBody().getItems().getItem().getDateName());
        //날짜 넣고
        int temp = responseSingle.getResponse().getBody().getItems().getItem().getLocdate();
        temp %= 100;
        baseDateInfo.setDate(temp);
        //데이터 저장한 리스트 만들기
        mBaseDateInfoList.getBaseDateInfoList().add(baseDateInfo);

        if (mBaseDateInfoList.getBaseDateInfoList().size() > 0) {
            setHolidayOnPreference();
        }

    }

    @Override
    public void validateFailure(String message) {
        //공휴일이 1달에 하루 뿐이라, 리스트가 아닌, 하나의 객체로 여기로 떨어지는 경우.
        Log.i(TAG, "starting : vlidateFailure");

        String serviceKey = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        String type = "json";

        CalendarService calendarService = new CalendarService(this);
        calendarService.getRestInfoForOneDay(mYear, mMonth, serviceKey, type);
    }

    @Override
    public void validateFaliureSingle(String message) {

        Log.i(TAG, "starting : validateFailureSingle. fin.");


    }


    private void makeDummySchedule() {
        //preference 동작 준비
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //더미 스케쥴
        String dummy_year = "2021";
        String dummy_month_aug = "08";
        String dummy_month_sep = "09";
        String dummy_date_1 = "10";
        String dummy_date_2 = "20";
        String dummy_date_3 = "11";
        String dummy_date_4 = "12";
        String dummy_where = "서울특별시 성동구 성수골";
        String dummy_where2 = "전라북도 익산시 월성동";
        String dummy_who = "홍고은";
        String dummy_who2 = "장석우";
        ArrayList<String> whoList = new ArrayList<>();
        whoList.add(dummy_who);
        whoList.add(dummy_who2);

        Schedule dum1 = new Schedule();
        Schedule dum2 = new Schedule();
        Schedule dum3 = new Schedule();
        Schedule dum4 = new Schedule();

        dum1.setYear(dummy_year);
        dum1.setMonth(dummy_month_aug);
        dum1.setDate(dummy_date_1);
        dum1.setWhere(dummy_where);
        dum1.setWho(whoList);

        dum2.setYear(dummy_year);
        dum2.setMonth(dummy_month_aug);
        dum2.setDate(dummy_date_2);
        dum2.setWhere(dummy_where2);
        dum2.setWho(whoList);

        dum3.setYear(dummy_year);
        dum3.setMonth(dummy_month_sep);
        dum3.setDate(dummy_date_3);

        dum4.setYear(dummy_year);
        dum4.setMonth(dummy_month_sep);
        dum4.setDate(dummy_date_4);

        //친구정보는 리스트로 받아야한다!
        BaseDateInfoList baseDateInfoList = new BaseDateInfoList();

        //리스트를 불러옴
        baseDateInfoList = getDateInfoFromSP("2021", "08");

        setScheduleInDateList(baseDateInfoList, dum1);

        setScheduleInDateList(baseDateInfoList, dum2);

        setDateInfoToSP(baseDateInfoList, "2021", "08");

        setLunaOnThePreference("2021","09");

        baseDateInfoList = getDateInfoFromSP("2021", "09");

        setScheduleInDateList(baseDateInfoList, dum3);

        setScheduleInDateList(baseDateInfoList, dum4);

        setDateInfoToSP(baseDateInfoList, "2021", "09");
        //더미 preference 저장 완료

        //스케쥴에도 저장해야한다.
        ScheduleList scheduleList = new ScheduleList();

        ArrayList<Schedule> temp = new ArrayList<>();
        //null처리
        scheduleList.setScheduleArrayList(temp);

        //scheduleList = getScheduleFromSp();

        scheduleList.getScheduleArrayList().add(dum1);
        scheduleList.getScheduleArrayList().add(dum2);

        setScheduleInToSp(scheduleList);
    }

    private void setScheduleInToSp(ScheduleList scheduleList) {

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        //Preference에 정보 객체 저장하기
        //JSON으로 변환
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //초기화
        //editor.remove(year + month);

        editor.putString("schedule", jsonString);
        editor.commit();
        //저장완료

    }

    private ScheduleList getScheduleFromSp() {

        //Preference에 날씨 정보 객체 불러오기

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null일 경우 처리할것.
        String loaded = pref.getString("schedule", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference에 저장된 데이터 class 형태로 불러오기 완료

        return scheduleList;

    }

    private BaseDateInfoList setScheduleInDateList(BaseDateInfoList baseDateInfoList, Schedule dum1) {
        //스케쥴 날짜
        int temp = Integer.parseInt(dum1.getDate());

        //더미 데이터 세팅
        //해당 일자
        for (int i = 0; i < baseDateInfoList.getBaseDateInfoList().size(); i++) {
            //받아온 공휴일 리스트에서 날짜를 가져온다.
            int temp2 = baseDateInfoList.getBaseDateInfoList().get(i).getDate();

            //스케쥴 날짜(temp) = 해당 월의 날짜,음력 데이터가 들어있는 리스트에서의 양력 날짜 구조체에 추가
            if (temp == temp2) {
                baseDateInfoList.getBaseDateInfoList().get(i).setSchedule(dum1);
            }
        }
        return baseDateInfoList;
    }

}
