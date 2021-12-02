package com.devpilot.weatherkok.weather.webview;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.src.BaseActivity;

public class WebViewActivity extends BaseActivity {
    private static final String TAG = WebViewActivity.class.getSimpleName();

    public static WebView webView;
    public static WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //개인정보 처리방침
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webview);

        webView.setWebViewClient(new WebViewClient()); //클릭시 새창 뜨지않게
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl("file:///android_asset/WeatherKok.html");

    }

}
