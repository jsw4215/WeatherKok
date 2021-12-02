package com.devpilot.weatherkok.where.models;

public class PropertiesSgg {
    private static final String TAG = PropertiesSgg.class.getSimpleName();

    String sig_cd;
    String sig_eng_nm;
    String full_nm;
    String sig_kor_nm;

    public static String getTAG() {
        return TAG;
    }

    public String getSig_cd() {
        return sig_cd;
    }

    public void setSig_cd(String sig_cd) {
        this.sig_cd = sig_cd;
    }

    public String getSig_eng_nm() {
        return sig_eng_nm;
    }

    public void setSig_eng_nm(String sig_eng_nm) {
        this.sig_eng_nm = sig_eng_nm;
    }

    public String getFull_nm() {
        return full_nm;
    }

    public void setFull_nm(String full_nm) {
        this.full_nm = full_nm;
    }

    public String getSig_kor_nm() {
        return sig_kor_nm;
    }

    public void setSig_kor_nm(String sig_kor_nm) {
        this.sig_kor_nm = sig_kor_nm;
    }
}
