package com.example.weatherkok.where.models;

import com.google.gson.annotations.SerializedName;

public class Properties {

    String ctp_eng_nm;

    String ctprvn_cd;

    String ctp_kor_nm;

    @SerializedName("full_nm")
    String full_nm;

    String sig_kor_nm;

    String emd_kor_nm;

    public String getSig_kor_nm() {
        return sig_kor_nm;
    }

    public void setSig_kor_nm(String sig_kor_nm) {
        this.sig_kor_nm = sig_kor_nm;
    }

    public String getEmd_kor_nm() {
        return emd_kor_nm;
    }

    public void setEmd_kor_nm(String emd_kor_nm) {
        this.emd_kor_nm = emd_kor_nm;
    }

    public String getFull_nm() {
        return full_nm;
    }

    public void setFull_nm(String full_nm) {
        this.full_nm = full_nm;
    }

    public String getCtp_eng_nm() {
        return ctp_eng_nm;
    }

    public void setCtp_eng_nm(String ctp_eng_nm) {
        this.ctp_eng_nm = ctp_eng_nm;
    }

    public String getCtprvn_cd() {
        return ctprvn_cd;
    }

    public void setCtprvn_cd(String ctprvn_cd) {
        this.ctprvn_cd = ctprvn_cd;
    }

    public String getCtp_kor_nm() {
        return ctp_kor_nm;
    }

    public void setCtp_kor_nm(String ctp_kor_nm) {
        this.ctp_kor_nm = ctp_kor_nm;
    }
}
