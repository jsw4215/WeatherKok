package com.devpilot.weatherkok.when;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.devpilot.weatherkok.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class WhenActivity extends AppCompatActivity {

    MaterialCalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_when);

        mCalendarView = findViewById(R.id.cv_when_cal);



    }
}