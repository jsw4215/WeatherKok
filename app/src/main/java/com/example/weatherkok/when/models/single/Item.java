package com.example.weatherkok.when.models.single;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {
    private static final String TAG = Item.class.getSimpleName();
    @SerializedName("dateKind")
    @Expose
    private String dateKind;
    @SerializedName("dateName")
    @Expose
    private String dateName;
    @SerializedName("isHoliday")
    @Expose
    private String isHoliday;
    @SerializedName("locdate")
    @Expose
    private Integer locdate;
    @SerializedName("seq")
    @Expose
    private Integer seq;

    public String getDateKind() {
        return dateKind;
    }

    public void setDateKind(String dateKind) {
        this.dateKind = dateKind;
    }

    public String getDateName() {
        return dateName;
    }

    public void setDateName(String dateName) {
        this.dateName = dateName;
    }

    public String getIsHoliday() {
        return isHoliday;
    }

    public void setIsHoliday(String isHoliday) {
        this.isHoliday = isHoliday;
    }

    public Integer getLocdate() {
        return locdate;
    }

    public void setLocdate(Integer locdate) {
        this.locdate = locdate;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }


}
