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
import com.devpilot.weatherkok.when.models.Schedule;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SingleWxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final String TAG = SingleWxAdapter.class.getSimpleName();

    Schedule schedule;
    Context mContext;
    int CENTER = 0;
    int BOTTOM = 1;

    public SingleWxAdapter(Schedule schedule, Context mContext) {
        this.schedule = schedule;
        this.mContext = mContext;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        if(viewType==CENTER){
//            View view = inflater.inflate(R.layout.item_center, parent, false);
//            return new CenterViewHolder(view);
//        }else{
            Log.i(TAG, "onCreateViewHolder with schedules");
            View view = inflater.inflate(R.layout.item_singlewx_list, parent, false);
            return new ViewHolder(view);
//        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

//        if(holder instanceof CenterViewHolder) {
//
//            ((NowWxActivity)mContext).decorCenter();
//
//        }else {

            setWxImg(holder, position);

//        }


    }

    @Override
    public int getItemCount() {
        return 11;
    }

    private Date getToday(){
        // ????????? ????????? ?????? ?????????.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    public static String getFutureDay(String pattern, int gap) {
        DateFormat dtf = new SimpleDateFormat(pattern);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, gap);
        return dtf.format(cal.getTime());
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


    private void setWxImg(RecyclerView.ViewHolder holder, int positionItem) {
        //????????? true
        boolean checker = checkAMPM();

            findScheduleDateWxData(holder, positionItem);

    }

    private void findScheduleDateWxData(RecyclerView.ViewHolder holder, int position) {

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
        String wxAm ="";
        String wxPm ="??????";
        int tempMax = 0;
        int tempMin = 0;
        String yyyyMMdd = "";
        if (position==0&&schedule.getScheduleData().getFcst().getWxList().getItem().size()!=0) {
            //????????? ??????/?????? ????????? ????????? ?????????.
                wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf0Am();
                wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf0Pm();
            if(wxAm.equals("")){
                wxAm = wxPm;
            }
                tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax0();
                tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin0();
                yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }else if (position==1) {
            //????????? ??????/?????? ????????? ????????? ?????????.
            wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf1Am();
            wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf1Pm();
            tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax1();
            tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin1();
            yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }else if (position==2) {
            //????????? ??????/?????? ????????? ????????? ?????????.
            wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf2Am();
            wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf2Pm();
            tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax2();
            tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin2();
            yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }else if (position==3) {
            //????????? ??????/?????? ????????? ????????? ?????????.
            wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf3Am();
            wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf3Pm();
            tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax3();
            tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin3();
            yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }else if (position==4) {
            //????????? ??????/?????? ????????? ????????? ?????????.
            wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf4Am();
            wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf4Pm();
            tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax4();
            tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin4();
            yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }else if (position==5) {
            //????????? ??????/?????? ????????? ????????? ?????????.
            wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf5Am();
            wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf5Pm();
            tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax5();
            tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin5();
            yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }else if (position==6) {
            //????????? ??????/?????? ????????? ????????? ?????????.
            wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf6Am();
            wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf6Pm();
            tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax6();
            tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin6();
            yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }else if (position==7) {
            //????????? ??????/?????? ????????? ????????? ?????????.
            wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf7Am();
            wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf7Pm();
            tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax7();
            tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin7();
            yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }else if (position==8) {
            //????????? ??????/?????? ????????? ????????? ?????????.
            wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf8();
            wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf8();
            tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax8();
            tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin8();
            yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }else if (position==9) {
            //????????? ??????/?????? ????????? ????????? ?????????.
            wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf9();
            wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf9();
            tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax9();
            tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin9();
            yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }else if (position==10) {
            //????????? ??????/?????? ????????? ????????? ?????????.
            wxAm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf10();
            wxPm = schedule.getScheduleData().getFcst().getWxList().getItem().get(0).getWf10();
            tempMax = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMax10();
            tempMin = schedule.getScheduleData().getFcst().getTempList().getItem().get(0).getTaMin10();
            yyyyMMdd = getFutureDay("MM / dd (E)",position);

            ((ViewHolder) holder).tvDate.setText(yyyyMMdd);
            ((ViewHolder) holder).ivAm.setImageDrawable(setWxImage(wxAm));
            ((ViewHolder) holder).ivPm.setImageDrawable(setWxImage(wxPm));
            ((ViewHolder) holder).tvDegrees.setText(
                    tempMax + "\u00B0" + " / "
                            + tempMin + "\u00B0"
            );

        }
    }

    public class CenterViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        ImageView ivAm;
        ImageView ivPm;
        TextView tvDegrees;

        CenterViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_now_weather_dates_list);
            ivAm = view.findViewById(R.id.iv_now_weather_am_list);
            ivPm = view.findViewById(R.id.iv_now_weather_pm_list);
            tvDegrees = view.findViewById(R.id.tv_now_weather_degrees);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //????????? ?????? ????????? center??? ??????

                }
            });
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        ImageView ivAm;
        ImageView ivPm;
        TextView tvDegrees;

        ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_now_weather_dates_list);
            ivAm = view.findViewById(R.id.iv_now_weather_am_list);
            ivPm = view.findViewById(R.id.iv_now_weather_pm_list);
            tvDegrees = view.findViewById(R.id.tv_now_weather_degrees);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //????????? ?????? ????????? center??? ??????

                }
            });
        }

    }

    private Drawable setWxImage(String wx) {

        Drawable sunny = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable gray = mContext.getResources().getDrawable(R.drawable.ic_gray);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_rain_snow);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);

        if(wx.contains("??????")){
            return sunny;
        }else if(wx.contains("??????")){
            return cloudy;
        }else if(wx.contains("??????")){
            return gray;
        }else if(wx.equals("???/???")){
            return snowRain;
        }else if(wx.contains("???")){
            return rain;
        }else if(wx.contains("???")){
            return snow;
        }else if(wx.contains("?????????")){
            return shower;
        }

        return null;

    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return CENTER;
        }else {
            return BOTTOM;
        }
    }
}
