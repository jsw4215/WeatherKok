package com.example.weatherkok.weather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.util.Log;

import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.datalist.data.wxdata.am.AM;
import com.example.weatherkok.datalist.data.wxdata.pm.PM;
import com.example.weatherkok.weather.WeatherService;
import com.example.weatherkok.weather.interfaces.WeatherContract;
import com.example.weatherkok.weather.models.LatLon;
import com.example.weatherkok.weather.models.midTemp.MidTempResponse;
import com.example.weatherkok.weather.models.midTemp.TempItem;
import com.example.weatherkok.weather.models.midWx.WxItem;
import com.example.weatherkok.weather.models.midWx.WxResponse;
import com.example.weatherkok.weather.models.shortsExpectation.ShortsResponse;
import com.example.weatherkok.weather.models.xy;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class WxKokDataPresenter implements WeatherContract.ActivityView {
    private static final String TAG = WxKokDataPresenter.class.getSimpleName();

    Context mContext;
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ArrayList<String> mTargetPlaces = new ArrayList<>();
    ArrayList<String> mCodeList = new ArrayList<>();
    String mToday;
    ArrayList<xy> xyList;

    public WxKokDataPresenter(Context mContext) {
        this.mContext = mContext;
    }

    //스케쥴장소별!! 날씨 데이터 단기예보 preference에 저장

    //1. 스케쥴을 검색해 장소를 가져온다.
    //2. 장소 기준으로 api를 연동한다.
    //3. 도착한 데이터를 리턴한다.

    public void getScheduleDateWxApi() {

        //주소 가져오기
        mTargetPlaces = getPlaceOfSchedule();
        //주소로 중기예보 지역 코드 가져오기
        mCodeList = manageDataForMidWxExpectation(mTargetPlaces);
        //코드로 api를 이용해 데이터 가져오기

        mToday = calculateToday();

        String requestToDay = mToday + "0600";

        for (int i = 0; i < mCodeList.size(); i++) {
            midWxService(mCodeList.get(i), requestToDay);
            midTempService(mCodeList.get(i), requestToDay);
        }
        //중기예보 데이터 도착 확인
        //중기예보의 retrofit null 아니면 그대로 보내는거 단기예보가 먼저 출발해버리면 흐트러질 수 있으니 나중에 생각해볼것

        //단기예보 데이터보내기
        sendDateForShortsWx();

        Log.i(TAG, "fin.");

    }

    //단기예보는 그때 그때 쏴야함

    private ArrayList<xy> getXYlist(ArrayList<String> mTargetPlaces) {

        ArrayList<xy> temp = new ArrayList<>();
        xy tempObj = new xy();
        for (int i = 0; i < mTargetPlaces.size(); i++) {
            Map<String, Object> xyMap = getXYWithCalculator(mTargetPlaces.get(0));

            String nx = String.valueOf(xyMap.get("x"));
            String ny = String.valueOf(xyMap.get("y"));
            String lat = String.valueOf(xyMap.get("lat"));
            String lon = String.valueOf(xyMap.get("lng"));

            tempObj.setX(nx);
            tempObj.setY(ny);
            tempObj.setLon(lon);
            tempObj.setLat(lat);

            temp.add(tempObj);

        }

        return temp;

    }

    private void sendDateForShortsWx() {

        xyList = new ArrayList<xy>();

        xyList = getXYlist(mTargetPlaces);

        //주소로 nx, ny 구하기
        //단기예보
        for (int i = 0; i < xyList.size(); i++) {
            shortWxService(calculateToday(),"0500", xyList.get(i));
        }
    }

    // 등록된 스케쥴에서 위치정보 가져와서
    // 도단위 시단위 찾아서
    // api연결
    private ArrayList<String> getPlaceOfSchedule() {

        ScheduleList scheduleList = getScheduleFromSp();

        ArrayList<Schedule> list = scheduleList.getScheduleArrayList();

        ArrayList<String> place = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            place.add(list.get(i).getWhere());
        }

        return place;
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

    private void setScheduleDataInToSp(ScheduleList scheduleList) {

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


    //단기예보에서 최고기온, 최저기온 뽑아내는 알고리즘

    //단기예보에서 현재 날씨 오전, 오후로 결정짓는 알고리즘

    //중기예보 저장

    //중기기온 저장

    //현재위치의~ 반복 preference 저장


    //API연동

    private Map<String, Object> getXYWithCalculator(String s) {

        LatLonCalculator latLonCalculator = new LatLonCalculator();

        LatLon latLon = latLonCalculator.getLatLonWithAddr(mTargetPlaces.get(0), mContext);

        Map<String, Object> result = latLonCalculator.getGridxy(latLon.getLat(), latLon.getLon());

        return result;
    }

    private Date getToday() {
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
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

    private void midWxService(String placeCode, String Date) {
        //중기예보
        //지역 정보 도단위, 시단위
        String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        int numOfRows = 10;
        int pageNo = 1;
        String dataType = "JSON";
        String regId = placeCode;
        String tmFc;
        tmFc = Date;

        WeatherService weatherService = new WeatherService(this);
        weatherService.getMidLandFcst(key, numOfRows, pageNo, dataType, regId, tmFc);
    }

    private void shortWxService(String Date,String time, xy data) {
        //단기예보
        //지역 정보 도단위, 시단위
        //Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회) 10분뒤 api사용 가능
        String key = "Lhp0GWghhWvVn4aUZSfe1rqUFsdQkNLvT+ZLt5RNHiFocjZjrbruHxVFaiKBOmTnOypgiM7WqtCcWTSLbAmIeA==";
        int numOfRows = 800;
        int pageNo = 1;
        String dataType = "JSON";
        String baseDate = Date;
        String baseTime = "0500";

        WeatherService weatherService = new WeatherService(this);
        weatherService.getShortFcst(key, numOfRows, pageNo, dataType, baseDate, time, data);
    }

    private void midTempService(String placeCode, String Date) {
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

        WeatherService weatherService = new WeatherService(this);
        weatherService.getMidTemp(key, numOfRows, pageNo, dataType, regId, tmFc);
    }

    private ArrayList<String> manageDataForMidWxExpectation(ArrayList<String> arrayList) {


        ArrayList<String> codeList = new ArrayList<>();

        for (int i = 0; i < arrayList.size(); i++) {
            codeList.add(getPlaceCode(arrayList.get(i)));
        }

        Log.i(TAG, "code : " + codeList.get(0) + " " + arrayList.get(0));
        Log.i(TAG, "code : " + codeList.get(1) + " " + arrayList.get(1));


        return codeList;
    }

    private String getPlaceCode(String place) {

        String Code = "";

        if (place.startsWith("서울") || place.startsWith("인천") || place.startsWith("경기도")) {
            Code = "11B00000";
        } else if (place.startsWith("강원도영서")) {
            Code = "11D10000";
        } else if (place.startsWith("강원도영동")) {
            Code = "11D20000";
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

        if (code.equals("11B10101")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("서울")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11B20201")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("인천")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11C20404")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("세종")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11C20401")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("대전")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11G00201")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("제주")) {
                    if (!scheduleList.getScheduleArrayList().get(i).getWhere().contains("서귀포")) {
                        //해당위치에 날씨 삽입
                        scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                                .setTempList(midTempResponse.getResponse().getBody().getItems());
                        //해당 위치에 오늘 날짜(기준) 삽입
                        scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                    }
                }
            }
        } else if (code.equals("11G00401")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("제주")) {
                    if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("서귀포")) {
                        //해당위치에 날씨 삽입
                        scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                                .setTempList(midTempResponse.getResponse().getBody().getItems());
                        //해당 위치에 오늘 날짜(기준) 삽입
                        scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                    }
                }
            }
        } else if (code.equals("11F20501")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("광주")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11H20201")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("부산")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11H20101")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("울산")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11H10701")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("대구")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11B20601")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("수원")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11B20305")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("파주")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11D10301")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("춘천")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11D10401")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("원주")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11D20501")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("강릉")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11C20101")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("서산")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11C10301")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().startsWith("청주")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("21F20801")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("목포")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11F20401")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("여수")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("21F10501")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("군산")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11F10201")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("전주")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11H20301")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("창원")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11H10501")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("안동")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        } else if (code.equals("11H10201")) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                if (scheduleList.getScheduleArrayList().get(i).getWhere().contains("포항")) {
                    //해당위치에 날씨 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst()
                            .setTempList(midTempResponse.getResponse().getBody().getItems());
                    //해당 위치에 오늘 날짜(기준) 삽입
                    scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().setTempCheckDay(mToday);
                }
            }
        }

        return scheduleList;

    }


    private void getMidTemp() {

        //중기기온 API

    }

    private void getShorts() {

        //단기예보 API

    }

    //중기예보 날씨와 스케쥴 데이터를 병합하여 preference에 다시 저장
    private void midWxIntoPref(WxResponse wxResponse) {

        ScheduleList tempSchedule = new ScheduleList();

        tempSchedule = getScheduleFromSp();

        //날씨정보와 중기예보 정보 합치기
        tempSchedule = mergingWxDataAndSchedule(tempSchedule, wxResponse);

        setScheduleDataInToSp(tempSchedule);

    }

    private void midTempIntoPref(MidTempResponse midTempResponse) {

        ScheduleList tempSchedule = new ScheduleList();

        tempSchedule = getScheduleFromSp();

        //날씨정보와 중기예보 정보 합치기
        tempSchedule = mergingMidTempAndSchedule(tempSchedule, midTempResponse);

        setScheduleDataInToSp(tempSchedule);

    }

    private String arrangeAddressResults(Address address) {

        String addr = "";
        ArrayList<String> temp = new ArrayList<>();

        temp.add(address.getAdminArea());
        temp.add(address.getSubAdminArea());
        temp.add(address.getLocality());
        temp.add(address.getSubLocality());
        temp.add(address.getThoroughfare().substring(0, 2));

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
    public void validateSuccess(boolean isSuccess, WxResponse wxResponse) {

        Log.i(TAG, "data도착 : " + wxResponse.getBody().getItems().getItem().get(0).getRegId());
        Log.i(TAG, "data도착 : " + wxResponse.getBody().getItems().getItem().get(0).getWf4Am());
        midWxIntoPref(wxResponse);

    }

    @Override
    public void validateShortSuccess(boolean isSuccess, ShortsResponse shortsResponse, String lat, String lon) {


        Log.i(TAG, "단기예보 도착!!" + shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstTime());
        //lat,lon을 가져오기

        //주소로 변환
        LatLonCalculator calculator = new LatLonCalculator();
        Address address = calculator.getAddressWithLatLon(lat, lon, mContext);
        String addrResult = arrangeAddressResults(address);

        setShortsDataIntoPref(shortsResponse, addrResult);

        //단기예보 로직 구현
        //데이터 집어넣기

    }

    private ScheduleData checkTempMaxMin(ShortsResponse shortsResponse,ScheduleData scheduleData) {
        //같은 날짜 애들 가져와서,

        String tomorrow = getFutureDay("yyyyMMdd", 1);
        String dayAfterTmr = getFutureDay("yyyyMMdd", 2);
        ArrayList<Integer> tempList = new ArrayList<>();
        ArrayList<Integer> tmrList = new ArrayList<>();
        ArrayList<Integer> datList = new ArrayList<>();


        for (int i = 0; i < shortsResponse.getResponse().getBody().getItems().getItem().size(); i++) {
            //오늘
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstDate().equals(mToday)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();
                int intTime = Integer.parseInt(time);
                //오전
                //if (intTime < 1200) {

                    //오늘 오전의 기온
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("TMP")) {

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

        //오늘 최대 최저기온 저장
        scheduleData=setMinMaxTemp(tempList, tmrList, datList);

        return scheduleData;

    }

    //3일간의 최대 최저기온 넣은 scheduleData
    private ScheduleData setMinMaxTemp(ArrayList<Integer> tempList, ArrayList<Integer> tmrList, ArrayList<Integer> datList) {

        int max = Collections.max(tempList);

        int min = Collections.min(tempList);

        ScheduleData scheduleData = new ScheduleData();

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

    private void setShortsDataIntoPref(ShortsResponse shortsResponse, String addrResult) {


        //단기예보 기온 최대, 최소 구하기
        ScheduleData scheduleData = new ScheduleData();

        scheduleData = checkTempMaxMin(shortsResponse, scheduleData);

        //날씨 로직 돌리기
        scheduleData = checkWxShorts(shortsResponse, scheduleData);

        //response의 주소와 string 주소 매칭 후 삽입
        ScheduleList scheduleList = getScheduleFromSp();

        scheduleList = matchingAdderess(scheduleList, addrResult, scheduleData);

        setScheduleDataInToSp(scheduleList);
    }

    //스케쥴과 날씨 데이터를 주소 장소로 매칭하여 정보를 집어 넣는다.
    private ScheduleList matchingAdderess(ScheduleList scheduleList, String addrResult, ScheduleData scheduleData) {


        for(int i=0;i<scheduleList.getScheduleArrayList().size();i++) {

            TempItem dum = new TempItem();

            scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().add(dum);

            //스케쥴의 주소가 데이터의 주소를 포함한다면~ 으로 알고리즘 선택
            if (scheduleList.getScheduleArrayList().get(i).getWhere().contains(addrResult)){
                Log.i(TAG, "matcingAddress Algo");
                //기상청이 하루에 두번 정보를 제공하므로 그시간에 돌아가게 만들면 되니, 여기서 중복체크 알고리즘을 할 필요는 없다. 아침 6시 저녁 18시
                //리스트에 데이터 추가, 기존의 데이터가 있으니 하나하나 집어넣어 줘야함.
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMin0(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMin0()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMax0(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMax0()
                );
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMin0(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMin1()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMax0(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMax1()
                );
                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMin0(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMin2()
                );

                scheduleList.getScheduleArrayList().get(i).getScheduleData().getFcst().getTempList().getItem().get(0).setTaMax0(
                        scheduleData.getFcst().getTempList().getItem().get(0).getTaMax2()
                );

                //날씨정보세팅
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

        scheduleData = shortsWxAlgorithm(shortsResponse, scheduleData, tomorrow);

        scheduleData = shortsWxAlgorithm(shortsResponse, scheduleData, dayAfterTmr);

        return scheduleData;


    }

    public ScheduleData shortsWxAlgorithm(ShortsResponse shortsResponse, ScheduleData scheduleData, String date) {

        WxItem dum = new WxItem();
        scheduleData.getFcst().getWxList().getItem().add(dum);

        for (int i = 0; i < shortsResponse.getResponse().getBody().getItems().getItem().size(); i++) {
            //오늘
            if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getBaseDate().equals(date)) {

                String time = shortsResponse.getResponse().getBody().getItems().getItem().get(i).getFcstTime();
                int intTime = Integer.parseInt(time);
                //오전
                if (intTime < 1200) {

                    //오늘 오전의 강수확률, 하늘상태, 강수형태
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //강수확률
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt0Am(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //강수타입
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt0AmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        //하늘상태
                        if (Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstValue()) == 3) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Am("구름많음");
                        } else if (Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstValue()) == 4) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Am("흐림");
                        } else if (Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstValue()) == 1) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Am("맑음");
                        }
                    }

                } else {
                    //오늘 오후의 기온
                    //오늘 오전의 강수확률, 하늘상태, 강수형태
                    if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("POP")) {
                        //강수확률
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt0Pm(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("PTY")) {
                        //강수타입
                        scheduleData.getFcst().getWxList().getItem().get(0).setRnSt0PmType(
                                Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstValue()));
                    } else if (shortsResponse.getResponse().getBody().getItems().getItem().get(i).getCategory().equals("SKY")) {
                        //하늘상태
                        if (Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstValue()) == 3) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Pm("구름많음");
                        } else if (Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstValue()) == 4) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Pm("흐림");
                        } else if (Integer.parseInt(shortsResponse.getResponse().getBody().getItems().getItem().get(0).getFcstValue()) == 1) {
                            scheduleData.getFcst().getWxList().getItem().get(0).setWf0Pm("맑음");
                        }
                    }
                }
            }

        }
        return scheduleData;
    }

    @Override
    public void validateFailure(String message, String regId) {

        Log.e(TAG, "중기예보 실패");
        //코드로 api를 이용해 데이터 가져오기
        midWxService(regId, getFutureDay("yyyyMMdd",-1) + "1800");
    }

    @Override
    public void validateShortFailure(String message, xy data) {
        Log.e(TAG, "단기예보 실패");
        shortWxService(getFutureDay("yyyyMMdd",-1), "1700", data);

    }

    @Override
    public void validateMidTempSuccess(MidTempResponse midTempResponse) {
        Log.e(TAG, "중기기온 성공");
    }

    @Override
    public void validateMidTempFailure(String message, String regId) {
        Log.e(TAG, "중기기온 실패");
        midTempService(regId, getFutureDay("yyyyMMdd",-1)+"0600");
    }

}
