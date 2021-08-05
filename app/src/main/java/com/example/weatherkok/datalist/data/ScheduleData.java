package com.example.weatherkok.datalist.data;

import android.location.Address;

import com.example.weatherkok.datalist.data.wxdata.Wx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleData {
    private static final String TAG = ScheduleData.class.getSimpleName();
    private static String Day;
    //약속날짜
    Date date;
    //약속위치
    //나중에 Address 클래스로 변경할지 정할 것
    String address;
    //날씨 예보 리스트
    ArrayList<Wx> Fcst;

    public ScheduleData() {
    }

    public ScheduleData(Date date, String address, ArrayList<Wx> fcst) {
        this.date = date;
        this.address = address;
        Fcst = fcst;
    }

    public static String getDay() {
        return Day;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<Wx> getFcst() {
        return Fcst;
    }

    public void setFcst(ArrayList<Wx> fcst) {
        Fcst = fcst;
    }

    /**
     * 특정 날짜에 대하여 요일을 구함(일 ~ 토)
     * @param date
     * @return
     * @throws Exception
     */
    public static String getDateDay(String date) throws Exception {

        String day = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
        return day;
    }

}
