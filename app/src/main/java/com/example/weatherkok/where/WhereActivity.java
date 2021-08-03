package com.example.weatherkok.where;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.weatherkok.R;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.where.interfaces.WhereContract;
import com.example.weatherkok.where.models.Record;
import com.example.weatherkok.where.models.Result;
import com.example.weatherkok.where.utils.GpsTracker;
import com.example.weatherkok.where.utils.WhereRecyclerViewAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WhereActivity extends BaseActivity implements WhereContract.ActivityView {

    RecyclerView mWhereRecyclerView;
    WhereRecyclerViewAdapter mWhereRecyclerViewAdapter;
    ImageView mIvGpsPos;
    Context mContext;
    GpsTracker mGpsTracker;
    String TAG = "WhereActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where);
        mContext = getBaseContext();
        mWhereRecyclerView = findViewById(R.id.rv_where_contents);

        String dummyWhereString = "dummy where";

        ArrayList<String> dummyWhere = new ArrayList<>();

        for(int i=0;i<100;i++){
            dummyWhere.add(dummyWhereString);

        }

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(dummyWhere);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

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

        WhereService whereService = new WhereService(this);
        whereService.getSidoList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data);

        whereService.getSigunguList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data2);

        whereService.getSigunguList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data3);


    }

    @Override
    public void validateSuccess(boolean isSuccess, Record record, Result result) {

        Log.d("isSuccess : ", String.valueOf(isSuccess));
        Log.d("Record : ", String.valueOf(record.getTotal()));
        Log.d("result : ", result.getClass().getName());
    }

    @Override
    public void validateFailure(String message) {
        Log.d("Failure : ", message);
    }
}