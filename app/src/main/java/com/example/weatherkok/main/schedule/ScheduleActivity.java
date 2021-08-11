package com.example.weatherkok.main.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.weatherkok.R;
import com.example.weatherkok.datalist.data.ScheduleData;
import com.example.weatherkok.datalist.data.wxdata.Wx;
import com.example.weatherkok.datalist.data.wxdata.am.AM;
import com.example.weatherkok.datalist.data.wxdata.pm.PM;
import com.example.weatherkok.main.schedule.utils.ScheduleRecyclerViewAdapter;
import com.example.weatherkok.where.utils.GpsTracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    String TAG = "ScheduleActivity";
    ArrayList<ScheduleData> mScheduleList = new ArrayList<>();
    //Preference를 불러오기 위한 키
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";

    private ScheduleRecyclerViewAdapter mScheduleRecyclerViewAdapter;
    private RecyclerView mScheduleRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Log.i("리사이클러뷰", "onCreate 실행");

        mScheduleRecyclerView = findViewById(R.id.rv_schedule_contents);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mScheduleRecyclerView.setLayoutManager(mLayoutManager);
        //어댑터 연결
        mScheduleRecyclerViewAdapter = new ScheduleRecyclerViewAdapter(mScheduleList);
        mScheduleRecyclerView.setAdapter(mScheduleRecyclerViewAdapter);

        String test_date = "2021-08-15 09:15:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date();
        try {
            date = dateFormat.parse(test_date);
        } catch (ParseException e) {


            e.printStackTrace();
        }

        //Schedule Test 객체 만들기
        AM am = new AM();
        am.setCloud("구름 많음");
        am.setRain("강수 많음");
        am.setSnow("눈 많음");
        am.setTemperature(23);
        am.setWindy("강풍");

        PM pm = new PM();
        pm.setCloud("구름 없음");
        pm.setRain("강수 없음");
        pm.setSnow("눈 없음");
        pm.setTemperature(28);
        pm.setWindy("바람 없음");

        Wx wx = new Wx();
        wx.setAm(am);
        wx.setPm(pm);
        wx.setDate(date);

        ArrayList<Wx> WxList = new ArrayList<>();
        WxList.add(wx);




        ScheduleData scheduleData = new ScheduleData();
        scheduleData.setFcst(WxList);

        //주소
        //현재위치 좌표 받아오기기
        double latitude  = 35.8565254;
        double longitude = 128.6090332;

        GpsTracker gpsTracker = new GpsTracker(getApplicationContext());
        Location currentLocation = gpsTracker.getLocation();
        latitude = currentLocation.getLatitude();
        longitude = currentLocation.getLongitude();

        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> gList = null;
        try {
            gList = geocoder.getFromLocation(latitude,longitude,8);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "setMaskLocation() - 서버에서 주소변환시 에러발생");
            // Fragment1 으로 강제이동 시키기
        }
        if (gList != null) {
            if (gList.size() == 0) {
                Toast.makeText(getBaseContext(), " 현재위치에서 검색된 주소정보가 없습니다. ", Toast.LENGTH_SHORT).show();

            } else {

                Address address = gList.get(0);
                String sido = address.getAdminArea();       // 대구광역시
                String gugun = address.getSubLocality();    // 수성구

                scheduleData.setAddress(address.getAddressLine(0));

                Log.i(TAG, address.toString());

                Log.i(TAG,sido + gugun);
            }
        }
        scheduleData.setDate(date);

        //Preference에 날씨 정보 객체 저장하기
        Gson gson = new GsonBuilder().create();
        //JSON으로 변환
        String jsonString = gson.toJson(scheduleData, ScheduleData.class);
        Log.i("jsonString : ",jsonString);
        SharedPreferences sp = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("schedule",jsonString);
        editor.commit();

        //Preference에서 날씨 정보 객체 불러오기
        String loaded = sp.getString("schedule","");

        ScheduleData loadedFromSP = gson.fromJson(loaded, ScheduleData.class);
        String a = loadedFromSP.getAddress();

        Log.i("loaded : ", a);
        Log.i("loaded : ", loadedFromSP.getDate().toString());
        Log.i("loaded : ", loadedFromSP.getFcst().get(0).getAm().getCloud());


    }

}