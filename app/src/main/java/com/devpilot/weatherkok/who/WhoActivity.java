package com.devpilot.weatherkok.who;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.who.kakao.kotlin.KakaoApplication;
import com.devpilot.weatherkok.who.kakao.kotlin.SendKakao;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class WhoActivity extends AppCompatActivity {

    ImageView mIvKakaoTalk;
    ImageView mIvFacebook;
    ImageView mIvLine;
    String name;
    String number;
    Context mContext;
    KakaoApplication mKakaoApplication;
    SendKakao mSendKakao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who);
        mContext = getApplicationContext();

//        mIvKakaoTalk = findViewById(R.id.who_kakao);
//        mIvLine = findViewById(R.id.who_line);

        //페이스북 공유
//        mIvFacebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentTitle("페이스북 공유 링크입니다.")
//                        .setImageUrl(Uri.parse("게시물에 표시될 썸네일 이미지의 url"))
//                        .setContentUrl(Uri.parse("공유될 링크"))
//                        .setContentDescription("2~4개의 문장으로 구성된 콘텐츠 설명")
//                        .build();
//                ShareDialog shareDialog = new ShareDialog((Activity) getApplicationContext());
//            }
//        });


        //라인 메세지 보내는걸로?할까?
//        mIvLine.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent startLink = getPackageManager().getLaunchIntentForPackage("jp.naver.line.android");
//                String url;
//                String message = "공유하기";
//                    if (startLink != null) {
//                        String encodeMessage = URLEncoder.encode(message, "UTF-8");
//                        url = "http://line.me/R/msg/text/" + encodeMessage;
//                    } else {
//                        url = "https://play.google.com/store/apps/details?id=jp.naver.line.android";
//                    }
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(url));
//                startActivity(intent);
//            }
//        });

        mIvKakaoTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendKakao = new SendKakao();
                mSendKakao.sendKakaoMessage(mContext);

                //Intent intent = new Intent(mContext, SendKakao.class);
                //mContext.startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

}