package com.example.weatherkok.main.schedule.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puing.weatherkok.R;
import com.example.puing.weatherkok.datalist.data.ScheduleData;
import com.example.puing.weatherkok.datalist.data.WeatherData;
import com.example.puing.weatherkok.datalist.data.wxdata.Wx;
import com.example.puing.weatherkok.datalist.data.wxdata.am.AM;
import com.example.puing.weatherkok.schedule.ScheduleActivity;
import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.datalist.data.wxdata.am.AM;

import java.util.ArrayList;


//스케줄리사이클러뷰어댑터 라는 이름의 클래스를 정의하겠다. 이 클래스는 리사이클러.어댑터라는 상속자를 가진다.
public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String TAG = "ScheduleRecyclerViewAdapter"; //변수타입 string 변수명 tag 변수값

    //리사이클러뷰에서 보여줄 데이터 선언
    private ArrayList<ScheduleData> mScheduleData;
    private ArrayList<AM> mAmList;

    //스케줄리사이클러뷰어댑터 클래스의 생성자.
    public ScheduleRecyclerViewAdapter(ArrayList<ScheduleData> mScheduleList) {
        this.mScheduleData = mScheduleList;
        Log.i(TAG, "Constructor");
    }
//    public ScheduleRecyclerViewAdapter(Context context){
//        mContext = context;
//    }

    @NonNull
    @Override
    //리스트의 한 셀에 보여질 뷰를 생성
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        //레이아웃 xml 파일을 View객체로 만들기 위해 LayoutInflater를 이용하여 가져오기
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_schedule, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        ((Holder) viewHolder).mDate.setText(mScheduleData.get(position).getDate.toString());
        ((Holder) viewHolder).mAddress.setText(mScheduleData.get(position).getPlace());

        Log.i(TAG, "date,address");

        // 날씨 뷰홀더에 불러오기, 스케줄액티비티 안에 있는 변수 --> 수신


        if (mAmList.get(position).getRain().equals("강수 많음")) {
            ((Holder) viewHolder).mWeather.setImageResource(R.drawable.ic_rain);
            Log.i(TAG, "비온다");
        } else if (mAmList.get(position).getSnow().equals("눈 많음")) {
            ((Holder) viewHolder).mWeather.setImageResource(R.drawable.ic_snowing);
        } else if (mAmList.get(position).getCloud().equals("구름 많음")) {
            ((Holder) viewHolder).mWeather.setImageResource(R.drawable.ic_cloudy);
        } else if (mAmList.get(position).getWindy().equals("강풍")) {
            ((Holder) viewHolder).mWeather.setImageResource(R.drawable.ic_wind);
        }
//        else if (mAmList.get(position).getRain().equals("소나기")) {
//            ((Holder) viewHolder).mWeather.setImageResource(R.drawable.ic__01_rain);
//        }
//        else if (mAmList.get(position).getRain().equals("눈")) {
//            ((Holder) viewHolder).mWeather.setImageResource(R.drawable.ic__10_snowflake);
//        }
        else {
            ((Holder) viewHolder).mWeather.setImageResource(R.drawable.ic_sun);
        }

    }

    //어댑터가 가지고 있는 아이템 개수 지정해주기
    @Override
    public int getItemCount() {
        return mScheduleData.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView mDate;
        TextView mAddress;
        ImageView mWeather;
        TextView mTemper;

        Holder(@NonNull View itemView) {
            super(itemView);
            mDate = itemView.findViewById(R.id.tv_item_date);
            mAddress = itemView.findViewById(R.id.tv_item_address);
            mWeather = itemView.findViewById(R.id.iv_item_weather);
            mTemper = itemView.findViewById(R.id.tv_item_temper);
        }

    }


}