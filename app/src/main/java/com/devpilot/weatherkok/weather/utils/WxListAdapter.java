package com.devpilot.weatherkok.weather.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.datalist.data.ScheduleData;
import com.devpilot.weatherkok.weather.WxListActivity;
import com.devpilot.weatherkok.when.models.ScheduleList;
import com.devpilot.weatherkok.when.utils.RecyclerViewAdapter;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WxListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    String TAG = "BookMarkAdapter";
    String mYearMonth;
    Context mContext;
    ScheduleList scheduleList;
    public static final int TRASH = 1;
    public static final int NORMAL = 0;

    ArrayList<Boolean> deleteList = new ArrayList<>();
    private SparseBooleanArray selectedArray = new SparseBooleanArray();


    public WxListAdapter(Context context, ScheduleList List) {
        Log.i(TAG, "Constructor");
        this.mContext = context;
        this.scheduleList = List;
        if(scheduleList!=null&&scheduleList.getScheduleArrayList()!=null) {
            for (int i = 0; i < scheduleList.getScheduleArrayList().size(); i++) {
                deleteList.add(false);
            }
        }
    }

    public ArrayList<Boolean> getDeleteList() {
        return deleteList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        if(type==TRASH){

            Log.i(TAG, "onCreateViewHolder TRASH");
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_weather_del_list, viewGroup, false);
            return new DelViewHolder(view);

        }else {

            Log.i(TAG, "onCreateViewHolder NORMAL");
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_weather_list, viewGroup, false);
            return new ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof WxListAdapter.ViewHolder) {
            setDate(holder, position);

            setLocation(holder, position);

            setWxImg(holder, position);
        } else {
            setDelDate(holder, position);

            setDelLocation(holder, position);

            setDelWxImg(holder, position);

            ((DelViewHolder)holder).rbBmList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((DelViewHolder)holder).rbBmList.isSelected()){
                        ((DelViewHolder)holder).rbBmList.setChecked(false);
                        ((DelViewHolder)holder).rbBmList.setSelected(false);
                        deleteList.set(position,false);
                    }else{
                        ((DelViewHolder)holder).rbBmList.setChecked(true);
                        ((DelViewHolder)holder).rbBmList.setSelected(true);
                        deleteList.set(position,true);
                    }
                }
            });

        }
    }

    private void setDate(RecyclerView.ViewHolder holder, int position) {
    Log.i(TAG, "??????" + position);


        String scheduledDate = scheduleList.getScheduleArrayList().get(position).getScheduleData().getScheduledDate();
        ScheduleData scheduleData = new ScheduleData();
        scheduleData.getDateDay(scheduledDate);
        String day = scheduleList.getScheduleArrayList().get(position).getScheduleData().getDay();
        String month = scheduledDate.substring(4,6);
        //09 -> 9??? ?????????
        int intMonth = Integer.parseInt(month);
        month = String.valueOf(intMonth);

        String date = scheduledDate.substring(6);

        int intDate = Integer.parseInt(date);
        date = String.valueOf(intDate);

        scheduledDate = month + " / " + date + "(" + scheduleData.getDay() + ")";
        ((ViewHolder) holder).tvDate.setText(scheduledDate);
    }

    private String removeAdminArea(String location) {

        String[] splited = location.split(" ");
        String temp2="";

        if(splited[0].startsWith("??????")){
            //????????????
            if(splited[1].startsWith("??????")) {

                if (splited.length == 2) {
                    temp2 = splited[0] + " " + splited[1];
                } else if (splited.length == 3) {
                    temp2 = splited[0] + " " + splited[1] + " " + splited[2];
                } else if (splited.length == 4) {
                    temp2 = splited[0] + " " + splited[1] + " " + splited[2] + " " + splited[3];
                } else if (splited.length == 5) {
                    temp2 = splited[0] + " " + splited[1] + " " + splited[2] + " " + splited[3] + " " + splited[4];
                } else {
                    temp2 = splited[0] + " " + splited[0] + splited[1];
                }

            }else{

                if (splited.length == 2) {
                    temp2 = splited[1];
                } else if (splited.length == 3) {
                    temp2 = splited[1] + " " + splited[2];
                } else if (splited.length == 4) {
                    temp2 = splited[1] + " " + splited[2] + " " + splited[3];
                } else if (splited.length == 5) {
                    temp2 = splited[1] + " " + splited[2] + " " + splited[3] + " " + splited[4];
                } else {
                    temp2 = splited[0] + splited[1];
                }

            }

        }else {

            if (splited.length == 2) {
                temp2 = splited[1];
            } else if (splited.length == 3) {
                temp2 = splited[1] + " " + splited[2];
            } else if (splited.length == 4) {
                temp2 = splited[1] + " " + splited[2] + " " + splited[3];
            } else if (splited.length == 5) {
                temp2 = splited[1] + " " + splited[2] + " " + splited[3] + " " + splited[4];
            } else {
                temp2 = splited[0] + splited[1];
            }

            splited[0]=setAdminString(splited[0]);

            if(splited[1].endsWith("???")) {

            }else{
                temp2 = splited[0] + " " + temp2;
            }
        }

        return temp2;

    }

    private String setAdminString(String s) {

        if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";

        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("?????????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="??????";
        }else if(s.startsWith("??????")){
            s="?????????";
        }else if(s.startsWith("??????")){
            s="??????";
        }

        return s;

    }

    private void setLocation(RecyclerView.ViewHolder holder, int position) {

        String location = scheduleList.getScheduleArrayList().get(position).getWhere();

        location = removeAdminArea(location);

        ((ViewHolder) holder).tvLoc.setText(location);
    }

    private void setWxImg(RecyclerView.ViewHolder holder, int positionItem) {

        ScheduleData scheduleData = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData();

        boolean checker = checkAMPM();

        int diffDays = (int) howFarFromToday(scheduleData.getScheduledDate());

        if(diffDays>10){
            ((ViewHolder) holder).tvNoWx.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).ivAm.setVisibility(View.GONE);
            ((ViewHolder) holder).ivPm.setVisibility(View.GONE);
        }else{
            findScheduleDateWxData(holder, scheduleData, checker, diffDays);
        }
    }

    private void setDelDate(RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "??????" + position);
        String scheduledDate = scheduleList.getScheduleArrayList().get(position).getScheduleData().getScheduledDate();
        ScheduleData scheduleData = new ScheduleData();
        scheduleData.getDateDay(scheduledDate);
        String day = scheduleList.getScheduleArrayList().get(position).getScheduleData().getDay();
        String month = scheduledDate.substring(4,6);
        //09 -> 9??? ?????????
        int intMonth = Integer.parseInt(month);
        month = String.valueOf(intMonth);

        String date = scheduledDate.substring(6);

        int intDate = Integer.parseInt(date);
        date = String.valueOf(intDate);

        scheduledDate = month + " / " + date + "(" + scheduleData.getDay() + ")";
        ((DelViewHolder) holder).tvDate.setText(scheduledDate);
    }

    private void setDelLocation(RecyclerView.ViewHolder holder, int position) {

        String location = scheduleList.getScheduleArrayList().get(position).getWhere();

        location = removeAdminArea(location);

        ((DelViewHolder) holder).tvLoc.setText(location);
    }

    private void setDelWxImg(RecyclerView.ViewHolder holder, int positionItem) {
        ScheduleData scheduleData = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData();

        boolean checker = checkAMPM();

        int diffDays = (int) howFarFromToday(scheduleData.getScheduledDate());

        if(diffDays>10){
            ((DelViewHolder) holder).tvNoWx.setVisibility(View.VISIBLE);
            ((DelViewHolder) holder).ivAm.setVisibility(View.GONE);
            ((DelViewHolder) holder).ivPm.setVisibility(View.GONE);
        }else{
            findScheduleDateWxDataDel(holder, scheduleData, checker, diffDays);
        }
    }

    private Date getToday(){
        // ????????? ????????? ?????? ?????????.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private long howFarFromToday(String dateCompared) {

        Calendar getToday = Calendar.getInstance();
        getToday.setTime(new Date()); //?????? ??????

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyyMMdd").parse(dateCompared);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i(TAG, "?????? ????????? ?????? ????????????.");
        }
        Calendar cmpDate = Calendar.getInstance();
        cmpDate.setTime(date); //?????? ??????

        long diffSec = (cmpDate.getTimeInMillis() - getToday.getTimeInMillis()) / 1000;
        long diffDays = diffSec / (24 * 60 * 60); //????????? ??????

        return diffDays;

    }

    private boolean checkAMPM() {

        Date dateToday = getToday();

        //???,???,?????? ?????? ??????
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        //???????????? ???????????? ?????? ?????? ?????????
        if(inthour<12||inthour==24){
            //??????
            return false;
        }else {
            //??????
            return true;
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private RecyclerViewAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(RecyclerViewAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        if(scheduleList==null||scheduleList.getScheduleArrayList()==null||scheduleList.getScheduleArrayList().size()==0){
            return 0;
        }
        return scheduleList.getScheduleArrayList().size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvLoc;
        //TextView tvTemp;
        ImageView ivPm;
        ImageView ivAm;
        TextView tvNoWx;

        ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_bm_weather_dates_list);
            tvLoc = view.findViewById(R.id.tv_bm_weather_location_list);
            //tvTemp = view.findViewById(R.id.tv_item_nowtemper);
            ivAm = view.findViewById(R.id.iv_bm_weather_am_list);
            ivPm = view.findViewById(R.id.iv_bm_weather_pm_list);
            tvNoWx = view.findViewById(R.id.tv_bm_no_wx);
            tvNoWx.setVisibility(View.GONE);
        }

    }

    public class DelViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvLoc;
        //TextView tvTemp;
        ImageView ivPm;
        ImageView ivAm;
        RadioButton rbBmList;
        TextView tvNoWx;

        DelViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_bm_weather_dates_list);
            tvLoc = view.findViewById(R.id.tv_bm_weather_location_list);
            //tvTemp = view.findViewById(R.id.tv_item_nowtemper);
            ivAm = view.findViewById(R.id.iv_bm_weather_am_list);
            ivPm = view.findViewById(R.id.iv_bm_weather_pm_list);
            tvNoWx = view.findViewById(R.id.tv_bm_no_wx);
            tvNoWx.setVisibility(View.GONE);
            rbBmList = view.findViewById(R.id.rb_item_list);
            rbBmList.setVisibility(View.VISIBLE);
        }

    }

    private int getHourHH() {

        Date dateToday = getToday();

        //???,???,?????? ?????? ??????
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        return inthour;

    }

    private String makingStrHour() {

        Date dateToday = getToday();

        //???,???,?????? ?????? ??????
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
        String currentHh = curHour.format(dateToday);

        int currHh = Integer.parseInt(currentHh);

        if(currHh<10){
            currentHh = "0" + String.valueOf(currHh);
        }

        currentHh = currentHh + "00";

        return currentHh;

    }

    private void findScheduleDateWxData(RecyclerView.ViewHolder holder,ScheduleData scheduleData, boolean checker, int diffDays) {

        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_gray);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_rain_snow);

        String strCurHH = makingStrHour();

        //????????????????????? ???????????? ?????? ????????? ????????????.
        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
        Log.i(TAG, "????????? ?????? ????????????? : " + diffDays);
        if (diffDays == 0) {
            //???????????? ??????????????? ?????? ?????? ????????? ??????????????? ?????????

            setNowWxCond(holder, scheduleData, strCurHH);

        } else if (diffDays == 1) {

            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am();
                if (wx.equals("??????")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(sun);
                } else if (wx.equals("????????????")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
                } else if (wx.equals("??????")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 1) {
                //???
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 2) {
                //??????
                ((ViewHolder) holder).ivAm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 3) {
                //???
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 4) {
                //?????????
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }


            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm();
                if (wx.equals("??????")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(sun);
                } else if (wx.equals("????????????")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
                } else if (wx.equals("??????")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 1) {
                //???
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 2) {
                //??????
                ((ViewHolder) holder).ivPm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 3) {
                //???
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 4) {
                //?????????
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }


        } else if (diffDays == 2) {

            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Am();
                if (wx.equals("??????")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(sun);
                } else if (wx.equals("????????????")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
                } else if (wx.equals("??????")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 1) {
                //???
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 2) {
                //??????
                ((ViewHolder) holder).ivAm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 3) {
                //???
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 4) {
                //?????????
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }


            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm();
                if (wx.equals("??????")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(sun);
                } else if (wx.equals("????????????")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
                } else if (wx.equals("??????")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 1) {
                //???
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 2) {
                //??????
                ((ViewHolder) holder).ivPm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 3) {
                //???
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 4) {
                //?????????
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }

        } else if (diffDays == 3) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Am();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Pm();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }

        } else if (diffDays == 4) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Am();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Pm();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 5) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Am();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Pm();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 6) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Am();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Pm();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 7) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Am();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Pm();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 8) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf8();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            }

        } else if (diffDays == 9) {
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf9();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            }
        } else if (diffDays == 10) {
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf10();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("?????????")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            }

        }
    }

    private void setNowWxCond(RecyclerView.ViewHolder viewHolder, ScheduleData scheduleData, String strCurHH) {

        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_gray);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_rain_snow);

        //????????????????????? ???????????? ?????? ????????? ????????????.
        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
        //???????????????, ????????? ????????? ??????
        for (int i = 0; i < scheduleData.getFcst().getWxToday().size(); i++) {

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)) {

                if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                    Log.i(TAG, "?????? ??????");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //???
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //??????
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //???
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //?????????
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(shower);
                    }
                }
            }


        }

        String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm();
        Log.i(TAG, "?????? ?????? : " + wx);
        //????????????
        if (wx.equals("??????")) {

            ((ViewHolder)viewHolder).ivPm.setImageDrawable(sun);
        } else if (wx.equals("????????????")) {
            ((ViewHolder)viewHolder).ivPm.setImageDrawable(cloudy);
        } else if (wx.equals("??????")) {
            ((ViewHolder)viewHolder).ivPm.setImageDrawable(wind);
        } else if (wx.contains("???")) {
            ((ViewHolder)viewHolder).ivPm.setImageDrawable(rain);
        } else if (wx.contains("???")) {
            ((ViewHolder)viewHolder).ivPm.setImageDrawable(snow);
        } else if (wx.contains("?????????")) {
            ((ViewHolder)viewHolder).ivPm.setImageDrawable(shower);
        }

    }

    private void setDelNowWxCond(RecyclerView.ViewHolder viewHolder, ScheduleData scheduleData, String strCurHH) {

        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_gray);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_rain_snow);

        //????????????????????? ???????????? ?????? ????????? ????????????.
        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
        //???????????????, ????????? ????????? ??????
        for (int i = 0; i < scheduleData.getFcst().getWxToday().size(); i++) {

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)) {

                if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                    Log.i(TAG, "?????? ??????");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //???
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //??????
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //???
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //?????????
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(shower);
                    }
                }
            }


        }

        String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm();
        Log.i(TAG, "?????? ?????? : " + wx);
        //????????????
        if (wx.equals("??????")) {

            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(sun);
        } else if (wx.equals("????????????")) {
            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(cloudy);
        } else if (wx.equals("??????")) {
            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(wind);
        } else if (wx.contains("???")) {
            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(rain);
        } else if (wx.contains("???")) {
            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(snow);
        } else if (wx.contains("?????????")) {
            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(shower);
        }

    }


    private void findScheduleDateWxDataDel(RecyclerView.ViewHolder holder,ScheduleData scheduleData, boolean checker, int diffDays) {

        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_gray);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_rain_snow);

        String strCurHH = makingStrHour();

        //????????????????????? ???????????? ?????? ????????? ????????????.
        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
        Log.i(TAG, "????????? ?????? ????????????? : " + diffDays);
        if (diffDays == 0) {
            //???????????? ??????????????? ?????? ?????? ????????? ??????????????? ?????????

            setDelNowWxCond(holder, scheduleData, strCurHH);

        } else if (diffDays == 1) {

            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am();
                if (wx.equals("??????")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
                } else if (wx.equals("????????????")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
                } else if (wx.equals("??????")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 1) {
                //???
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 2) {
                //??????
                ((DelViewHolder) holder).ivAm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 3) {
                //???
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 4) {
                //?????????
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }


            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm();
                if (wx.equals("??????")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
                } else if (wx.equals("????????????")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
                } else if (wx.equals("??????")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 1) {
                //???
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 2) {
                //??????
                ((DelViewHolder) holder).ivPm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 3) {
                //???
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 4) {
                //?????????
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }


        } else if (diffDays == 2) {

            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Am();
                if (wx.equals("??????")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
                } else if (wx.equals("????????????")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
                } else if (wx.equals("??????")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 1) {
                //???
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 2) {
                //??????
                ((DelViewHolder) holder).ivAm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 3) {
                //???
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 4) {
                //?????????
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }


            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm();
                if (wx.equals("??????")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
                } else if (wx.equals("????????????")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
                } else if (wx.equals("??????")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 1) {
                //???
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 2) {
                //??????
                ((DelViewHolder) holder).ivPm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 3) {
                //???
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 4) {
                //?????????
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }

        } else if (diffDays == 3) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Am();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Pm();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }

        } else if (diffDays == 4) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Am();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Pm();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 5) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Am();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Pm();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 6) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Am();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Pm();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 7) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Am();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Pm();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 8) {
            //?????? ?????? ?????????, ???, ?????????,???, ???/???
            //?????? ?????????,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf8();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            }

        } else if (diffDays == 9) {
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf9();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            }
        } else if (diffDays == 10) {
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf10();
            Log.i(TAG, "?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("?????????")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {

        if(((WxListActivity)mContext).ismTrashChecker()){
            return TRASH;
        }else {
            return NORMAL;
        }

    }

}
