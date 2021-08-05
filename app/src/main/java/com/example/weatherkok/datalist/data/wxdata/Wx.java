package com.example.weatherkok.datalist.data.wxdata;

import com.example.weatherkok.datalist.data.wxdata.am.AM;
import com.example.weatherkok.datalist.data.wxdata.pm.PM;

import java.util.Date;

public class Wx {
    private static final String TAG = Wx.class.getSimpleName();
    //날씨예보 클래스
    //date "yyyy-MM-dd hh:mm:ss"
    Date date;
    AM am;
    PM pm;

    public Wx() {
    }

    public Wx(Date date, AM am, PM pm) {
        this.date = date;
        this.am = am;
        this.pm = pm;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AM getAm() {
        return am;
    }

    public void setAm(AM am) {
        this.am = am;
    }

    public PM getPm() {
        return pm;
    }

    public void setPm(PM pm) {
        this.pm = pm;
    }
}
