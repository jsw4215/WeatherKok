package com.devpilot.weatherkok.who.kakao.kotlin

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devpilot.weatherkok.R
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.link.WebSharerClient
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.TextTemplate
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class WhoActivity : AppCompatActivity() {
    val TAG : String = "WhoActivity_kotlin"
    var name: String? = null
    var number: String? = null
    var mKakaoApplication: KakaoApplication? = null
    var mSendKakao: SendKakao? = null
    var messageText: String? = ""

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_who)
        var mContext: Context? = null

        mContext = applicationContext
        var mIvKakaoTalk: ImageView? = null
        var LlRegisterTotal: LinearLayout? = null
        var mIvLine: ImageView? = null
        var mIvUrl: ImageView? = null

        //mIvKakaoTalk = findViewById<ImageView>(R.id.who_kakao)

        LlRegisterTotal = findViewById(R.id.ll_register_total)
        //mIvLine = findViewById<ImageView>(R.id.who_line)
        mIvUrl = findViewById<ImageView>(R.id.who_url)

        val intent: Intent = getIntent()
        var where: String = ""
        var whenString: String = ""

        if(intent!=null){
            where = intent.getStringExtra("where").toString();
            whenString = intent.getStringExtra("when").toString()
        }

        messageText = getString(R.string.share_message) + packageName

//        mIvKakaoTalk.setOnClickListener(View.OnClickListener {
//            sendKakaoMessage(mContext, messageText!!)
//        })
//
//        mIvLine.setOnClickListener(View.OnClickListener {
//
//            postAsLine(messageText)
//
//        })

        LlRegisterTotal.setOnClickListener(View.OnClickListener {

            onBackPressed()

        })

        mIvUrl.setOnClickListener(View.OnClickListener {

            var clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clipData = ClipData.newPlainText("URL", getString(R.string.play_store_url) + packageName)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(mContext, "URL??? ?????????????????????.", Toast.LENGTH_SHORT).show();
        })

    }

    @Throws(UnsupportedEncodingException::class)
    fun postAsLine(message: String?) {
        val startLink: Intent? = packageManager.getLaunchIntentForPackage("jp.naver.line.android")
        val url: String

        url = if (startLink != null) {
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            "http://line.me/R/msg/text/$encodedMessage"
        } else {
            "https://play.google.com/store/apps/details?id=jp.naver.line.android"
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    fun sendKakaoMessage(context : Context, messageText: String) {

        val defaultFeed = TextTemplate(
            text = messageText.trimIndent(),
            link = Link(
                webUrl = "https://developers.kakao.com",
                mobileWebUrl = "https://developers.kakao.com"
            )
        )

        // ?????? ????????? ?????????

// ???????????? ???????????? ??????
        if (LinkClient.instance.isKakaoLinkAvailable(context)) {
            // ?????????????????? ??????????????? ?????? ??????
            LinkClient.instance.defaultTemplate(context, defaultFeed) { linkResult, error ->
                if (error != null) {
                    Log.e(TAG, "??????????????? ????????? ??????", error)
                }
                else if (linkResult != null) {
                    Log.d(TAG, "??????????????? ????????? ?????? ${linkResult.intent}")
                    startActivity(linkResult.intent)

                    // ??????????????? ???????????? ??????????????? ?????? ?????? ???????????? ????????? ?????? ?????? ???????????? ?????? ???????????? ?????? ??? ????????????.
                    Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                    Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                }
            }
        } else {
            // ???????????? ?????????: ??? ?????? ?????? ??????
            // ??? ?????? ?????? ??????
            val sharerUrl = WebSharerClient.instance.defaultTemplateUri(defaultFeed)

            // CustomTabs?????? ??? ???????????? ??????

            // 1. CustomTabs?????? Chrome ???????????? ??????
            try {
                KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
            } catch(e: UnsupportedOperationException) {
                // Chrome ??????????????? ?????? ??? ????????????
            }

            // 2. CustomTabs?????? ???????????? ?????? ???????????? ??????
            try {
                KakaoCustomTabsClient.open(context, sharerUrl)
            } catch (e: ActivityNotFoundException) {
                // ????????? ??????????????? ?????? ??? ????????????
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        super.onBackPressed()

        finish()

    }
}