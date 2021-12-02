package com.devpilot.weatherkok.datalist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.devpilot.weatherkok.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DataListActivity extends AppCompatActivity {

    //Preference를 불러오기 위한 키
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);
        Context context = getApplicationContext();

        ArrayList<String> dataList = new ArrayList<>();

        for(int i = 0;i<20;i++){
            dataList.add("data");
        }

        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
        SharedPreferences.Editor editor = pref.edit();

        //리스트 데이터를 저장한다.
        setStringArrayPref(context, PREFERENCE_KEY, dataList);

        dataList.clear();

        Log.i(TAG, "cleared?");
        for(int i=0;i<dataList.size();i++){
            Log.i(TAG, dataList.get(i));
        }

        //리스트 데이터를 불러온다.
        dataList = getStringArrayPref(context, PREFERENCE_KEY);

        Log.i(TAG, "re-loaded?");
        for(int i=0;i<dataList.size();i++){
            Log.i(TAG, dataList.get(i));
        }

    }

    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

}