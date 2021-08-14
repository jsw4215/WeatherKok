package com.example.weatherkok.weather.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.example.weatherkok.weather.models.geom.LatLon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LatLonCalculator {
    private static final String TAG = LatLonCalculator.class.getSimpleName();

    public LatLon getLatLonWithAddr(String address, Context context) {
        LatLon resultLatLon = new LatLon();
        double v1 = 0.0;
        double v2 = 0.0;
        List<Address> list = null;

        Geocoder geocoder = new Geocoder(context);

        try {
            list = geocoder.getFromLocationName(address,10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "주소->위도경도 변환시 오류");
        }

        if(list.size()==0){
            Toast.makeText(context, "주소와 일치하는 위도, 경도값을 찾을 수 없습니다.",1);
        }

        resultLatLon.setLatitude(list.get(0).getLatitude());
        resultLatLon.setLongitude(list.get(0).getLongitude());

        return resultLatLon;
    }

    public Map<String, Object> getGridxy(double v1, double v2) {

        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        double DEGRAD = Math.PI / 180.0;
        // double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("lat", v1);
        map.put("lng", v1);
        double ra = Math.tan(Math.PI * 0.25 + (v1) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = v2 * DEGRAD - olon;
        if (theta > Math.PI)
            theta -= 2.0 * Math.PI;
        if (theta < -Math.PI)
            theta += 2.0 * Math.PI;
        theta *= sn;

        map.put("x", Math.floor(ra * Math.sin(theta) + XO + 0.5));
        map.put("y", Math.floor(ro - ra * Math.cos(theta) + YO + 0.5));
        Log.i(TAG, "x,y" + map.get("x") + map.get("y"));
        return map;
    }

}