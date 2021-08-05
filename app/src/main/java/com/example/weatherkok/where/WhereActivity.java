package com.example.weatherkok.where;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weatherkok.R;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.where.interfaces.WhereContract;
import com.example.weatherkok.where.models.Record;
import com.example.weatherkok.where.models.Result;
import com.example.weatherkok.where.utils.GpsTracker;
import com.example.weatherkok.where.utils.WhereRecyclerViewAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WhereActivity extends BaseActivity implements WhereContract.ActivityView {

    RecyclerView mWhereRecyclerView;
    WhereRecyclerViewAdapter mWhereRecyclerViewAdapter;
    ImageView mIvGpsPos;
    Context mContext = getBaseContext();
    GpsTracker mGpsTracker;
    String TAG = "WhereActivity";
    Result mResult;
    ArrayList<String> mSidoList = new ArrayList<>();
    //Preference를 불러오기 위한 키
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    String key = "8FFE0527-8171-32CF-B797-F3A1D0978E0F";
    String domain = "http://localhost:8080";
    String request = "getfeature";
    String format = "json";
    int size = 1000;
    int page = 1;
    boolean geometry = false;
    boolean attribute = true;
    String crs = "EPSG:3857";
    String geomfilter = "BOX(13663271.680031825,3894007.9689600193,14817776.555251127,4688953.0631258525)";
    String data = "LT_C_ADSIDO_INFO";
    String data2 = "LT_C_ADSIGG_INFO";
    String data3 = "LT_C_ADEMD_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where);
        mWhereRecyclerView = findViewById(R.id.rv_where_contents);

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        String dummyWhereString = "dummy where";

        ArrayList<String> dummyWhere = new ArrayList<>();

        for(int i=0;i<100;i++){
            dummyWhere.add(dummyWhereString);
        }



        WhereService whereService = new WhereService(this);
        whereService.getSidoList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data);

        ArrayList<String> sidoList = mSidoList;
        Log.i("mainList size : ", String.valueOf(sidoList.size()));
        for(int i =0;i<sidoList.size();i++) {

            Log.i("mainList", sidoList.get(i));
        }


        mIvGpsPos = findViewById(R.id.iv_gps_position);

        mIvGpsPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재위치 좌표 받아오기기
                double latitude  = 35.8565254;
                double longitude = 128.6090332;
                mGpsTracker = new GpsTracker(mContext);
                Location currentLocation = mGpsTracker.getLocation();
                latitude = currentLocation.getLatitude();
                longitude = currentLocation.getLongitude();

                Geocoder geocoder = new Geocoder(getBaseContext());
                List<Address> gList = null;
                try {
                    gList = geocoder.getFromLocation(latitude,longitude,8);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("TAG", "setMaskLocation() - 서버에서 주소변환시 에러발생");
                    // Fragment1 으로 강제이동 시키기
                }
                if (gList != null) {
                    if (gList.size() == 0) {
                        Toast.makeText(getBaseContext(), " 현재위치에서 검색된 주소정보가 없습니다. ", Toast.LENGTH_SHORT).show();

                    } else {

                        Address address = gList.get(0);
                        String sido = address.getAdminArea();       // 대구광역시
                        String gugun = address.getSubLocality();    // 수성구

                        Log.i(TAG, address.toString());

                        Log.i(TAG,sido + gugun);
                    }
                }
           }
        });

    }




    @Override
    public void validateSuccess(boolean isSuccess, Record record, Result result) {

        Log.d("isSuccess : ", String.valueOf(isSuccess));
        Log.d("Record : ", String.valueOf(record.getTotal()));
        Log.d("result : ", result.getClass().getName());

        //mResult = result;

        if(result.getFeatureCollection().getFeatures().get(0).getProperties().getFull_nm()==null) {
            fromSidoToList(result);
        } else if(result.getFeatureCollection().getFeatures().get(0).getProperties().getSig_kor_nm()!=null) {
            fromSiggToList(result);
        } else {
            fromEmdToList(result);
        }

    }

    @Override
    public void validateFailure(String message) {
        Log.d("Failure : ", message);
    }

    public void fromSidoToList(Result result) {
        //리스트 데이터 초기화
        mSidoList.clear();

        for(int i=0;i<result.getFeatureCollection().getFeatures().size();i++) {
            mSidoList.add(result.getFeatureCollection().getFeatures().get(i).getProperties().getCtp_kor_nm());
        }
        //리스트에 시,도 데이터가 들어와있음
        for(int i=0;i<mSidoList.size();i++){
            Log.i(TAG, mSidoList.get(i));
        }

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(mSidoList);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

        WhereService whereService = new WhereService(this);
        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Log.i(TAG,"click lisener on activity");
                whereService.getSigunguList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data2);

            }
        });

    }

    public void fromSiggToList(Result result) {
        //리스트 데이터 초기화
        mSidoList.clear();

        for(int i =0;i<result.getFeatureCollection().getFeatures().size();i++){
            mSidoList.add(result.getFeatureCollection().getFeatures().get(i).getProperties().getFull_nm());
        }
        //리스트에 시,군,구 데이터 혹은 읍,면,동 데이터가 들어와있음
        for(int i=0;i<mSidoList.size();i++){
            Log.i(TAG, mSidoList.get(i));
        }

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(mSidoList);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

        WhereService whereService = new WhereService(this);
        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Log.i(TAG,"click lisener on activity");
                whereService.getEmdList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data3);

            }
        });
    }

    private void fromEmdToList(Result result) {

        //리스트 데이터 초기화
        mSidoList.clear();

        for(int i =0;i<result.getFeatureCollection().getFeatures().size();i++){
            mSidoList.add(result.getFeatureCollection().getFeatures().get(i).getProperties().getFull_nm());
        }
        //리스트에 읍,면,동 데이터가 들어와있음
        for(int i=0;i<mSidoList.size();i++){
            Log.i(TAG, mSidoList.get(i));
        }


        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(mSidoList);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

    }


}