package com.example.weatherkok.datalist.data;

import android.util.Log;

import com.example.weatherkok.datalist.data.wxdata.Wx;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
// 스케줄 정보를 담는 스케줄데이터 클래스. 날짜, 위치, 날씨예보에 관한 데이터
public class ScheduleData {
    private static final String TAG = ScheduleData.class.getSimpleName();
    private static String Day;
    //약속날짜
    String scheduledDate;
    //약속위치
    //나중에 Address 클래스로 변경할지 정할 것
    String place;
    //날씨 예보 리스트
    Wx Fcst = new Wx();


    public ScheduleData() {
    }

    public ScheduleData(String date, String address, Wx fcst) throws Exception {
        this.scheduledDate = date;
        this.place = address;
        Fcst = fcst;

        getDateDay(scheduledDate);
    }

    //getter/setter 함수를 구현
    public String getDay() {
        return Day;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;

        try {
            getDateDay(scheduledDate);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,"요일계산이 잘못 되었습니다");
        }
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Wx getFcst() {
        return Fcst;
    }

    public void setFcst(Wx fcst) {
        Fcst = fcst;
    }

    /**
     * 특정 날짜에 대하여 요일을 구함(일 ~ 토)
     * @param date
     * @throws Exception
     */
    public void getDateDay(String date) throws Exception {

        String day = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date nDate = dateFormat.parse(date);

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
        Day = day;
    }

}
