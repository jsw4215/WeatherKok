package com.example.weatherkok.when;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.CalendarView;

import com.example.weatherkok.R;
import com.google.android.material.datepicker.MaterialCalendar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.time.DayOfWeek;

public class WhenActivity extends AppCompatActivity {

    MaterialCalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_when);

        mCalendarView = findViewById(R.id.cv_when_cal);



    }
}