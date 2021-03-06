package com.devpilot.weatherkok.where;

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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.intro.IntroActivity;
import com.devpilot.weatherkok.intro.eventBus.eventBus;
import com.devpilot.weatherkok.main.MainActivity;
import com.devpilot.weatherkok.src.BaseActivity;
import com.devpilot.weatherkok.when.models.Schedule;
import com.devpilot.weatherkok.when.models.ScheduleList;
import com.devpilot.weatherkok.where.interfaces.WhereContract;
import com.devpilot.weatherkok.where.models.Record;
import com.devpilot.weatherkok.where.models.Result;
import com.devpilot.weatherkok.where.models.search.SearchedIndexOf;
import com.devpilot.weatherkok.where.utils.GpsTracker;
import com.devpilot.weatherkok.where.utils.WhereRecyclerViewAdapter;
import com.devpilot.weatherkok.where.utils.WhereSearchedRvAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    //Preference??? ???????????? ?????? ???
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    String key;
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
    ArrayList<String> temp;
    boolean firstTimeCheck=false;
    int receiveCnt=0;
    boolean stop=false;

    /**
     * WhereActivity??? ??? ???????????? API ????????? ????????? ????????? ??????, ???????????? ??? ?????? ?????????, Preference??? ???,???/?????????/??????????????? ?????? ???????????? ???????????? ?????? ??????????????????
     * ?????? ???????????? ?????? ???????????? ????????? ?????? ??? ????????? ?????? ????????????.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where);
        mContext = getBaseContext();
        key = getString(R.string.vworld_key);
        //initialize the preference
        initializeThePreference();

        //initialize the view and setting on click listener
        this.initView();
        this.settingOnClickListener();

        //Api ????????? ?????????, ????????? 30????????? ??????????????? ?????? ???????????????. + splash?????? DB?????? ??????????????? ????????????
        //Preference??? ?????? ????????? ????????? ????????? ???????????? ??????. Noapi ????????????, startingApi ???????????? ???????????????, starting????????????, Noapi????????????
        //startingApiService();
        NoApiStarting();

        //????????? TextWatcher
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

    public WhereActivity() {
    }

    public WhereActivity(Context mContext) {
        this.mContext = mContext;
        key = mContext.getString(R.string.vworld_key);
    }

    @Override
    public void onBackPressed() {
        stop=true;
        lastFunction();
        finish();
    }

    private void NoApiStarting() {

        //????????? ????????? ?????????
        mSidoList.clear();
        //?????? ????????? ????????????
        mSidoList = getListFromPref("sido");

        //?????? ????????? ????????? ????????? ????????? ????????? ????????????
        noApiSetSidoAdapter("sigg");

    }

    //????????? ????????? ?????????????????? ????????? ?????? ?????? - ???????????? ????????? ???????????? ???????????? ??????
    private void noApiSetSidoAdapter(String key){

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(mSidoList);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

        mSidoList = getListFromPref(key);

        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, boolean s) {
                Log.i(TAG,"click lisener on activity");
                //???????????? ??????
                if(s){
                        mSidoList = getListFromPref("emd");
                        noApiSetEmdAdapter("emd");
                    }else {

                    noApiSetSiggAdapter("emd");
                }
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
            public void onItemClick(View v, int position, boolean s) {
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
        mWhereSearchedRvAdapter.notifyDataSetChanged();

        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, boolean s) {
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

        //???????????? ?????? ???????????? ??????
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
                } else if(splited.length==2) {
                    temp2.add(splited[1]);
                } else {
                    Log.i("length=2",splited[0] + splited[1]);
                }
            }
        }
        mSidoList=temp2;
    }


    private ArrayList<String> getListFromPref(String key) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        //Preference?????? ?????? ?????? ?????? ????????????
        Gson gson = new GsonBuilder().create();
        String loaded = pref.getString(key,"");

        ArrayList<String> loadedFromSP = gson.fromJson(loaded,new TypeToken<ArrayList<String>>(){}.getType());

        return loadedFromSP;

    }

    public void setFirstTimeCheck(boolean check){
        this.firstTimeCheck=check;
    }

    private void startingApiService() {

        //API service
        WhereService whereService = new WhereService(this);
        whereService.getSidoList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data);

    }

    public void firstConnectionWhereApi(){

        WhereService whereService = new WhereService(this);
        whereService.getSidoList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data);
    }

    private ArrayList<SearchedIndexOf> tryGetSearch(String searchWord) {

            ArrayList<SearchedIndexOf> searchedList = new ArrayList<>();
            SearchedIndexOf tempObj = new SearchedIndexOf();
            for(int i =0;i<temp.size();i++){
                int index = (temp.get(i)).indexOf(searchWord);

                if(index!=(-1)){
                    tempObj.setStartIndex(index);
                    tempObj.setAddress(temp.get(i));
                    searchedList.add(tempObj);
                    tempObj = new SearchedIndexOf();
                }
            }

            return searchedList;
    }

    private void setListToSearchedAdapter(ArrayList<SearchedIndexOf> temp,String searchWord){

        mWhereSearchedRvAdapter.initial(temp, searchWord);
        mSearchedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchedRecyclerView.setAdapter(mWhereSearchedRvAdapter);

        mWhereSearchedRvAdapter.setOnItemClickListener(new WhereSearchedRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                lastFunction();

            }
        });
    }

    //????????? ????????? ????????? ????????????????????? ??????
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

        mWhereSearchedRvAdapter = new WhereSearchedRvAdapter();
    }

    public void settingOnClickListener(){
        //gps tracker
        mIvGpsPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???????????? ?????? ???????????????
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
                    Log.e("TAG", "setMaskLocation() - ???????????? ??????????????? ????????????");
                    // Fragment1 ?????? ???????????? ?????????
                }
                if (gList != null) {
                    if (gList.size() == 0) {
                        Toast.makeText(getBaseContext(), " ?????????????????? ????????? ??????????????? ????????????. ", Toast.LENGTH_SHORT).show();

                    } else {

                        Address address = gList.get(0);
                        String sido = address.getAdminArea();       // ?????????
                        String gugun = address.getSubLocality();    // ?????????
                        String emd = address.getThoroughfare();     //?????????
                        Log.i(TAG, address.toString());
                        Log.i(TAG,sido + gugun);
                        mGpsAddress = address;
                        String result = arrangeGpsResults(address);

                        mEtSearch.setText(result);

                        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("where","");
                        editor.apply();
                    }
                }
            }
        });

    }

    private String arrangeGpsResults(Address address){

        String gps="";
        ArrayList<String> temp = new ArrayList<>();

        temp.add(address.getAdminArea());
        temp.add(address.getSubAdminArea());
        temp.add(address.getLocality());
        temp.add(address.getSubLocality());
        temp.add(address.getThoroughfare().substring(0,2));

        temp.removeAll(Arrays.asList("", null));

        for(int i=0;i<temp.size();i++){
            if(!(i==temp.size()-1)){
                gps = gps + temp.get(i) + " ";
            }else {
                gps = gps + temp.get(i);
            }
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

        //Address[addressLines=[0:"???????????? ????????? ????????? ????????? ????????? 165"],feature=?????????,admin=?????????,sub-admin=null,locality=?????????,thoroughfare=?????????,
        // postalCode=463-480,countryCode=KR,countryName=????????????,hasLatitude=true,
        // latitude=37.3507579,hasLongitude=true,longitude=127.10712969999999,phone=null,url=null,extras=null]

        return gpsPosition;
    }

    private void initializeThePreference(){
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        //?????????????????? ??????
        //????????? ????????? ?????? ?????????
        temp = getFullAddressFromPreference();
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

        if(firstTimeCheck){
            firstSavingSido(result);
        }else{
            fromSidoToList(result);
        }
    }

    private void firstSavingSido(Result result) {

        //????????? ????????? ?????????
        mSidoList.clear();

        for(int i=0;i<result.getFeatureCollection().getFeatures().size();i++) {
            mSidoList.add(result.getFeatureCollection().getFeatures().get(i).getProperties().getCtp_kor_nm());
        }
        //???????????? ??????. ???????????? ?????? ????????? ???????????? ??????.
        resetFullAddress();
        //30?????? ?????? ????????? ??????????????? ????????? ???
        setSidoInPreference(mSidoList);

        WhereService whereService = new WhereService(this);
        whereService.getSigunguList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data2);

    }

    @Override
    public void validateSggSuccess(boolean isSuccess, Record record, Result result) {


        if(firstTimeCheck){
            firstSavingSgg(result);
        }else {
            fromSiggToList(result);
        }
    }

    private void firstSavingSgg(Result result) {

        //????????? ????????? ?????????
        mSidoList.clear();

        for(int i =0;i<result.getFeatureCollection().getFeatures().size();i++){
            mSidoList.add(result.getFeatureCollection().getFeatures().get(i).getProperties().getFull_nm());
        }
        //???????????? ???,???,??? ????????? ?????? ???,???,??? ???????????? ???????????????
        for(int i=0;i<mSidoList.size();i++){
            Log.i(TAG, mSidoList.get(i));
        }

        //30?????? ?????? ????????? ??????????????? ????????????
        //???,??? ???????????? ?????????????????? ?????? ??? ??????.
        ArrayList<String> totList = getFullAddressFromPreference();
        totList.addAll(mSidoList);
        setSggInPreference(totList);

        mSidoList.clear();

        WhereService whereService = new WhereService(this);
        whereService.getEmdList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data3);
        whereService.getEmdList(key, domain, request, format, size, 2, geometry, attribute, crs, geomfilter, data3);
        whereService.getEmdList(key, domain, request, format, size, 3, geometry, attribute, crs, geomfilter, data3);
        whereService.getEmdList(key, domain, request, format, size, 4, geometry, attribute, crs, geomfilter, data3);
        whereService.getEmdList(key, domain, request, format, size, 5, geometry, attribute, crs, geomfilter, data3);
        whereService.getEmdList(key, domain, request, format, size, 6, geometry, attribute, crs, geomfilter, data3);

    }

    @Override
    public void validateEmdSuccess(boolean isSuccess, Record record, Result result) {
        receiveCnt++;
        if(firstTimeCheck){
            firstSavingEmd(result);
        }else{
            fromEmdToList(result);
        }
    }

    private void firstSavingEmd(Result result) {

        for(int i =0;i<result.getFeatureCollection().getFeatures().size();i++){
            mSidoList.add(result.getFeatureCollection().getFeatures().get(i).getProperties().getFull_nm());
        }

        //???????????? ?????? ???????????? ??????
        String[] splited = new String[3];
        ArrayList<String> temp2 = new ArrayList<>();
        for(int i=0;i<mSidoList.size();i++){
                splited = mSidoList.get(i).split(" ");

                if(splited.length==3) {
                    temp2.add(splited[2]);
                } else if(splited.length==2) {
                    temp2.add(splited[0]+splited[1]);
                } else{
                    temp2.add(splited[0]);
                }

        }

        if(receiveCnt>5) {
            finishEmdIntoSp();
            finishFirstSettingWhere();
            SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            firstTimeCheck = pref.getBoolean("firstStart", false);
        }
    }

    private void finishFirstSettingWhere(){

        EventBus.getDefault().post(new eventBus(false));

    }


    @Override
    public void validateFailure(String message) {
        Log.d("Failure : ", message);
    }

    public void fromSidoToList(Result result) {
        //????????? ????????? ?????????
        mSidoList.clear();

        for(int i=0;i<result.getFeatureCollection().getFeatures().size();i++) {
            mSidoList.add(result.getFeatureCollection().getFeatures().get(i).getProperties().getCtp_kor_nm());
        }
        //???????????? ??????. ???????????? ?????? ????????? ???????????? ??????.
        resetFullAddress();
        //30?????? ?????? ????????? ??????????????? ????????? ???
        setSidoInPreference(mSidoList);

        //???????????? ???,??? ???????????? ???????????????
        for(int i=0;i<mSidoList.size();i++){
            Log.i(TAG, mSidoList.get(i));
        }

        mWhereRecyclerViewAdapter = new WhereRecyclerViewAdapter(mSidoList);
        mWhereRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWhereRecyclerView.setAdapter(mWhereRecyclerViewAdapter);

        WhereService whereService = new WhereService(this);
        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, boolean s) {

                Log.i(TAG,"click lisener on activity");
                whereService.getSigunguList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data2);

            }
        });

    }

    private ArrayList<String> getFullAddressFromPreference(){

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        //Preference?????? ?????? ?????? ?????? ????????????
        Gson gson = new GsonBuilder().create();
        String loaded = pref.getString("koreaAddress","");

        ArrayList<String> loadedFromSP = gson.fromJson(loaded,new TypeToken<ArrayList<String>>(){}.getType());

        return loadedFromSP;
    }

    /**
     * resetFullAddress method
     *
     * Api??? ?????? ????????? ??? fulladdress??? reset????????? ??????????????????. ???????????? ???????????? ????????? ??????
     */
    private void resetFullAddress(){
        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //Preference??? ?????? ?????? ?????? ????????????
        Gson gson = new GsonBuilder().create();
        //JSON?????? ??????
        editor.putString("koreaAddress","");
        editor.commit();
    }

    private void setSidoInPreference(ArrayList<String> sidoList) {

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        mSidoList = removeDuplication(mSidoList);

        //Preference??? ?????? ?????? ?????? ????????????
        Gson gson = new GsonBuilder().create();
        //JSON?????? ??????
        String jsonString = gson.toJson(sidoList);
        String jsonString2 = gson.toJson(mSidoList);
        Log.i("jsonString : ",jsonString);
        editor.putString("koreaAddress",jsonString);
        editor.putString("sido",jsonString2);
        editor.commit();

    }

    private void setSggInPreference(ArrayList<String> sidoList) {

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        mSidoList = removeDuplication(mSidoList);

        //Preference??? ?????? ?????? ?????? ????????????
        Gson gson = new GsonBuilder().create();
        //JSON?????? ??????
        String jsonString = gson.toJson(sidoList);
        String jsonString2 = gson.toJson(mSidoList);
        Log.i("jsonString : ",jsonString);
        editor.putString("koreaAddress",jsonString);
        editor.putString("sigg",jsonString2);
        editor.commit();

    }

    private void setEmdInPreference(ArrayList<String> sidoList) {

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        mSidoList = removeDuplication(mSidoList);

        //Preference??? ?????? ?????? ?????? ????????????
        Gson gson = new GsonBuilder().create();
        //JSON?????? ??????
        String jsonString = gson.toJson(sidoList);
        String jsonString2 = gson.toJson(mSidoList);
        Log.i("jsonString : ",jsonString);
        editor.putString("koreaAddress",jsonString);
        editor.putString("emd",jsonString2);
        editor.commit();

    }

    private void setListInPreference(ArrayList<String> sidoList, String key) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //Preference??? ?????? ?????? ?????? ????????????
        Gson gson = new GsonBuilder().create();
        //JSON?????? ??????
        String jsonString = gson.toJson(sidoList);
        Log.i("jsonString : ",jsonString);
        editor.putString(key,jsonString);
        editor.commit();

    }

    public void fromSiggToList(Result result) {
        //????????? ????????? ?????????
        mSidoList.clear();

        for(int i =0;i<result.getFeatureCollection().getFeatures().size();i++){
            mSidoList.add(result.getFeatureCollection().getFeatures().get(i).getProperties().getFull_nm());
        }
        //???????????? ???,???,??? ????????? ?????? ???,???,??? ???????????? ???????????????
        for(int i=0;i<mSidoList.size();i++){
            Log.i(TAG, mSidoList.get(i));
        }

        //30?????? ?????? ????????? ??????????????? ????????????
        //???,??? ???????????? ?????????????????? ?????? ??? ??????.
        ArrayList<String> totList = getFullAddressFromPreference();
        totList.addAll(mSidoList);
        setSggInPreference(totList);

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
        //???????????? ????????? ???????????? ?????? ????????? ????????? ????????? ??????
        mSidoList.clear();

        WhereService whereService = new WhereService(this);
        mWhereRecyclerViewAdapter.setOnItemClickListener(new WhereRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, boolean s) {

                Log.i(TAG,"click lisener on activity");
                //???????????? ???????????? ?????? 6?????? ?????? ????????? ??????.
                whereService.getEmdList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data3);
                whereService.getEmdList(key, domain, request, format, size, 2, geometry, attribute, crs, geomfilter, data3);
                whereService.getEmdList(key, domain, request, format, size, 3, geometry, attribute, crs, geomfilter, data3);
                whereService.getEmdList(key, domain, request, format, size, 4, geometry, attribute, crs, geomfilter, data3);
                whereService.getEmdList(key, domain, request, format, size, 5, geometry, attribute, crs, geomfilter, data3);
                whereService.getEmdList(key, domain, request, format, size, 6, geometry, attribute, crs, geomfilter, data3);

            }
        });
    }

    /**
     * ????????? ???????????? ???????????? ?????? ????????? 6??? ???????????? ????????? ?????????. ????????? ?????? ????????? ??????????????????.
     *
     * @param result
     */
    private void fromEmdToList(Result result) {
        //????????? ????????? ?????????

        for(int i =0;i<result.getFeatureCollection().getFeatures().size();i++){
            mSidoList.add(result.getFeatureCollection().getFeatures().get(i).getProperties().getFull_nm());
        }
        //???????????? ???,???,??? ???????????? ???????????????
        for(int i=0;i<mSidoList.size();i++){
            Log.i("fromEmdToList", mSidoList.get(i));
        }

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        String temp = pref.getString("where","");

        Log.i("emd",temp);

        //???????????? ?????? ???????????? ??????
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
            public void onItemClick(View v, int position, boolean s) {

                lastFunctionForInit();
            }
        });

    }

    private void finishEmdIntoSp(){

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        //30?????? ?????? ????????? ??????????????? ??????
        //???????????????. ??????.
        ArrayList<String> totList = getFullAddressFromPreference();
        totList.addAll(mSidoList);
        setEmdInPreference(totList);

    }

    public void lastFunctionForInit(){

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        //30?????? ?????? ????????? ??????????????? ??????
        //???????????????. ??????.
        ArrayList<String> totList = getFullAddressFromPreference();
        totList.addAll(mSidoList);
        setEmdInPreference(totList);

        //????????? ????????? ?????? ??? ?????? ??????
        String result = pref.getString("where","");
        Log.i(TAG,"result!!" + result);
        editor.putString("temp_where", result);
        editor.putString("where","");
        editor.commit();
        Log.i(TAG,"click lisener on activity");
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("fromApp",true);
        finish();
        startActivity(intent);

    }

    public void lastFunction(){
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        //????????? ????????? ?????? ??? ?????? ??????
        String result = pref.getString("where", "");

        if(stop){
            editor.putString("temp_where","");
            editor.apply();
            finish();
            return;
        }

        //nowWx?????? ?????? ????????? ???????????? ??????, nowWx?????? ????????? ????????? ???????????? nowWx??? ?????? ??????
        if(TextUtils.isEmpty(getIntent().getStringExtra("from"))) {

            Log.i(TAG, "result!!" + result);
            editor.putString("temp_where", result);
            editor.putString("where", "");
            editor.commit();
            Log.i(TAG, "click lisener on activity");
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("fromApp", true);
            finish();
            startActivity(intent);
        }else if(!(TextUtils.isEmpty(getIntent().getStringExtra("from")))&&getIntent().getStringExtra("from").equals("nowWx")){

            ScheduleList scheduleList = new ScheduleList();

            Schedule schedule = new Schedule();

            schedule.getScheduleData().setPlace(result);
            schedule.setWhere(result);

            scheduleList = getBookMarkFromSp();

            ArrayList<Schedule> temp = new ArrayList<>();

            if(scheduleList==null||scheduleList.getScheduleArrayList()==null||scheduleList.getScheduleArrayList().size()==0){
                scheduleList = new ScheduleList();
                scheduleList.setScheduleArrayList(temp);
            }

            scheduleList.getScheduleArrayList().add(schedule);

            setBookMarkInToSp(scheduleList);

            Intent intent = new Intent(getBaseContext(), IntroActivity.class);
            intent.putExtra("from","goToNow");
            finish();
            startActivity(intent);

        }

    }

    private ScheduleList getBookMarkFromSp() {

        //Preference??? ?????? ?????? ?????? ????????????

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        ScheduleList scheduleList = new ScheduleList();

        //null??? ?????? ????????????.
        String loaded = pref.getString("bookMark", "");

        ArrayList<Schedule> temp = new ArrayList<>();
        if(loaded==null||loaded==""){

            scheduleList.setScheduleArrayList(temp);

        }else {

            scheduleList = gson.fromJson(loaded, ScheduleList.class);
            //Preference??? ????????? ????????? class ????????? ???????????? ??????
        }
        return scheduleList;

    }

    private void setBookMarkInToSp(ScheduleList scheduleList) {

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new GsonBuilder().create();

        //Preference??? ?????? ?????? ????????????
        //JSON?????? ??????
        String jsonString = gson.toJson(scheduleList, ScheduleList.class);
        Log.i("jsonString : ", jsonString);

        //?????????
        //editor.remove(year + month);

        editor.putString("bookMark", jsonString);
        editor.commit();
        //????????????

    }

    private ArrayList<String> removeDuplication(ArrayList<String> list){

        ArrayList<String> arrayList = new ArrayList<>();

        for(String item : list){
            if(!arrayList.contains(item))
                arrayList.add(item);
        }

        return arrayList;

    }


}