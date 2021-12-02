package com.devpilot.weatherkok.when.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.when.CalendarActivity;
import com.devpilot.weatherkok.when.LoadingCalendarActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DialogDateSelectorMonthly  extends Dialog {
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

    public DialogDateSelectorMonthly(@NonNull Context context) {
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
                }else{
                    monthStr = String.valueOf(month);
                }

                Intent intent = new Intent(mContext, LoadingCalendarActivity.class);
                intent.putExtra("year",String.valueOf(year));
                intent.putExtra("month",monthStr);
                mContext.startActivity(intent);
                dismiss();
                ((CalendarActivity)mContext).finish();
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
        int year = Integer.parseInt(getFutureDay("yyyy",0));
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_KEY,Context.MODE_PRIVATE);

        String maxYear = sp.getString("maxYear","");

        mNpYear.setMaxValue(Integer.parseInt(maxYear));
        mNpYear.setValue(year);

    }

    private void setNpMonth() {
        mNpMonth.setWrapSelectorWheel(false);

        mNpMonth.setMinValue(0);
        mNpMonth.setMaxValue(12);

        mNpMonth.setDisplayedValues( new String[] { "", "1", "2","3","4","5","6","7","8","9","10","11","12" } );
        mNpMonth.setValue(Integer.parseInt(getFutureDay("MM",0)));

        mNpDate.setEnabled(false);
    }

    public static String getFutureDay(String pattern, int gap) {
        DateFormat dtf = new SimpleDateFormat(pattern);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, gap);
        return dtf.format(cal.getTime());
    }

    @Override
    public void dismiss() {
        super.dismiss();

        ((CalendarActivity) mContext).mTvDialog.setVisibility(View.VISIBLE);
        ((CalendarActivity) mContext).mIvCalDown.setVisibility(View.GONE);

    }
}