package com.example.weatherkok.when.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.weatherkok.R;
import com.example.weatherkok.when.CalendarActivity;
import com.example.weatherkok.when.interfaces.DateSelectorListener;

import org.w3c.dom.Text;

import java.util.Calendar;

public class DialogDateSelector extends Dialog {
    private static final String TAG = DialogDateSelector.class.getSimpleName();
    TextView mTvDialogPositive;
    TextView mTvDialogNegative;
    NumberPicker mNpYear;
    NumberPicker mNpMonth;
    NumberPicker mNpDate;
    int year;
    int month;
    int date;
    Context mContext;
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";

    public DialogDateSelector(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_selector);

        mTvDialogPositive = findViewById(R.id.tv_dialog_positive);
        mTvDialogNegative = findViewById(R.id.tv_dialog_negative);
        mNpYear = findViewById(R.id.np_dialog_year);
        mNpMonth = findViewById(R.id.np_dialog_month);
        mNpDate = findViewById(R.id.np_dialog_day);

        setNpYear();

        setNpMonth();

        Calendar calendar = Calendar.getInstance();

        mNpYear.setDescendantFocusability(
                NumberPicker.FOCUS_BLOCK_DESCENDANTS
        );

        mNpMonth.setDescendantFocusability(
                NumberPicker.FOCUS_BLOCK_DESCENDANTS
        );

        mNpDate.setDescendantFocusability(
                NumberPicker.FOCUS_BLOCK_DESCENDANTS
        );

        mTvDialogPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                year = mNpYear.getValue();
                month = mNpMonth.getValue();
                date = mNpMonth.getValue();

                String monthStr = null;

                if(month<10){
                    monthStr = "0" + String.valueOf(month);
                }

                Intent intent = new Intent(mContext, CalendarActivity.class);
                intent.putExtra("year",String.valueOf(year));
                intent.putExtra("month",monthStr);
                mContext.startActivity(intent);
            }
        });

        mTvDialogNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mNpYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if (!(newVal==0)) {

                    year = newVal;

                    calendar.set(Calendar.YEAR,year);
                }
            }
        });

        mNpMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Calendar calendar = Calendar.getInstance();


                if (!(newVal==0)) {
                    calendar.set(Calendar.MONTH,newVal-1);
                    //실제 월 -1
                    int temp = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                    mNpDate.setWrapSelectorWheel(false);
                    mNpDate.setMinValue(1);
                    //월별 일자 가져와서 정하기
                    mNpDate.setMaxValue(temp);
                    mNpDate.setEnabled(true);
                }
            }
        });

    }

    private void setNpYear() {
        mNpYear.setWrapSelectorWheel(false);
        mNpYear.setMinValue(2010);

        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_KEY,Context.MODE_PRIVATE);

        String maxYear = sp.getString("maxYear","");

        mNpYear.setMaxValue(Integer.parseInt(maxYear));
    }

    private void setNpMonth() {
        mNpMonth.setWrapSelectorWheel(false);

        mNpMonth.setMinValue(0);
        mNpMonth.setMaxValue(12);
        mNpMonth.setDisplayedValues( new String[] { "", "1", "2","3","4","5","6","7","8","9","10","11","12" } );

        mNpDate.setEnabled(false);
    }

}
