package com.devpilot.weatherkok.weather.utils;

import android.content.Context;
import android.content.Intent;
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
import com.devpilot.weatherkok.weather.SingleWxActivity;
import com.devpilot.weatherkok.when.models.Schedule;
import com.devpilot.weatherkok.when.models.ScheduleList;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NowWxActivityAdapter extends RecyclerView.Adapter<NowWxActivityAdapter.ViewHolder> {
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    String TAG = "BookMarkAdapter";
    String mYearMonth;
    Context mContext;
    ArrayList<Schedule> temp = new ArrayList<>();
    ScheduleList scheduleList;

    public NowWxActivityAdapter(Context context, ScheduleList List) {
        Log.i(TAG, "Constructor");
        this.mContext = context;
        this.scheduleList = List;
    }

    @Override
    public NowWxActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Log.i(TAG, "onCreateViewHolder with schedules");
        View view = inflater.inflate(R.layout.item_bookmark_list, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        setDate(holder, position);

        setLocation(holder, position);

        setWxImg(holder, position);

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

        String today = getFutureDay("yyyyMMdd", 0);

        //holder.tvDate.setText(today);
    }

    public static String getFutureDay(String pattern, int gap) {
        DateFormat dtf = new SimpleDateFormat(pattern);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, gap);
        return dtf.format(cal.getTime());
    }

    private void setWxImg(ViewHolder viewHolder, int positionItem) {

        ScheduleData scheduleData = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData();
        String strHour = makingStrHour();
        findScheduleDateWxData(viewHolder, scheduleData, true, 0, strHour);
        //POP ????????????
        //- ????????????(SKY) ?????? : ??????(1), ????????????(3), ??????(4)
        //- ????????????(PTY) ?????? : (??????) ??????(0), ???(1), ???/???(2), ???(3), ?????????(4)
        //??????


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

    private void findScheduleDateWxData(ViewHolder viewHolder, ScheduleData scheduleData, boolean checker, int diffDays, String strCurHH) {


            //???????????? ??????????????? ?????? ?????? ????????? ??????????????? ?????????

            setNowWxCond(viewHolder, scheduleData, strCurHH);


    }

    private void setNowWxCond(ViewHolder viewHolder, ScheduleData scheduleData, String strCurHH) {

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
                        viewHolder.ivAm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        viewHolder.ivAm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        viewHolder.ivAm.setImageDrawable(wind);
                    }

                } else {

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

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals("1800")) {

                if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                    Log.i(TAG, "?????? ??????");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        viewHolder.ivPm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        viewHolder.ivPm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        viewHolder.ivPm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //???
                        viewHolder.ivPm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //??????
                        viewHolder.ivPm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //???
                        viewHolder.ivPm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //?????????
                        viewHolder.ivPm.setImageDrawable(shower);
                    }
                }
            }

        }

    }


    private Date getToday() {
        // ????????? ????????? ?????? ?????????.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private boolean checkAMPM() {

        Date dateToday = getToday();

        //???,???,?????? ?????? ??????
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        //???????????? ???????????? ?????? ?????? ?????????
        if (inthour < 12 || inthour == 24) {
            //??????
            return false;
        } else {
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
                    int pos = getAdapterPosition();

                    //?????????
                    Intent intent = new Intent(mContext, SingleWxActivity.class);
                    intent.putExtra("position",pos);
                    mContext.startActivity(intent);
                }
            });
        }

    }


}
