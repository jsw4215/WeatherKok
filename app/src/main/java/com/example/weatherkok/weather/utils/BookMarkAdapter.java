package com.example.weatherkok.weather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.weatherkok.R;
import com.example.weatherkok.when.YearActivity;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.when.utils.YearRecyclerViewAdapter;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.ViewHolder> {
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    String TAG = "BookMarkAdapter";
    String mYearMonth;
    Context mContext;
    ScheduleList scheduleList;

    public BookMarkAdapter(Context context, ScheduleList List) {
        Log.i(TAG, "Constructor");
        this.mContext = context;
        this.scheduleList = List;

    }

    @Override
    public BookMarkAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        Log.i(TAG, "onCreateViewHolder");
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_bookmark_list, viewGroup, false);
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
                viewHolder.ivPm.setImageDrawable(sun);
            }else if(wx.equals("구름많음")){
                viewHolder.ivPm.setImageDrawable(cloudy);
            }else if(wx.equals("흐림")){
                viewHolder.ivPm.setImageDrawable(wind);
            }

            //비가온다면, 비가온다는 표시를 먼저 해야겠지
            int raining = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getRnSt0PmType();

            if(raining==1){
                viewHolder.ivPm.setImageDrawable(rain);
            }else if(raining==2){
                viewHolder.ivPm.setImageDrawable(snowRain);
            }else if(raining==3){
                viewHolder.ivPm.setImageDrawable(snow);
            }else if(raining==4){
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }else {
        //오전
            String wx = scheduleList.getScheduleArrayList().get(0).getScheduleData().getFcst().getWxList().getItem().get(positionItem).getWf0Am();

            //기본날씨
            if(wx.equals("맑음")){
                viewHolder.ivAm.setImageDrawable(sun);
            }else if(wx.equals("구름많음")){
                viewHolder.ivAm.setImageDrawable(cloudy);
            }else if(wx.equals("흐림")){
                viewHolder.ivAm.setImageDrawable(wind);
            }

            //비가온다면, 비가온다는 표시를 먼저 해야겠지
            int raining = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getRnSt0AmType();

            if(raining==1){
                viewHolder.ivAm.setImageDrawable(rain);
            }else if(raining==2){
                viewHolder.ivAm.setImageDrawable(snowRain);
            }else if(raining==3){
                viewHolder.ivAm.setImageDrawable(snow);
            }else if(raining==4){
                viewHolder.ivAm.setImageDrawable(shower);
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
        ImageView ivAm;
        ImageView ivPm;

        ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_bm_weather_dates_list);
            tvLoc = view.findViewById(R.id.tv_bm_weather_location_list);
            ivAm = view.findViewById(R.id.iv_bm_weather_am_list);
            ivPm = view.findViewById(R.id.iv_bm_weather_pm_list);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //클릭시 해당 지역이 center로 이동

                }
            });
        }

    }


}
