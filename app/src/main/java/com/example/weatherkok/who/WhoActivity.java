package com.example.weatherkok.who;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weatherkok.R;
import com.example.weatherkok.who.kakao.kotlin.KakaoApplication;
import com.example.weatherkok.who.kakao.kotlin.SendKakao;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.kakao.sdk.link.KakaoLinkIntentClient;
import com.kakao.sdk.link.model.KakaoLinkAttachment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WhoActivity extends AppCompatActivity {

    ImageView mIvKakaoTalk;
    ImageView mIvFacebook;
    ImageView mIvLine;
    ImageView mIvSms;
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

        mIvKakaoTalk = findViewById(R.id.who_kakao);
        mIvFacebook = findViewById(R.id.who_face_book);
        mIvLine = findViewById(R.id.who_line);
        mIvSms = findViewById(R.id.who_sms);

        //페이스북 공유
        mIvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentTitle("페이스북 공유 링크입니다.")
                        .setImageUrl(Uri.parse("게시물에 표시될 썸네일 이미지의 url"))
                        .setContentUrl(Uri.parse("공유될 링크"))
                        .setContentDescription("2~4개의 문장으로 구성된 콘텐츠 설명")
                        .build();
                ShareDialog shareDialog = new ShareDialog((Activity) getApplicationContext());
            }
        });


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

        mIvSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
            cursor.moveToFirst();
            name = cursor.getString(0);        //0은 이름을 얻어옵니다.
            number = cursor.getString(1);   //1은 번호를 받아옵니다.
            cursor.close();
            Log.d("number : ", number);
            Log.d("name : ",name);
            try {
                //전송
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, "test message", null, null);
                Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}