package com.example.weatherkok.weather.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.weather.WxListActivity;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.when.utils.RecyclerViewAdapter;

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
        for(int i=0;i<scheduleList.getScheduleArrayList().size();i++) {
            deleteList.add(false);
        }
        //scheduleList.getScheduleArrayList().get(0).getScheduleData().setScheduledDate("20210818");
        //scheduleList.getScheduleArrayList().get(1).getScheduleData().setScheduledDate("20210819");
    }

    public ArrayList<Boolean> getDeleteList() {
        return deleteList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        if(type==TRASH){

            Log.i(TAG, "onCreateViewHolder TRASH");
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_bookmark_del_list, viewGroup, false);
            return new DelViewHolder(view);

        }else {

            Log.i(TAG, "onCreateViewHolder NORMAL");
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_bookmark_list, viewGroup, false);
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
    Log.i(TAG, "몇번" + position);
        String scheduledDate = scheduleList.getScheduleArrayList().get(position).getScheduleData().getScheduledDate();

        ((ViewHolder) holder).tvDate.setText(scheduledDate);
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

    private void setLocation(RecyclerView.ViewHolder holder, int position) {

        String location = scheduleList.getScheduleArrayList().get(position).getWhere();

        location = removeAdminArea(location);

        ((ViewHolder) holder).tvLoc.setText(location);
    }

    private void setWxImg(RecyclerView.ViewHolder holder, int positionItem) {

        ScheduleData scheduleData = scheduleList.getScheduleArrayList().get(positionItem).getScheduleData();

        boolean checker = checkAMPM();

        int diffDays = (int) howFarFromToday(scheduleData.getScheduledDate());

        findScheduleDateWxData(holder, scheduleData, checker, diffDays);

    }

    private void setDelDate(RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "몇번" + position);
        String scheduledDate = scheduleList.getScheduleArrayList().get(position).getScheduleData().getScheduledDate();

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

        findScheduleDateWxDataDel(holder, scheduleData, checker, diffDays);

    }

    private Date getToday(){
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
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

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private RecyclerViewAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(RecyclerViewAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return scheduleList.getScheduleArrayList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvLoc;
        //TextView tvTemp;
        ImageView ivPm;
        ImageView ivAm;

        ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_bm_weather_dates_list);
            tvLoc = view.findViewById(R.id.tv_bm_weather_location_list);
            //tvTemp = view.findViewById(R.id.tv_item_nowtemper);
            ivAm = view.findViewById(R.id.iv_bm_weather_am_list);
            ivPm = view.findViewById(R.id.iv_bm_weather_pm_list);

        }

    }

    public class DelViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvLoc;
        //TextView tvTemp;
        ImageView ivPm;
        ImageView ivAm;
        RadioButton rbBmList;

        DelViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_bm_weather_dates_list);
            tvLoc = view.findViewById(R.id.tv_bm_weather_location_list);
            //tvTemp = view.findViewById(R.id.tv_item_nowtemper);
            ivAm = view.findViewById(R.id.iv_bm_weather_am_list);
            ivPm = view.findViewById(R.id.iv_bm_weather_pm_list);
            rbBmList = view.findViewById(R.id.rb_item_list);
            rbBmList.setVisibility(View.VISIBLE);
        }

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

    private void findScheduleDateWxData(RecyclerView.ViewHolder holder,ScheduleData scheduleData, boolean checker, int diffDays) {

        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_wind);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_snowing);

        String strCurHH = makingStrHour();

        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        Log.i(TAG, "오늘과 몇일 차이인가? : " + diffDays);
        if (diffDays == 0) {
            //오늘이면 시간비교를 해서 해당 시간의 날씨예보를 담을것

            setNowWxCond(holder, scheduleData, strCurHH);

        } else if (diffDays == 1) {

            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am();
                if (wx.equals("맑음")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(sun);
                } else if (wx.equals("구름많음")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
                } else if (wx.equals("흐림")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 1) {
                //비
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 2) {
                //비눈
                ((ViewHolder) holder).ivAm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 3) {
                //눈
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 4) {
                //소나기
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }


            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm();
                if (wx.equals("맑음")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(sun);
                } else if (wx.equals("구름많음")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
                } else if (wx.equals("흐림")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 1) {
                //비
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 2) {
                //비눈
                ((ViewHolder) holder).ivPm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 3) {
                //눈
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 4) {
                //소나기
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }


        } else if (diffDays == 2) {

            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Am();
                if (wx.equals("맑음")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(sun);
                } else if (wx.equals("구름많음")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
                } else if (wx.equals("흐림")) {
                    ((ViewHolder) holder).ivAm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 1) {
                //비
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 2) {
                //비눈
                ((ViewHolder) holder).ivAm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 3) {
                //눈
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 4) {
                //소나기
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }


            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm();
                if (wx.equals("맑음")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(sun);
                } else if (wx.equals("구름많음")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
                } else if (wx.equals("흐림")) {
                    ((ViewHolder) holder).ivPm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 1) {
                //비
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 2) {
                //비눈
                ((ViewHolder) holder).ivPm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 3) {
                //눈
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 4) {
                //소나기
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }

        } else if (diffDays == 3) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Am();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Pm();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }

        } else if (diffDays == 4) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Am();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Pm();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 5) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Am();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Pm();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 6) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Am();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Pm();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 7) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Am();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Pm();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 8) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf8();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            }

        } else if (diffDays == 9) {
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf9();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("소나기")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(shower);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            }
        } else if (diffDays == 10) {
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf10();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(sun);
                ((ViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((ViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(wind);
                ((ViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(rain);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((ViewHolder) holder).ivAm.setImageDrawable(snow);
                ((ViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("소나기")) {
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
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_wind);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_snowing);

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
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        ((ViewHolder)viewHolder).ivAm.setImageDrawable(shower);
                    }
                }
            }


        }

        String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm();
        Log.i(TAG, "오늘 오후 : " + wx);
        //기본날씨
        if (wx.equals("맑음")) {

            ((ViewHolder)viewHolder).ivPm.setImageDrawable(sun);
        } else if (wx.equals("구름많음")) {
            ((ViewHolder)viewHolder).ivPm.setImageDrawable(cloudy);
        } else if (wx.equals("흐림")) {
            ((ViewHolder)viewHolder).ivPm.setImageDrawable(wind);
        } else if (wx.contains("비")) {
            ((ViewHolder)viewHolder).ivPm.setImageDrawable(rain);
        } else if (wx.contains("눈")) {
            ((ViewHolder)viewHolder).ivPm.setImageDrawable(snow);
        } else if (wx.contains("소나기")) {
            ((ViewHolder)viewHolder).ivPm.setImageDrawable(shower);
        }

    }

    private void setDelNowWxCond(RecyclerView.ViewHolder viewHolder, ScheduleData scheduleData, String strCurHH) {

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
        for (int i = 0; i < scheduleData.getFcst().getWxToday().size(); i++) {

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)) {

                if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                    Log.i(TAG, "오늘 현재");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        ((DelViewHolder)viewHolder).ivAm.setImageDrawable(shower);
                    }
                }
            }


        }

        String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf0Pm();
        Log.i(TAG, "오늘 오후 : " + wx);
        //기본날씨
        if (wx.equals("맑음")) {

            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(sun);
        } else if (wx.equals("구름많음")) {
            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(cloudy);
        } else if (wx.equals("흐림")) {
            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(wind);
        } else if (wx.contains("비")) {
            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(rain);
        } else if (wx.contains("눈")) {
            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(snow);
        } else if (wx.contains("소나기")) {
            ((DelViewHolder)viewHolder).ivPm.setImageDrawable(shower);
        }

    }


    private void findScheduleDateWxDataDel(RecyclerView.ViewHolder holder,ScheduleData scheduleData, boolean checker, int diffDays) {

        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_wind);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_snowing);

        String strCurHH = makingStrHour();

        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        Log.i(TAG, "오늘과 몇일 차이인가? : " + diffDays);
        if (diffDays == 0) {
            //오늘이면 시간비교를 해서 해당 시간의 날씨예보를 담을것

            setDelNowWxCond(holder, scheduleData, strCurHH);

        } else if (diffDays == 1) {

            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Am();
                if (wx.equals("맑음")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
                } else if (wx.equals("구름많음")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
                } else if (wx.equals("흐림")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 1) {
                //비
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 2) {
                //비눈
                ((DelViewHolder) holder).ivAm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 3) {
                //눈
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Am() == 4) {
                //소나기
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }


            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf1Pm();
                if (wx.equals("맑음")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
                } else if (wx.equals("구름많음")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
                } else if (wx.equals("흐림")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 1) {
                //비
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 2) {
                //비눈
                ((DelViewHolder) holder).ivPm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 3) {
                //눈
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt1Pm() == 4) {
                //소나기
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }


        } else if (diffDays == 2) {

            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Am();
                if (wx.equals("맑음")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
                } else if (wx.equals("구름많음")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
                } else if (wx.equals("흐림")) {
                    ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 1) {
                //비
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 2) {
                //비눈
                ((DelViewHolder) holder).ivAm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 3) {
                //눈
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Am() == 4) {
                //소나기
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }


            if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 0) {
                String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf2Pm();
                if (wx.equals("맑음")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
                } else if (wx.equals("구름많음")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
                } else if (wx.equals("흐림")) {
                    ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
                }
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 1) {
                //비
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 2) {
                //비눈
                ((DelViewHolder) holder).ivPm.setImageDrawable(snowRain);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 3) {
                //눈
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (scheduleData.getFcst().getWxList().getItem().get(0).getRnSt2Pm() == 4) {
                //소나기
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }

        } else if (diffDays == 3) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Am();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf3Pm();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }

        } else if (diffDays == 4) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Am();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf4Pm();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 5) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Am();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf5Pm();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 6) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Am();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf6Pm();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 7) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Am();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
            }

            wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf7Pm();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(snow);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivPm.setImageDrawable(shower);
            }
        } else if (diffDays == 8) {
            //만약 비가 온다면, 비, 소나기,눈, 비/눈
            //비가 안오면,
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf8();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            }

        } else if (diffDays == 9) {
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf9();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("소나기")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(shower);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            }
        } else if (diffDays == 10) {
            String wx = scheduleData.getFcst().getWxList().getItem().get(0).getWf10();
            Log.i(TAG, "오늘 오전 : " + wx);
            //기본날씨
            if (wx.equals("맑음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(sun);
                ((DelViewHolder) holder).ivPm.setImageDrawable(sun);
            } else if (wx.equals("구름많음")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(cloudy);
                ((DelViewHolder) holder).ivPm.setImageDrawable(cloudy);
            } else if (wx.equals("흐림")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(wind);
                ((DelViewHolder) holder).ivPm.setImageDrawable(wind);
            } else if (wx.contains("비")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(rain);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("눈")) {
                ((DelViewHolder) holder).ivAm.setImageDrawable(snow);
                ((DelViewHolder) holder).ivPm.setImageDrawable(rain);
            } else if (wx.contains("소나기")) {
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
