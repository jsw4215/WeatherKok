package com.devpilot.weatherkok.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.alarm.PreferenceHelper;
import com.devpilot.weatherkok.main.MainActivity;
import com.devpilot.weatherkok.src.BaseActivity;
import com.devpilot.weatherkok.weather.dialog.AdditionErrorDialog;
import com.devpilot.weatherkok.weather.utils.WxListAdapter;
import com.devpilot.weatherkok.when.models.Schedule;
import com.devpilot.weatherkok.when.models.ScheduleList;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Iterator;

import static com.devpilot.weatherkok.alarm.Constants.SHARED_PREF_AD_KEY;

public class WxListActivity extends BaseActivity {
    private static final String TAG = WxListActivity.class.getSimpleName();
    RecyclerView mRvBml;
    WxListAdapter mBmlAdapter;
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    ScheduleList mScheduleList;
    TextView mTvBmWxListDelete;
    TextView mTvBmWxListAdd;
    ImageView mIvBackArrow;
    ImageView mIvBmTrash;
    boolean mTrashChecker = false;
    ArrayList<Boolean> mDelList = new ArrayList<>();
    private AdView mAdview;
    FrameLayout adContainerView;
    int mNumOfList=-1;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bookmarklist);

        boolean key = PreferenceHelper.getBoolean(getApplicationContext(), SHARED_PREF_AD_KEY);

        initView();

        mScheduleList = new ScheduleList();
        ArrayList<Schedule> temp = new ArrayList<>();
        mScheduleList.setScheduleArrayList(temp);

        ScheduleList temp2 = getScheduleFromSp();

        mScheduleList.setScheduleArrayList(temp2.getScheduleArrayList());

        if(mScheduleList!=null&&mScheduleList.getScheduleArrayList()!=null) {
            mNumOfList = mScheduleList.getScheduleArrayList().size();
        }

        makeRv(mScheduleList);

        listeners();

        admob(key);
    }

    private void admob(boolean key) {

        adContainerView = findViewById(R.id.admob_container);

        if(key){

            adContainerView.setVisibility(View.INVISIBLE);

        }else{

            adContainerView.setVisibility(View.VISIBLE);

            MobileAds.initialize(this, new OnInitializationCompleteListener() { //광고 초기화
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            mAdview = new AdView(this);
            mAdview.setAdUnitId(getString(R.string.ad_mob_unit_id));
            adContainerView.addView(mAdview);
            loadBanner();

        }

    }

    private void loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        AdRequest adRequest =
                new AdRequest.Builder().build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        mAdview.setAdSize(adSize);


        // Step 5 - Start loading the ad in the background.
        mAdview.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }


    private void initView(){
        mRvBml = findViewById(R.id.rv_bmlist);

        mTvBmWxListAdd = findViewById(R.id.tv_bmlist_add_place);

        mTvBmWxListDelete = findViewById(R.id.tv_bmlist_deletion);

        mIvBmTrash = findViewById(R.id.iv_bml_trashcan);

        mIvBackArrow = findViewById(R.id.iv_bml_back_arrow_top_bar);

        mIvBmTrash.setVisibility(View.GONE);
    }



    private void listeners(){

        AdditionErrorDialog additionErrDialog = new AdditionErrorDialog(WxListActivity.this);

        mTvBmWxListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mNumOfList>4){

                additionErrDialog.show();
                Window window = additionErrDialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                }else {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }

            }
        });

        mTvBmWxListDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIvBmTrash.setVisibility(View.VISIBLE);

                mTvBmWxListDelete.setVisibility(View.GONE);

                mTrashChecker = true;

                mBmlAdapter.notifyDataSetChanged();
            }
        });

        mIvBmTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //삭제처리

                mIvBmTrash.setVisibility(View.GONE);

                mTvBmWxListDelete.setVisibility(View.VISIBLE);

                deleteList();

                mTrashChecker = false;

                makeRv(mScheduleList);

                setScheduleDataInToSp(mScheduleList);

                if(mScheduleList.getScheduleArrayList().size()==0){
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }

            }
        });

        mIvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void deleteList() {

        mDelList = mBmlAdapter.getDeleteList();

        for(int i= 0;i<mScheduleList.getScheduleArrayList().size();i++){
            if(mDelList.get(i)){
                mScheduleList.getScheduleArrayList().get(i).setYear("delete");
            }
        }

            Iterator it = mScheduleList.getScheduleArrayList().iterator();

            while(it.hasNext()) {
                Schedule schedule = (Schedule) it.next();

                if(schedule.getYear()!=null&&schedule.getYear().equals("delete")){
                    it.remove();
                }
            }

    }

    private ScheduleList getValueWithKeyFromSp(String key) {

        //Preference에 날씨 정보 객체 불러오기

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null일 경우 처리할것.
        String loaded = pref.getString(key, "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference에 저장된 데이터 class 형태로 불러오기 완료

        return scheduleList;

    }

    public boolean ismTrashChecker() {
        return mTrashChecker;
    }

    private void makeRv(ScheduleList scheduleList){

        mBmlAdapter = new WxListAdapter(WxListActivity.this, scheduleList);

        mRvBml.setLayoutManager(new LinearLayoutManager(this));
        mRvBml.setAdapter(mBmlAdapter);
        mBmlAdapter.notifyDataSetChanged();

    }

    private void setScheduleDataInToSp(ScheduleList scheduleList) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        //Preference에 정보 객체 저장하기
        //JSON으로 변환
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //초기화
        //editor.remove(year + month);

        editor.putString("schedule", jsonString);
        editor.commit();
        //저장완료

    }

    private ScheduleList getScheduleFromSp() {

        //Preference에 날씨 정보 객체 불러오기

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null일 경우 처리할것.
        String loaded = pref.getString("schedule", "");

        scheduleList = gson.fromJson(loaded, ScheduleList.class);
        //Preference에 저장된 데이터 class 형태로 불러오기 완료

        return scheduleList;

    }


}
