package com.example.weatherkok.src;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


import com.example.weatherkok.config.XAccessTokenInterceptor;
import com.example.weatherkok.weather.WeatherService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationClass extends Application {
    public static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=uft-8");
    public static MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

    // 테스트 서버 주소
    public static String BASE_URL = "https://api.vworld.kr/req/";
    public static String BASE_URL2 = "http://apis.data.go.kr/1360000/MidFcstInfoService/";
    // 실서버 주소
//    public static String BASE_URL = "https://template.softsquared.com/";

    public static SharedPreferences sSharedPreferences = null;

    // SharedPreferences 키 값
    public static String TAG = "TEMPLATE_APP";

    // JWT Token 값
    public static String X_ACCESS_TOKEN = "X_ACCESS_TOKEN";

    //날짜 형식
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    // Retrofit 인스턴스
    public static Retrofit retrofit;


    @Override
    public void onCreate() {
        super.onCreate();

        if (sSharedPreferences == null) {
            sSharedPreferences = getApplicationContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        }
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .addNetworkInterceptor(new XAccessTokenInterceptor()) // JWT 자동 헤더 전송
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static Retrofit getRetrofitForWeather() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

//        Gson gson = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            gson = new GsonBuilder().
//                    registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
//                        @Override public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//                            return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
//                        } }).create();
//        }
        //return gson.fromJson(resultStr, User.class);


        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
                    .connectTimeout(5000, TimeUnit.MILLISECONDS)
                    .addNetworkInterceptor(new XAccessTokenInterceptor()) // JWT 자동 헤더 전송
                    .addInterceptor(interceptor)
                    .build();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL2)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }

        return retrofit;
    }

}