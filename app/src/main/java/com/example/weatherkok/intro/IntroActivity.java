package com.example.weatherkok.intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.weatherkok.R;
import com.example.weatherkok.main.MainActivity;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.weather.utils.WxKokDataPresenter;
import com.example.weatherkok.when.utils.CalenderInfoPresenter;

public class IntroActivity extends BaseActivity {
    private static final String TAG = IntroActivity.class.getSimpleName();

    WxKokDataPresenter mWxKokDataPresenter;
    CalenderInfoPresenter mCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        initDummy();


        mWxKokDataPresenter = new WxKokDataPresenter(getBaseContext());

        mWxKokDataPresenter.getScheduleDateWxApi();

        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000); // 1초 후에 hd handler 실행  3000ms = 3초

    }

    private void initDummy() {
        mCal = new CalenderInfoPresenter(getApplication());
        mCal.makeDummySchedule();

    }

    private class splashhandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            IntroActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

}
