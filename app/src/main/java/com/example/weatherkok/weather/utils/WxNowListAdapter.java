package com.example.weatherkok.weather.utils;

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

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.weather.WxNowListActivity;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.when.utils.RecyclerViewAdapter;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WxNowListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    String TAG = "BookMarkAdapter";
    String mYearMonth;
    Context mContext;
    ScheduleList scheduleList;
    public static final int TRASH = 1;
    public static final int NORMAL = 0;

    ArrayList<Boolean> deleteList = new ArrayList<>();
    private SparseBooleanArray selectedArray = new SparseBooleanArray();


    public WxNowListAdapter(Context context, ScheduleList List) {
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

        if(holder instanceof WxNowListAdapter.ViewHolder) {
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
                    if(((WxListAdapter.DelViewHolder)holder).rbBmList.isSelected()){
                        ((WxListAdapter.DelViewHolder)holder).rbBmList.setChecked(false);
                        ((WxListAdapter.DelViewHolder)holder).rbBmList.setSelected(false);
                        deleteList.set(position,false);
                    }else{
                        ((WxListAdapter.DelViewHolder)holder).rbBmList.setChecked(true);
                        ((WxListAdapter.DelViewHolder)holder).rbBmList.setSelected(true);
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

        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
            String curHh = makingStrHour();

            setNowWxCond(holder, scheduleList.getScheduleArrayList().get(positionItem).getScheduleData(), curHh);

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

        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)

        //if(amPm) {
            //오후
        String curHh = makingStrHour();

        setDelNowWxCond(holder, scheduleList.getScheduleArrayList().get(positionItem).getScheduleData(), curHh);

       // }

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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //클릭시


                }
            });
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



//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    //클릭시
//
//
//
//                }
//            });
        }

    }

    private void setNowWxCond(RecyclerView.ViewHolder viewHolder,ScheduleData scheduleData, String strCurHH) {
        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_gray);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_rain_snow);

        for (int i = 0; i < scheduleData.getFcst().getWxToday().size(); i++) {

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)) {

                if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                    Log.i(TAG, "오늘 현재");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        ((ViewHolder) viewHolder).ivAm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        ((ViewHolder) viewHolder).ivAm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        ((ViewHolder) viewHolder).ivAm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        ((ViewHolder) viewHolder).ivAm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        ((ViewHolder) viewHolder).ivAm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        ((ViewHolder) viewHolder).ivAm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        ((ViewHolder) viewHolder).ivAm.setImageDrawable(shower);
                    }
                }
            }

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals("1800")) {

                if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                    Log.i(TAG, "오늘 현재");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        ((ViewHolder) viewHolder).ivPm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        ((ViewHolder) viewHolder).ivPm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        ((ViewHolder) viewHolder).ivPm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        ((ViewHolder) viewHolder).ivPm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        ((ViewHolder) viewHolder).ivPm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        ((ViewHolder) viewHolder).ivPm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        ((ViewHolder) viewHolder).ivPm.setImageDrawable(shower);
                    }
                }
            }
        }
    }

    private void setDelNowWxCond(RecyclerView.ViewHolder viewHolder,ScheduleData scheduleData, String strCurHH) {
        //날짜차이만큼에 해당하는 예보 정보를 띄워준다.
        //POP 강수확률
        //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        //- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        Drawable sun = mContext.getResources().getDrawable(R.drawable.ic_sun);
        Drawable cloudy = mContext.getResources().getDrawable(R.drawable.ic_cloudy);
        Drawable snow = mContext.getResources().getDrawable(R.drawable.ic_snow);
        Drawable shower = mContext.getResources().getDrawable(R.drawable.ic_shower);
        Drawable rain = mContext.getResources().getDrawable(R.drawable.ic_rain);
        Drawable wind = mContext.getResources().getDrawable(R.drawable.ic_gray);
        Drawable snowRain = mContext.getResources().getDrawable(R.drawable.ic_rain_snow);

        for (int i = 0; i < scheduleData.getFcst().getWxToday().size(); i++) {

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals(strCurHH)) {

                if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                    Log.i(TAG, "오늘 현재");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        ((DelViewHolder) viewHolder).ivAm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        ((DelViewHolder) viewHolder).ivAm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        ((DelViewHolder) viewHolder).ivAm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        ((DelViewHolder) viewHolder).ivAm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        ((DelViewHolder) viewHolder).ivAm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        ((DelViewHolder) viewHolder).ivAm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        ((DelViewHolder) viewHolder).ivAm.setImageDrawable(shower);
                    }
                }
            }

            if (scheduleData.getFcst().getWxToday().get(i).getTimeHhmm().equals("1800")) {

                if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 0) {
                    Log.i(TAG, "오늘 현재");
                    if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 1) {
                        ((DelViewHolder) viewHolder).ivPm.setImageDrawable(sun);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 3) {
                        ((DelViewHolder) viewHolder).ivPm.setImageDrawable(cloudy);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getWeather() == 4) {
                        ((DelViewHolder) viewHolder).ivPm.setImageDrawable(wind);
                    }

                } else {

                    if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 1) {
                        //비
                        ((DelViewHolder) viewHolder).ivPm.setImageDrawable(rain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 2) {
                        //비눈
                        ((DelViewHolder) viewHolder).ivPm.setImageDrawable(snowRain);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 3) {
                        //눈
                        ((DelViewHolder) viewHolder).ivPm.setImageDrawable(snow);
                    } else if (scheduleData.getFcst().getWxToday().get(i).getRainType() == 4) {
                        //소나기
                        ((DelViewHolder) viewHolder).ivPm.setImageDrawable(shower);
                    }
                }
            }
        }
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

    @Override
    public int getItemViewType(int position) {

        if(((WxNowListActivity)mContext).ismTrashChecker()){
            return TRASH;
        }else {
            return NORMAL;
        }

    }

}
