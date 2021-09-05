package com.example.weatherkok.weather.utils;

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

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;

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
        String temp2 = "";
        if(splited.length==2){
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

        return temp2;

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
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        //오후


    }

    private String makingStrHour() {

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentHh = curHour.format(dateToday);

        int currHh = Integer.parseInt(currentHh);

        if(currHh<10){
            currentHh = "0" + String.valueOf(currHh);
        }

        currentHh = currentHh + "00";

        return currentHh;

    }

    private void findScheduleDateWxData(ViewHolder viewHolder, ScheduleData scheduleData, boolean checker, int diffDays, String strCurHH) {


            //오늘이면 시간비교를 해서 해당 시간의 날씨예보를 담을것

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

        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        //비가온다면, 비모양 안오면 날씨
        for (int i = 0; i < scheduleData.getFcst().getWxToday().size(); i++) {

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)) {

                if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                    Log.i(TAG, "오늘 현재");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        viewHolder.ivAm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        viewHolder.ivAm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        viewHolder.ivAm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        viewHolder.ivAm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        viewHolder.ivAm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        viewHolder.ivAm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        viewHolder.ivAm.setImageDrawable(shower);
                    }
                }
            }

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals("1800")) {

                if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                    Log.i(TAG, "오늘 현재");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        viewHolder.ivPm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        viewHolder.ivPm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        viewHolder.ivPm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        viewHolder.ivPm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        viewHolder.ivPm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        viewHolder.ivPm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        viewHolder.ivPm.setImageDrawable(shower);
                    }
                }
            }

        }

    }


    private Date getToday() {
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private boolean checkAMPM() {

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        //현재시간 출력하여 오전 오후 나누기
        if (inthour < 12 || inthour == 24) {
            //오전
            return false;
        } else {
            //오후
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

                    //클릭시 해당 지역이 center로 이동

                }
            });
        }

    }


}
