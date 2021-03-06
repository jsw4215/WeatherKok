package com.devpilot.weatherkok.weather.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.datalist.data.ScheduleData;
import com.devpilot.weatherkok.when.models.ScheduleList;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeatherActivityAdapter extends RecyclerView.Adapter<WeatherActivityAdapter.ViewHolder> {
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    String TAG = "BookMarkAdapter";
    String mYearMonth;
    Context mContext;
    ScheduleList scheduleList;

    public WeatherActivityAdapter(Context context, ScheduleList List) {
        Log.i(TAG, "Constructor");
        this.mContext = context;
        this.scheduleList = List;

    }

    @Override
    public WeatherActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Log.i(TAG, "onCreateViewHolder with schedules");
            View view = inflater.inflate(R.layout.item_weather_list, viewGroup, false);
            return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

            setDate(holder, position);

            String scheduledDate = scheduleList.getScheduleArrayList().get(position).getScheduleData().getScheduledDate();

            setLocation(holder, position);

            setWxImg(holder, position, scheduledDate);

    }

    private void setLocation(ViewHolder holder, int position) {

        String location = scheduleList.getScheduleArrayList().get(position).getWhere();

        location = removeAdminArea(location);

        holder.tvLoc.setText(location);
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

    private void setDate(ViewHolder holder, int position) {

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
        holder.tvDate.setText(scheduledDate);

    }


    private void setWxImg(ViewHolder viewHolder, int positionItem, String scheduledDate) {
        //?????? ????????? ????????? ?????? ??????????????? ?????? ????????? ???????????? ???????????? ??????. ????????? ?????????
        boolean amPm = checkAMPM();

        int gap = (int) howFarFromToday(scheduledDate);

        int hour = getHourHH();



        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
        //??????

        ScheduleData scheduleData = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData();
        //Drawable xMark = mContext.getResources().getDrawable(R.drawable.close_mark);
        if(gap>10) {
            viewHolder.ivAm.setVisibility(View.GONE);
            viewHolder.ivPm.setVisibility(View.GONE);
            viewHolder.tvNoWx.setVisibility(View.VISIBLE);
        }else {
            findScheduleDateWxData(viewHolder, scheduleData, amPm, gap, hour);
        }


//            String wx =scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getWf0Pm();
//            //????????????
//            if(wx.equals("??????")){
//                viewHolder.ivPm.setImageDrawable(sun);
//            }else if(wx.equals("????????????")){
//                viewHolder.ivPm.setImageDrawable(cloudy);
//            }else if(wx.equals("??????")){
//                viewHolder.ivPm.setImageDrawable(wind);
//            }
//
//            //???????????????, ??????????????? ????????? ?????? ????????????
//            int raining = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getRnSt0PmType();
//
//            if(raining==1){
//                viewHolder.ivPm.setImageDrawable(rain);
//            }else if(raining==2){
//                viewHolder.ivPm.setImageDrawable(snowRain);
//            }else if(raining==3){
//                viewHolder.ivPm.setImageDrawable(snow);
//            }else if(raining==4){
//                viewHolder.ivPm.setImageDrawable(shower);
//            }
//
//        //??????
//            String wxAm = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getWf0Am();
//
//            //????????????
//            if(wxAm.equals("??????")){
//                viewHolder.ivAm.setImageDrawable(sun);
//            }else if(wxAm.equals("????????????")){
//                viewHolder.ivAm.setImageDrawable(cloudy);
//            }else if(wxAm.equals("??????")){
//                viewHolder.ivAm.setImageDrawable(wind);
//            }
//
//            //???????????????, ??????????????? ????????? ?????? ????????????
//            int rainingAm = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getRnSt0AmType();
//
//            if(rainingAm==1){
//                viewHolder.ivAm.setImageDrawable(rain);
//            }else if(rainingAm==2){
//                viewHolder.ivAm.setImageDrawable(snowRain);
//            }else if(rainingAm==3){
//                viewHolder.ivAm.setImageDrawable(snow);
//            }else if(rainingAm==4){
//                viewHolder.ivAm.setImageDrawable(shower);
//            }


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

    private Date getToday(){
        // ????????? ????????? ?????? ?????????.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
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

    @Override
    public int getItemCount() {
        return scheduleList.getScheduleArrayList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvLoc;
        ImageView ivAm;
        ImageView ivPm;
        TextView tvNoWx;

        ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_bm_weather_dates_list);
            tvLoc = view.findViewById(R.id.tv_bm_weather_location_list);
            ivAm = view.findViewById(R.id.iv_bm_weather_am_list);
            ivPm = view.findViewById(R.id.iv_bm_weather_pm_list);
            tvNoWx = view.findViewById(R.id.tv_bm_no_wx);
            tvNoWx.setVisibility(View.GONE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //????????? ?????? ????????? center??? ??????

                }
            });
        }

    }

    //???????????? ?????? ???????????? ????????????, ???????????? ??????????????? ?????????.
    private void setNowWxCond(ViewHolder viewHolder, ScheduleData scheduleData,String strCurHH){

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
            for(int i=0;i<scheduleData.getFcst().getWxToday().size();i++){

                if(scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)){

                    if(scheduleData.getFcst().getWxToday().get(i).getRainType()==0) {
                        Log.i(TAG, "?????? ??????");
                        if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                            viewHolder.ivAm.setImageDrawable(sun);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                            viewHolder.ivAm.setImageDrawable(cloudy);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                            viewHolder.ivAm.setImageDrawable(wind);
                        }

                    }else {

                        if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                            //???
                            viewHolder.ivAm.setImageDrawable(rain);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                            //??????
                            viewHolder.ivAm.setImageDrawable(snowRain);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                            //???
                            viewHolder.ivAm.setImageDrawable(snow);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                            //?????????
                            viewHolder.ivAm.setImageDrawable(shower);
                        }
                    }
                }


            }

        String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm();
        Log.i(TAG, "?????? ?????? : " + wx);
        //????????????
        if (wx.equals("??????")) {

            viewHolder.ivPm.setImageDrawable(sun);
        } else if (wx.equals("????????????")) {
            viewHolder.ivPm.setImageDrawable(cloudy);
        } else if (wx.equals("??????")) {
            viewHolder.ivPm.setImageDrawable(wind);
        } else if (wx.contains("???")) {
            viewHolder.ivPm.setImageDrawable(rain);
        } else if (wx.contains("???")) {
            viewHolder.ivPm.setImageDrawable(snow);
        } else if (wx.contains("?????????")) {
            viewHolder.ivPm.setImageDrawable(shower);
        }

        }


    private void findScheduleDateWxData(ViewHolder viewHolder, ScheduleData scheduleData, boolean checker, int diffDays, int currHh) {

        String strCurHH = "";

        if(currHh<10){
            strCurHH = "0" + String.valueOf(currHh);
        }else{
            strCurHH = String.valueOf(currHh);
        }

        strCurHH = strCurHH + "00";



        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_gray);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_rain_snow);

        if(diffDays==0){
            //???????????? ??????????????? ?????? ?????? ????????? ??????????????? ?????????

            setNowWxCond(viewHolder, scheduleData, strCurHH);

        }else if(diffDays==1){
                //????????????????????? ???????????? ?????? ????????? ????????????.
                //POP ????????????
                //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
                //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
                //??????
                //?????? ?????? ?????????, ???, ?????????,???, ???/???
                //?????? ?????????,

                    String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am();
            Log.i(TAG, "1?????? ?????? : " + wx);
                    //????????????
                    if (wx.equals("??????")) {
                        viewHolder.ivAm.setImageDrawable(sun);
                    } else if (wx.equals("????????????")) {
                        viewHolder.ivAm.setImageDrawable(cloudy);
                    } else if (wx.equals("??????")) {
                        viewHolder.ivAm.setImageDrawable(wind);
                    } else if (wx.contains("???")) {
                        viewHolder.ivAm.setImageDrawable(rain);
                    } else if (wx.contains("???")) {
                        viewHolder.ivAm.setImageDrawable(snow);
                    } else if (wx.contains("?????????")) {
                        viewHolder.ivAm.setImageDrawable(shower);
                    }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm();
            Log.i(TAG, "1?????? ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }


        }else if(diffDays==2){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Am();
            Log.i(TAG, "2 ?????? : " + wx);
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm();
            Log.i(TAG, "2 ?????? : " + wx);

            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }

        }
        else if(diffDays==3){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Am();
            Log.i(TAG, "3 ?????? : " + wx);

            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Pm();
            Log.i(TAG, "3 ?????? : " + wx);

            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }
        else if(diffDays==4){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Am();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Pm();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }
        else if(diffDays==5){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Am();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Pm();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }
        else if(diffDays==6){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Am();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Pm();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }
        else if(diffDays==7){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Am();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Pm();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }
        else if(diffDays==8){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf8();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }
        }else if(diffDays==9){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf9();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }
        }
        else if(diffDays==10){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf10();
            //????????????
            if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("????????????")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("??????")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("???")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("?????????")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }
        }

    }


}
