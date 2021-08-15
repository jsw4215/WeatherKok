package com.example.weatherkok.weather.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.example.weatherkok.weather.models.LatLon;

import java.io.IOException;
import java.util.Arrays;
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
            list = geocoder.getFromLocationName(address, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "주소->위도경도 변환시 오류");
        }

        if (list.size() == 0) {

            List<String> splitAddr = Arrays.asList(address.split(" "));

            splitAddr.remove(splitAddr.size()-1);
            address = "";
            for(int i =0;i<splitAddr.size();i++) {

                if(!(i==(splitAddr.size()-1))){
                    address = address + splitAddr.get(i) + " ";
                }else{
                    address = address + splitAddr.get(i);
                }

            }

            Log.i(TAG, "주소 동 제외" + address);
            getLatLonWithAddr(address, context);

            Toast.makeText(context, "주소와 일치하는 위도, 경도값을 찾을 수 없습니다.", Toast.LENGTH_SHORT);
        }

        resultLatLon.setLat(list.get(0).getLatitude());
        resultLatLon.setLon(list.get(0).getLongitude());

        return resultLatLon;
    }

    public Address getAddressWithLatLon(String lat, String lon,Context context) {
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);

        Geocoder geocoder = new Geocoder(context);
        List<Address> gList = null;
        try {
            gList = geocoder.getFromLocation(latitude, longitude, 8);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG", "setMaskLocation() - 서버에서 주소변환시 에러발생");
            // Fragment1 으로 강제이동 시키기
        }
        if (gList != null) {
            if (gList.size() == 0) {
                Toast.makeText(context, "주소 정보에서 검색된 주소정보가 없습니다. ", Toast.LENGTH_SHORT).show();

            } else {

                Address address = gList.get(0);
                String sido = address.getAdminArea();       // 경기도
                String gugun = address.getSubLocality();    // 성남시
                String emd = address.getThoroughfare();     //금곡동
                Log.i(TAG, address.toString());
                Log.i(TAG, sido + gugun);

                return address;

            }
        }
        return null;
    }

        public Map<String, Object> getGridxy ( double v1, double v2){

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
            map.put("lng", v2);
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
