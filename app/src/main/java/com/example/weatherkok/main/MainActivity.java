package com.example.weatherkok.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weatherkok.R;
import com.example.weatherkok.where.WhereActivity;

public class MainActivity extends AppCompatActivity {
    //Preference를 불러오기 위한 키
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    Context mContext;
    String TAG = "MainActivity";
    TextView mTvMainWhere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getBaseContext();

        String where = "where";
        String when = "when";
        String who = "who";
        //Intent 확인
        Intent intent = getIntent();

        //preference 연동
//첫번째 매개변수(PREFERENCE) : 저장/불러오기 하기 위한 key이다.
//이 고유키로 앱의 할당된 저장소(data/data/[패키지 이름]/shared_prefs) 에 "com.studio572.samplesharepreference.xml" 로 저장된다.
//이 때 xml 파일명은 사용자 정의가 가능하다.
//
//> 두번째 매개변수(MODE_PRIVATE) : 프리퍼런스의 저장 모드를 정의한다.
//        [MODE_PRIVATE : 이 앱안에서 데이터 공유]
//[MODE_WORLD_READABLE : 다른 앱과 데이터 읽기 공유]
//[MODE_WORLD_WRITEABLE : 다른 앱과 데이터 쓰기 공유]
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

//불러오기
// key에 해당한 value를 불러온다.
// 두번째 매개변수는 , key에 해당하는 value값이 없을 때에는 이 값으로 대체한다.
        String result = pref.getString(where, "");

        Log.i(TAG, result);

        mTvMainWhere = findViewById(R.id.tv_main_where);
        mTvMainWhere.setText(result);



        //이미지 연동 -> where, when, who
        LinearLayout llWhere = findViewById(R.id.ll_main_where);

        //클릭하면 화면 이동
        llWhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), WhereActivity.class);
                startActivity(intent);
            }
        });




    }



}