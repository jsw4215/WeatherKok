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
import com.example.weatherkok.when.models.ScheduleList;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookMarkListAdapter extends RecyclerView.Adapter<BookMarkListAdapter.ViewHolder> {
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    String TAG = "BookMarkAdapter";
    String mYearMonth;
    Context mContext;
    ScheduleList scheduleList;

    public BookMarkListAdapter(Context context, ScheduleList List) {
        Log.i(TAG, "Constructor");
        this.mContext = context;
        this.scheduleList = List;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        Log.i(TAG, "onCreateViewHolder");
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_schedule, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        setWxImg(holder, position);

    }


    private void setWxImg(ViewHolder viewHolder, int positionItem) {

        boolean amPm = checkAMPM();

        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_wind);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_snowing);

        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)

        if(amPm) {
            //오후

            String wx =scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getWf0Pm();
            //기본날씨
            if(wx.equals("맑음")){
                viewHolder.ivWx.setImageDrawable(sun);
            }else if(wx.equals("구름많음")){
                viewHolder.ivWx.setImageDrawable(cloudy);
            }else if(wx.equals("흐림")){
                viewHolder.ivWx.setImageDrawable(wind);
            }

            //비가온다면, 비가온다는 표시를 먼저 해야겠지
            int raining = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getRnSt0PmType();

            if(raining==1){
                viewHolder.ivWx.setImageDrawable(rain);
            }else if(raining==2){
                viewHolder.ivWx.setImageDrawable(snowRain);
            }else if(raining==3){
                viewHolder.ivWx.setImageDrawable(snow);
            }else if(raining==4){
                viewHolder.ivWx.setImageDrawable(shower);
            }
        }else {
            //오전
            String wx = scheduleList.getScheduleArrayList().get(0).getScheduleData().getFcst().getWxList().getItem().get(positionItem).getWf0Am();

            //기본날씨
            if(wx.equals("맑음")){
                viewHolder.ivWx.setImageDrawable(sun);
            }else if(wx.equals("구름많음")){
                viewHolder.ivWx.setImageDrawable(cloudy);
            }else if(wx.equals("흐림")){
                viewHolder.ivWx.setImageDrawable(wind);
            }

            //비가온다면, 비가온다는 표시를 먼저 해야겠지
            int raining = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getRnSt0AmType();

            if(raining==1){
                viewHolder.ivWx.setImageDrawable(rain);
            }else if(raining==2){
                viewHolder.ivWx.setImageDrawable(snowRain);
            }else if(raining==3){
                viewHolder.ivWx.setImageDrawable(snow);
            }else if(raining==4){
                viewHolder.ivWx.setImageDrawable(shower);
            }
        }

    }

    private Date getToday(){
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
        if(inthour<12||inthour==24){
            //오전
            return false;
        }else {
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
        TextView tvTemp;
        TextView tvMaxMin;
        ImageView ivWx;

        ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_item_date);
            tvLoc = view.findViewById(R.id.tv_item_address);
            tvTemp = view.findViewById(R.id.tv_item_nowtemper);
            ivWx = view.findViewById(R.id.iv_item_weather);
            tvMaxMin = view.findViewById(R.id.tv_item_temper);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //클릭시

                }
            });
        }

    }


}
