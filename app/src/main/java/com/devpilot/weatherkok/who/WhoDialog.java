package com.devpilot.weatherkok.who;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.main.MainActivity;
import com.devpilot.weatherkok.who.kakao.kotlin.KakaoApplication;
import com.devpilot.weatherkok.who.kakao.kotlin.SendKakao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WhoDialog extends Dialog {
    private static final String TAG = WhoDialog.class.getSimpleName();
    ImageView mIvKakaoTalk;
    ImageView mIvFacebook;
    ImageView mIvLine;
    String name;
    String number;
    Context mContext;
    KakaoApplication mKakaoApplication;
    SendKakao mSendKakao;

    public WhoDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who);
        mContext = mContext.getApplicationContext();

//        mIvKakaoTalk = findViewById(R.id.who_kakao);
//        mIvLine = findViewById(R.id.who_line);

//        mIvKakaoTalk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSendKakao = new SendKakao();
//                mSendKakao.sendKakaoMessage(mContext);
//
//                //Intent intent = new Intent(mContext, SendKakao.class);
//                //mContext.startActivity(intent);
//            }
//        });
//
//        mIvLine.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                try {
//                    postAsLine("test");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


    }

    public void postAsLine(String message) throws UnsupportedEncodingException {
        Intent startLink = mContext.getPackageManager().getLaunchIntentForPackage("jp.naver.line.android");
        String url;
        if (startLink != null) {
            String encodedMessage = URLEncoder.encode(message, "UTF-8");
            url = "http://line.me/R/msg/text/" + encodedMessage;
        } else {
            url = "https://play.google.com/store/apps/details?id=jp.naver.line.android";
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mContext.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

}
