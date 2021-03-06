package com.devpilot.weatherkok.datalist.data.wxdata;

import com.devpilot.weatherkok.weather.models.midTemp.TempItems;
import com.devpilot.weatherkok.weather.models.midWx.WxItems;
import com.devpilot.weatherkok.weather.models.shortsExpectation.todayTemp;
import com.devpilot.weatherkok.weather.models.shortsExpectation.todayWx;

import java.util.ArrayList;

public class Wx {
    private static final String TAG = Wx.class.getSimpleName();
    //날씨예보 클래스
    //date "yyyy-MM-dd hh:mm:ss"
    //오늘날짜
    String wxCheckday;
    String tempCheckDay;
    //오늘날짜 기준으로 +1+2+3... 하면됨
    WxItems wxList = new WxItems();
    TempItems tempList = new TempItems();
    ArrayList<todayTemp> tempToday = new ArrayList<>();
    ArrayList<todayWx> wxToday = new ArrayList<>();

    public ArrayList<todayWx> getWxToday() {
        return wxToday;
    }

    public void setWxToday(ArrayList<todayWx> wxToday) {
        this.wxToday = wxToday;
    }

    public ArrayList<todayTemp> getTempToday() {
        return tempToday;
    }

    public void setTempToday(ArrayList<todayTemp> tempToday) {
        this.tempToday = tempToday;
    }

    public Wx() {
    }

    public Wx(String wxCheckday, String tempCheckDay, WxItems wxList, TempItems tempList) {
        this.wxCheckday = wxCheckday;
        this.tempCheckDay = tempCheckDay;
        this.wxList = wxList;
        this.tempList = tempList;
    }

    public String getWxCheckday() {
        return wxCheckday;
    }

    public void setWxCheckday(String wxCheckday) {
        this.wxCheckday = wxCheckday;
    }

    public String getTempCheckDay() {
        return tempCheckDay;
    }

    public void setTempCheckDay(String tempCheckDay) {
        this.tempCheckDay = tempCheckDay;
    }

    public WxItems getWxList() {
        return wxList;
    }

    public void setWxList(WxItems wxList) {
        this.wxList = wxList;
    }

    public TempItems getTempList() {
        return tempList;
    }

    public void setTempList(TempItems tempList) {
        this.tempList = tempList;
    }
}
