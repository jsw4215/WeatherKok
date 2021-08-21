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
import com.example.weatherkok.when.models.ScheduleList;

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
            View view = inflater.inflate(R.layout.item_bookmark_list, viewGroup, false);
            return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

            String scheduledDate = setDate(holder, position);

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
        if(splited.length==3) {
            temp2 = splited[1] + " " + splited[2];
        } else if(splited.length==4) {
            temp2 = splited[1] + " " + splited[2] + " " + splited[3];
        } else if(splited.length==5){
            temp2 = splited[1] + " " + splited[2] + " " + splited[3] + " " + splited[4];
        } else {
            temp2 = splited[0] + splited[1];
        }

        return temp2;

    }

    private String setDate(ViewHolder holder, int position) {

        String scheduledDate = scheduleList.getScheduleArrayList().get(position).getScheduleData().getScheduledDate();

        holder.tvDate.setText(scheduledDate);

        return scheduledDate;
    }


    private void setWxImg(ViewHolder viewHolder, int positionItem, String scheduledDate) {
        //해당 스케쥴 날짜에 맞춘 날씨정보와 기온 정보를 리스트에 띄우도록 한다. 오늘이 아니라
        boolean amPm = checkAMPM();

        int gap = (int) howFarFromToday(scheduledDate);

        int hour = getHourHH();



        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        //오후

        ScheduleData scheduleData = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData();
        Drawable xMark = mContext.getResources().getDrawable(R.drawable.close_mark);
        if(gap>10) {
            viewHolder.ivAm.setImageDrawable(xMark);
            viewHolder.ivPm.setImageDrawable(xMark);
        }else {
            findScheduleDateWxData(viewHolder, scheduleData, amPm, gap, hour);
        }


//            String wx =scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getWf0Pm();
//            //기본날씨
//            if(wx.equals("맑음")){
//                viewHolder.ivPm.setImageDrawable(sun);
//            }else if(wx.equals("구름많음")){
//                viewHolder.ivPm.setImageDrawable(cloudy);
//            }else if(wx.equals("흐림")){
//                viewHolder.ivPm.setImageDrawable(wind);
//            }
//
//            //비가온다면, 비가온다는 표시를 먼저 해야겠지
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
//        //오전
//            String wxAm = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData().getFcst().getWxList().getItem().get(0).getWf0Am();
//
//            //기본날씨
//            if(wxAm.equals("맑음")){
//                viewHolder.ivAm.setImageDrawable(sun);
//            }else if(wxAm.equals("구름많음")){
//                viewHolder.ivAm.setImageDrawable(cloudy);
//            }else if(wxAm.equals("흐림")){
//                viewHolder.ivAm.setImageDrawable(wind);
//            }
//
//            //비가온다면, 비가온다는 표시를 먼저 해야겠지
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
        getToday.setTime(new Date()); //금일 날짜

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyyMMdd").parse(dateCompared);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i(TAG, "비교 날짜가 옳지 않습니다.");
        }
        Calendar cmpDate = Calendar.getInstance();
        cmpDate.setTime(date); //특정 일자

        long diffSec = (cmpDate.getTimeInMillis() - getToday.getTimeInMillis()) / 1000;
        long diffDays = diffSec / (24 * 60 * 60); //일자수 차이

        return diffDays;

    }

    private Date getToday(){
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    private int getHourHH() {

        Date dateToday = getToday();

        //연,월,일을 따로 저장
        final SimpleDateFormat curHour = new SimpleDateFormat("kk", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentHour = curHour.format(dateToday);
        int inthour = Integer.parseInt(currentHour);

        return inthour;

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

    //리스트에 오전 이미지는 현재날씨, 오후에는 오후날씨를 띄운다.
    private void setNowWxCond(ViewHolder viewHolder, ScheduleData scheduleData,String strCurHH){

        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_wind);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_snowing);

            //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
            //POP 강수확률
            //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
            //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
            //비가온다면, 비모양 안오면 날씨
            for(int i=0;i<scheduleData.getFcst().getWxToday().size();i++){

                if(scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)){

                    if(scheduleData.getFcst().getWxToday().get(i).getRainType()==0) {
                        Log.i(TAG, "오늘 현재");
                        if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                            viewHolder.ivAm.setImageDrawable(sun);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                            viewHolder.ivAm.setImageDrawable(cloudy);
                        } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                            viewHolder.ivAm.setImageDrawable(wind);
                        }

                    }else {

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


            }

        String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm();
        Log.i(TAG, "오늘 오후 : " + wx);
        //기본날씨
        if (wx.equals("맑음")) {

            viewHolder.ivPm.setImageDrawable(sun);
        } else if (wx.equals("구름많음")) {
            viewHolder.ivPm.setImageDrawable(cloudy);
        } else if (wx.equals("흐림")) {
            viewHolder.ivPm.setImageDrawable(wind);
        } else if (wx.contains("비")) {
            viewHolder.ivPm.setImageDrawable(rain);
        } else if (wx.contains("눈")) {
            viewHolder.ivPm.setImageDrawable(snow);
        } else if (wx.contains("소나기")) {
            viewHolder.ivPm.setImageDrawable(shower);
        }

        }


    private void findScheduleDateWxData(ViewHolder viewHolder, ScheduleData scheduleData, boolean checker, int diffDays, int currHh) {

        String strCurHH = "";

        if(currHh<10){
            strCurHH = "0" + String.valueOf(currHh);
        }

        strCurHH = strCurHH + "00";



        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_wind);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_snowing);

        if(diffDays==0){
            //오늘이면 시간비교를 해서 해당 시간의 날씨예보를 담을것

            setNowWxCond(viewHolder, scheduleData, strCurHH);

        }else if(diffDays==1){
                //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
                //POP 강수확률
                //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
                //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
                //오전
                //만약 비가 온다면, 비, 소나기,눈, 비/눈
                //비가 안오면,

                    String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am();
            Log.i(TAG, "1일후 오전 : " + wx);
                    //기본날씨
                    if (wx.equals("맑음")) {
                        viewHolder.ivAm.setImageDrawable(sun);
                    } else if (wx.equals("구름많음")) {
                        viewHolder.ivAm.setImageDrawable(cloudy);
                    } else if (wx.equals("흐림")) {
                        viewHolder.ivAm.setImageDrawable(wind);
                    } else if (wx.contains("비")) {
                        viewHolder.ivAm.setImageDrawable(rain);
                    } else if (wx.contains("눈")) {
                        viewHolder.ivAm.setImageDrawable(snow);
                    } else if (wx.contains("소나기")) {
                        viewHolder.ivAm.setImageDrawable(shower);
                    }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm();
            Log.i(TAG, "1일후 오후 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }


        }else if(diffDays==2){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Am();
            Log.i(TAG, "2 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm();
            Log.i(TAG, "2 오후 : " + wx);

            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }

        }
        else if(diffDays==3){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Am();
            Log.i(TAG, "3 오전 : " + wx);

            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Pm();
            Log.i(TAG, "3 오후 : " + wx);

            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }
        else if(diffDays==4){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Am();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Pm();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }
        else if(diffDays==5){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Am();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Pm();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }
        else if(diffDays==6){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Am();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Pm();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }
        else if(diffDays==7){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Am();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Pm();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivPm.setImageDrawable(shower);
            }
        }
        else if(diffDays==8){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf8();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }
        }else if(diffDays==9){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf9();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }
        }
        else if(diffDays==10){
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf10();
            //기본날씨
            if (wx.equals("맑음")) {
                viewHolder.ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                viewHolder.ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                viewHolder.ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                viewHolder.ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                viewHolder.ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                viewHolder.ivAm.setImageDrawable(shower);
            }
        }

    }


}
