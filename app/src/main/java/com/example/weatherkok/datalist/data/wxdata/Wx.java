package com.example.weatherkok.datalist.data.wxdata;

import com.example.weatherkok.weather.models.midTemp.MidTempResponse;
import com.example.weatherkok.weather.models.midTemp.TempItems;
import com.example.weatherkok.weather.models.midWx.WxItems;
import com.example.weatherkok.weather.models.midWx.WxResponse;

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
