package com.example.weatherkok.main.schedule.utils;

import android.content.Context;
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
import com.example.weatherkok.datalist.data.wxdata.Wx;

import java.util.ArrayList;

//스케줄리사이클러뷰어댑터 라는 이름의 클래스를 정의하겠다. 이 클래스는 리사이클러.어댑터라는 상속자를 가진다.
public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.Holder> {
    String TAG = "ScheduleRecyclerViewAdapter"; //변수타입 string 변수명 tag 변수값
    Context mContext;
    private ArrayList<ScheduleData> mScheduleList;

    //스케줄리사이클러뷰어댑터 클래스의 생성자.
    public ScheduleRecyclerViewAdapter(ArrayList<ScheduleData> mScheduleList) {
        this.mScheduleList = mScheduleList;
        Log.i("스케줄리스트","스케줄리스트"+ mScheduleList);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        //레이아웃 xml 파일을 View객체로 만들기 위해 LayoutInflater를 이용
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_schedule, parent, false);
        return new ScheduleRecyclerViewAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleRecyclerViewAdapter.Holder holder, int position) {

        holder.mDay.setText(mScheduleList.get(position).getDay());
        holder.mAddress.setText(mScheduleList.get(position).getAddress());

    }

    //어댑터가 가지고 있는 아이템 개수 지정해주기
    @Override
    public int getItemCount() {return mScheduleList.size(); }

    public class Holder extends RecyclerView.ViewHolder {
        TextView mDay;
        TextView mAddress;
        ImageView mWeather;
        TextView mTemper;
        //여기에 리사이클러뷰 선언

        public Holder(@NonNull View itemView) {
            super(itemView);
            mDay = itemView.findViewById(R.id.tv_item_day);
            mAddress = itemView.findViewById(R.id.tv_item_address);
        }

        public void onBind(ScheduleData scheduleData) {
            //여기에 값 입력
            mDay.setText(scheduleData.getDay());
            mAddress.setText(scheduleData.getAddress());

        }
    }



}