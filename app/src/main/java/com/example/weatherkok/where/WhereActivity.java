package com.example.weatherkok.where;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.weatherkok.R;
import com.example.weatherkok.main.MainActivity;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.where.interfaces.WhereContract;
import com.example.weatherkok.where.models.Record;
import com.example.weatherkok.where.models.Result;
import com.example.weatherkok.where.models.search.SearchedIndexOf;
import com.example.weatherkok.where.utils.GpsTracker;
import com.example.weatherkok.where.utils.WhereRecyclerViewAdapter;
import com.example.weatherkok.where.utils.WhereSearchedRvAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WhereActivity extends BaseActivity implements WhereContract.ActivityView {

    RecyclerView mSearchedRecyclerView;
    RecyclerView mWhereRecyclerView;
    WhereRecyclerViewAdapter mWhereRecyclerViewAdapter;
    WhereSearchedRvAdapter mWhereSearchedRvAdapter;
    ImageView mIvGpsPos;
    ImageView mIvBackArrow;
    Context mContext;
    GpsTracker mGpsTracker;
    EditText mEtSearch;
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
    ArrayList<String> splited = new ArrayList<>();
    Address mGpsAddress;
    String mTarget;
    ScrollView mSvSearched;
    ScrollView mSvContents;

    /**
     * WhereActivity는 매 화면에서 API 사용시 기기에 부담이 크고, 데이터가 잘 변치 않기에, Preference에 시,도/시군구/읍면동으로 나눠 리스트를 저장하여 쓰게 만들것이기에
     * 당장 사용하지 않는 함수라도 함부로 수정 및 삭제를 주의 바랍니다.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where);
        mContext = getBaseContext();

        //initialize the preference
        initializeThePreference();

        //initialize the view and setting on click listener
        this.initView();
        this.settingOnClickListener();

        //Api 연동은 공휴일, 주소는 30일마다 작동되도록 설계 생각해볼것. + splash에서 DB구축 완료하는게 좋아보임
        //startingApiService();
        NoApiStarting();

        //검색창 TextWatcher
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchWord = mEtSearch.getText().toString();

                if(!searchWord.equals("")){
                    mSvContents.setVisibility(View.GONE);
                    mSvSearched.setVisibility(View.VISIBLE);
                    ArrayList<SearchedIndexOf> temp = tryGetSearch(searchWord);
                    setListToSearchedAdapter(temp, searchWord);
                }
                else if(searchWord.equals("")){
                    mSvContents.setVisibility(View.VISIBLE);
                    mSvSearched.setVisibility(View.GONE);
                }
            }
        });

        mIvBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        lastFunction();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
    }

    private void NoApiStarting() {

        //리스트 데이터 초기화
        mSidoList.clear();
        //시도 리스트 가져오기
        mSidoList = getListFromPref("sido");

        //시도 어댑터 연결과 클릭시 시군구 리스트 가져오기
        noApiSetSidoAdapter("sigg");

    }

    //하나의 함수로 시도하였으나 안되는 것을 확인 - 어댑터의 객체가 살아있기 때문으로 예상
    private void noApiSetSidoAdapter(String key){

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(mSidoList);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

        mSidoList = getListFromPref(key);

        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.i(TAG,"click lisener on activity");

                    noApiSetSiggAdapter("emd");

            }
        });
    }

    private void noApiSetSiggAdapter(String key){

        cutStringForSgg();

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(mSidoList);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

        mSidoList = getListFromPref(key);

        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.i(TAG,"click lisener on activity");

                noApiSetEmdAdapter("emd");

            }
        });
    }

    private void noApiSetEmdAdapter(String key){

        cutStringForEmd();

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(mSidoList);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.i(TAG,"click lisener on activity");

                lastFunction();

            }
        });
    }

    private void cutStringForSgg(){
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        String temp = pref.getString("where","");

        String[] splited = new String[2];
        ArrayList<String> temp2 = new ArrayList<>();
        for(int i=0;i<mSidoList.size();i++){

            if((mSidoList.get(i)).startsWith(temp)){
                splited = mSidoList.get(i).split(" ");
                temp2.add(splited[1]);
            }

        }
        mSidoList=temp2;
    }

    private void cutStringForEmd(){
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        String temp = pref.getString("where","");

        Log.i("emd",temp);

        //읍면동만 따로 정리하여 저장
        //String[] splited = new String[3];
        ArrayList<String> temp2 = new ArrayList<>();
        for(int i=0;i<mSidoList.size();i++){

            if((mSidoList.get(i)).startsWith(temp)){
                String[] splited = mSidoList.get(i).split(" ");

                if(splited.length==3) {
                    temp2.add(splited[2]);
                } else if(splited.length==4) {
                    temp2.add(splited[2] + " " + splited[3]);
                } else if(splited.length==5){
                    temp2.add(splited[2] + " " + splited[3] + " " + splited[4]);
                } else {
                    Log.i("length=2",splited[0] + splited[1]);
                }
            }
        }
        mSidoList=temp2;
    }


    private ArrayList<String> getListFromPref(String key) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        //Preference에서 날씨 정보 객체 불러오기
        Gson gson = new GsonBuilder().create();
        String loaded = pref.getString(key,"");

        ArrayList<String> loadedFromSP = gson.fromJson(loaded,new TypeToken<ArrayList<String>>(){}.getType());

        return loadedFromSP;

    }

    private void startingApiService() {

        //API service
        WhereService whereService = new WhereService(this);
        whereService.getSidoList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data);

    }

    private ArrayList<SearchedIndexOf> tryGetSearch(String searchWord) {

            ArrayList<String> temp = getFullAddressFromPreference();
            ArrayList<SearchedIndexOf> searchedList = new ArrayList<>();
            SearchedIndexOf tempObj = new SearchedIndexOf();
            for(int i =0;i<temp.size();i++){
                int index = (temp.get(i)).indexOf(searchWord);

                if(index!=(-1)){
                    tempObj.setStartIndex(index);
                    tempObj.setAddress(temp.get(i));
                    searchedList.add(tempObj);
                }
            }

            return searchedList;
    }

    private void setListToSearchedAdapter(ArrayList<SearchedIndexOf> temp,String searchWord){

        mWhereSearchedRvAdapter = new WhereSearchedRvAdapter(temp, searchWord);
        mSearchedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchedRecyclerView.setAdapter(mWhereSearchedRvAdapter);

        mWhereSearchedRvAdapter.setOnItemClickListener(new WhereSearchedRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                lastFunction();

            }
        });
    }

    //마지막 읍면동 선택시 메인액티비티로 전환
    private void setLastListToAdapter(ArrayList<String> temp){

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(temp);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

        mWhereSearchedRvAdapter.setOnItemClickListener(new WhereSearchedRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                lastFunction();

            }
        });
    }

    public void initView(){

        mWhereRecyclerView = findViewById(R.id.rv_where_contents);
        mSearchedRecyclerView = findViewById(R.id.rv_where_searched);
        mIvGpsPos = findViewById(R.id.iv_gps_position);
        mEtSearch = findViewById(R.id.et_where_search);
        mSvContents = findViewById(R.id.sv_where_contents);
        mSvSearched = findViewById(R.id.sv_where_searched);
        mSvSearched.setVisibility(View.GONE);
        mIvBackArrow = findViewById(R.id.iv_back_arrow);

    }

    public void settingOnClickListener(){
        //gps tracker
        mIvGpsPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재위치 좌표 받아오기기
                double latitude  = 0;
                double longitude = 0;
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
                        String sido = address.getAdminArea();       // 경기도
                        String gugun = address.getSubLocality();    // 성남시
                        String emd = address.getThoroughfare();     //금곡동
                        Log.i(TAG, address.toString());
                        Log.i(TAG,sido + gugun);
                        mGpsAddress = address;
                        String result = arrangeGpsResults(address);
                        mEtSearch.setText(result);
                    }
                }
            }
        });

    }

    private String arrangeGpsResults(Address address){

        String gps="";

        if(address.getThoroughfare()!=null) {
            gps = address.getAdminArea() + " " + address.getLocality() + " " + address.getSubLocality() + " " + address.getThoroughfare();
        }else if(address.getThoroughfare()==null||address.getThoroughfare().isEmpty()){
            gps = address.getAdminArea() + " " + address.getLocality() + " " + address.getSubLocality();
        }else {
            gps = address.getAddressLine(0);
        }

        return gps;
    }

    private void setGpsResults(String gps){

        mSvContents.setVisibility(View.GONE);
        mSvSearched.setVisibility(View.VISIBLE);
        ArrayList<SearchedIndexOf> temp = tryGetSearch(gps);
        setListToSearchedAdapter(temp, gps);

    }



    private String gpsPositionData(Address address){

        String gpsPosition=address.getAddressLine(0);

        //Address[addressLines=[0:"대한민국 경기도 성남시 분당구 금곡동 165"],feature=１６５,admin=경기도,sub-admin=null,locality=성남시,thoroughfare=금곡동,
        // postalCode=463-480,countryCode=KR,countryName=대한민국,hasLatitude=true,
        // latitude=37.3507579,hasLongitude=true,longitude=127.10712969999999,phone=null,url=null,extras=null]

        return gpsPosition;
    }

    private void initializeThePreference(){
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        //초기화해주고 시작
        editor.putString("where","");
        editor.putString("temp_where","");
        editor.commit();
    }


    @Override
    public void validateSuccess(boolean isSuccess, Record record, Result result) {

        Log.d("isSuccess : ", String.valueOf(isSuccess));
        Log.d("Record : ", String.valueOf(record.getTotal()));
        Log.d("result : ", result.getClass().getName());

        //mResult = result;

            fromSidoToList(result);

    }

    @Override
    public void validateSggSuccess(boolean isSuccess, Record record, Result result) {
        fromSiggToList(result);

    }

    @Override
    public void validateEmdSuccess(boolean isSuccess, Record record, Result result) {
        fromEmdToList(result);
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

        //30일에 한번 함수가 돌아가도록 설정할 것
        //setSidoInPreference(mSidoList);

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

    private ArrayList<String> getFullAddressFromPreference(){

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        //Preference에서 날씨 정보 객체 불러오기
        Gson gson = new GsonBuilder().create();
        String loaded = pref.getString("koreaAddress","");

        ArrayList<String> loadedFromSP = gson.fromJson(loaded,new TypeToken<ArrayList<String>>(){}.getType());

        return loadedFromSP;
    }

    private void setSidoInPreference(ArrayList<String> sidoList) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //Preference에 날씨 정보 객체 저장하기
        Gson gson = new GsonBuilder().create();
        //JSON으로 변환
        String jsonString = gson.toJson(sidoList);
        Log.i("jsonString : ",jsonString);
        editor.putString("koreaAddress",jsonString);
        editor.commit();

    }

    private void setListInPreference(ArrayList<String> sidoList, String key) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //Preference에 날씨 정보 객체 저장하기
        Gson gson = new GsonBuilder().create();
        //JSON으로 변환
        String jsonString = gson.toJson(sidoList);
        Log.i("jsonString : ",jsonString);
        editor.putString(key,jsonString);
        editor.commit();

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

        //30일에 한번 함수가 돌아가도록 설정할것
        //시,도 리스트랑 시군구리스트 병합 후 저장.
//        ArrayList<String> totList = getFullAddressFromPreference();
//        totList.addAll(mSidoList);
//        setSidoInPreference(totList);

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        String temp = pref.getString("where","");

        String[] splited = new String[2];
        ArrayList<String> temp2 = new ArrayList<>();
        for(int i=0;i<mSidoList.size();i++){

            if((mSidoList.get(i)).startsWith(temp)){
                splited = mSidoList.get(i).split(" ");
                temp2.add(splited[1]);
            }

        }

        for(int i=0;i<temp2.size();i++){
            Log.i(TAG, temp2.get(i));
        }

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(temp2);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

        WhereService whereService = new WhereService(this);
        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Log.i(TAG,"click lisener on activity");
                //읍면동은 데이터가 많아 6번을 나눠 받아야 한다.
                whereService.getEmdList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data3);
                whereService.getEmdList(key, domain, request, format, size, 2, geometry, attribute, crs, geomfilter, data3);
                whereService.getEmdList(key, domain, request, format, size, 3, geometry, attribute, crs, geomfilter, data3);
                whereService.getEmdList(key, domain, request, format, size, 4, geometry, attribute, crs, geomfilter, data3);
                whereService.getEmdList(key, domain, request, format, size, 5, geometry, attribute, crs, geomfilter, data3);
                whereService.getEmdList(key, domain, request, format, size, 6, geometry, attribute, crs, geomfilter, data3);

            }
        });
    }

    private void fromEmdToList(Result result) {
        //리스트 데이터 초기화

        for(int i =0;i<result.getFeatureCollection().getFeatures().size();i++){
            mSidoList.add(result.getFeatureCollection().getFeatures().get(i).getProperties().getFull_nm());
        }
        //리스트에 읍,면,동 데이터가 들어와있음
        for(int i=0;i<mSidoList.size();i++){
            Log.i(TAG, mSidoList.get(i));
        }

        //30일에 한번 함수가 돌아가도록 설정
        //토탈리스트. 저장.
//        ArrayList<String> totList = getFullAddressFromPreference();
//        totList.addAll(mSidoList);
//        setSidoInPreference(totList);

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        String temp = pref.getString("where","");

        Log.i("emd",temp);

        //읍면동만 따로 정리하여 저장
        String[] splited = new String[3];
        ArrayList<String> temp2 = new ArrayList<>();
        for(int i=0;i<mSidoList.size();i++){

            if((mSidoList.get(i)).startsWith(temp)){
                splited = mSidoList.get(i).split(" ");

                if(splited.length==3) {
                    temp2.add(splited[2]);
                } else {
                    Log.i("length=2",splited[0]+splited[1]);
                }
            }

        }

        for(int i=0;i<temp2.size();i++){
            Log.i("emd", temp2.get(i));
        }

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(temp2);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                lastFunction();
            }
        });

    }

    public void lastFunction(){

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        //저장된 데이터 처리 후 화면 이동
        String result = pref.getString("where","");
        Log.i(TAG,"result!!" + result);
        editor.putString("temp_where", result);
        editor.putString("where","");
        editor.commit();
        Log.i(TAG,"click lisener on activity");
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("fromApp",true);
        finish();
        startActivity(intent);

    }


}