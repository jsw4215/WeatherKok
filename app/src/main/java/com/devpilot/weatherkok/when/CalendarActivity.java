package com.devpilot.weatherkok.when;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.main.MainActivity;
import com.devpilot.weatherkok.src.BaseActivity;
import com.devpilot.weatherkok.when.models.base.BaseDateInfo;
import com.devpilot.weatherkok.when.models.base.BaseDateInfoList;
import com.devpilot.weatherkok.when.utils.CalenderInfoPresenter;
import com.devpilot.weatherkok.when.utils.DialogDateSelectorMonthly;
import com.devpilot.weatherkok.when.utils.RecyclerViewAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalendarActivity extends BaseActivity{
    private static final String TAG = CalendarActivity.class.getSimpleName();
        RecyclerViewAdapter adapter;
        RecyclerView mRecyclerViewNow;
        Context mContext;
        private TextView tvYearMonthTop;
        String year_month;
        TextView mTvSelBtn;
        public ImageView mTvDialog;
        ImageView mIvBefore;
        ImageView mIvAfter;
        private ArrayList<String> dayList;
        String mSelectedDate;
        ArrayList<String> mScheduledDateList;
        String mToday;
        String mDialogDate;
        CheckBox mTvSolar;
        CheckBox mTvLuna;
        TextView mTvYearTerm;
        boolean mLunaChecker=false;
        int mMaxYear;
        public ProgressDialog progressDoalog;
        String month;
        public ImageView mIvCalDown;

        private Calendar mCal;

        String PREFERENCE_KEY = "WeatherKok.SharedPreference";

        public Calendar getmCal() {
            return mCal;
        }

        public void setmCal(Calendar mCal) {
            this.mCal = mCal;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = getBaseContext();

            progressDoalog = new ProgressDialog(this);
            progressDoalog.setMax(100);
            progressDoalog.setMessage("Its loading....");
            progressDoalog.setTitle("Loading Calendar Data..");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            //when ?????????
            initView();

            Intent intent = getIntent();
            //????????? ?????? ??? ?????????, ?????? ???, ????????? ?????? ??? ?????? ?????????
            if(!TextUtils.isEmpty(intent.getStringExtra("year"))
                    &&!TextUtils.isEmpty(intent.getStringExtra("month"))){
                String year = intent.getStringExtra("year");
                month = intent.getStringExtra("month");
                if(month.equals("01")){mIvBefore.setVisibility(View.INVISIBLE);}
                else if(month.equals("12")){mIvAfter.setVisibility(View.INVISIBLE);}
                initForNotCurr(year, month);

            } else {
                editor.putString("temp_when","");
                editor.commit();
                setCurrentYearMonth();
            }

            mCal = Calendar.getInstance();

            //??????????????? ??????

            mTvLuna.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mTvLuna.isChecked()){
                    mTvLuna.setChecked(false);
                    mTvSolar.setChecked(true);
                    //?????? visible invisible
                    if(mLunaChecker==true) {
                        mLunaChecker = false;

                        parsingDate(mSelectedDate);
                    }
                    }
                    else{
                        mTvLuna.setChecked(true);
                        mTvSolar.setChecked(false);

                        if(mLunaChecker==false) {
                            mLunaChecker = true;

                            parsingDate(mSelectedDate);
                        }

                    }
                }
            });

            mTvSolar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mTvSolar.isChecked()){
                        mTvSolar.setChecked(false);
                        mTvLuna.setChecked(true);
                        //?????? visible invisible
                        if(mLunaChecker==false) {
                            mLunaChecker = true;

                            parsingDate(mSelectedDate);
                        }
                    }
                    else{
                        mTvSolar.setChecked(true);
                        mTvLuna.setChecked(false);
                        if(mLunaChecker==true) {
                            mLunaChecker = false;

                            parsingDate(mSelectedDate);
                        }

                    }
                }
            });


            mTvSelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String resultDate = mSelectedDate;

                    Log.i(TAG, "result : " + mSelectedDate);
                    //Preference??? ??????
                    //?????????, ???????????? ?????? ???????????? ?????? ?????? temp??? ????????????
                    editor.putString("temp_when", mSelectedDate);
                    editor.commit();

                    //???????????? Main?????? ??????
                    Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("fromApp",true);
                    finish();
                    startActivity(intent);
                }
            });

            mTvDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTvDialog.setVisibility(View.GONE);
                    mIvCalDown.setVisibility(View.VISIBLE);
                    DialogDateSelectorMonthly dialog = new DialogDateSelectorMonthly(CalendarActivity.this);
                    dialog.show();

                }
            });

            mTvYearTerm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(CalendarActivity.this, YearActivity.class);
                    intent.putExtra("year",mSelectedDate.substring(0,4));
                    finish();
                    startActivity(intent);
                }
            });

            mIvBefore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String yearForAfter = year_month.substring(0,4);
                    String monthForAfter = year_month.substring(5);
                    int a = Integer.parseInt(yearForAfter);
                    int b = Integer.parseInt(monthForAfter)-1;
                    //if(b==0){b=12; a=a-1;}
                    if(b==1){
                        mIvBefore.setVisibility(View.INVISIBLE);
                    }
                        yearForAfter = String.valueOf(a);
                        monthForAfter = String.valueOf(b);
                        if (monthForAfter.length() < 2) {
                            monthForAfter = "0" + monthForAfter;
                        }
                        if (!(a < 2010)) {
                            Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("year", yearForAfter);
                            intent.putExtra("month", monthForAfter);
                            finish();
                            overridePendingTransition(0, 0); //????????? ??????????????? ?????????
                            startActivity(intent);
                            overridePendingTransition(0, 0); //????????? ??????????????? ?????????
                        }
                    }

            });

            mIvAfter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String yearForBefore = year_month.substring(0,4);
                    String monthForBefore = year_month.substring(5);
                    int a = Integer.parseInt(yearForBefore);
                    int b = Integer.parseInt(monthForBefore)+1;
                    //if(b==13){b=1; a=a+1;}
                    if(b==12){
                        mIvAfter.setVisibility(View.INVISIBLE);
                    }
                        yearForBefore = String.valueOf(a);
                        monthForBefore = String.valueOf(b);
                        if (monthForBefore.length() < 2) {
                            monthForBefore = "0" + monthForBefore;
                        }

                        if (!(a > mMaxYear)) {
                            Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("year", yearForBefore);
                            intent.putExtra("month", monthForBefore);
                            finish();
                            overridePendingTransition(0, 0); //????????? ??????????????? ?????????
                            startActivity(intent);
                            overridePendingTransition(0, 0); //????????? ??????????????? ?????????
                        }
                    }

            });

        }

    private void setMaxYear() {

        mMaxYear = Integer.parseInt(mToday.substring(0,4))+10;

        SharedPreferences sp = getSharedPreferences(PREFERENCE_KEY,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("maxYear", String.valueOf(mMaxYear));
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sp = getSharedPreferences(PREFERENCE_KEY,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("temp_when", "");
        editor.apply();
        finish();
        }

    private void initView(){

            setContentView(R.layout.activity_calendar);

            mRecyclerViewNow = findViewById(R.id.rv_calendar);

            mTvDialog = findViewById(R.id.iv_cal_dialog);

            tvYearMonthTop = (TextView) findViewById(R.id.tv_year_month_top);

            mTvSelBtn = findViewById(R.id.tv_calendar_selection_btn);

            mTvLuna = findViewById(R.id.cb_cal_luna);
            mTvLuna.setChecked(false);
            mTvSolar = findViewById(R.id.cb_cal_solar);
            mTvSolar.setChecked(true);

            mTvYearTerm = findViewById(R.id.tv_when_top_bar_year_term);

            mIvBefore = findViewById(R.id.iv_before);

            mIvAfter = findViewById(R.id.iv_after);

            mIvCalDown = findViewById(R.id.iv_cal_dialog_arrow_down);
            mIvCalDown.setVisibility(View.GONE);
        }

        private void initForNotCurr(String year, String month){

            SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            //???????????? ?????? ?????????, ??????????????? ???????????? ?????? ?????? ????????????
            setmSelectedDate(year+ "/" +month+ "/" +"00");
            year_month=year + "/" + month;
            tvYearMonthTop.setText(year_month);
            //???,??? ?????? ????????????
            editor.putString("when",year+"/"+month);
            //temp ?????????
            editor.putString("temp_when","");
            editor.commit();
            //???????????? ??????
            calculateToday();
            //?????? ?????? ??????
            setMaxYear();
            //DB ?????? - ?????? ?????? ????????? ??????
            //loadScheduleData(year,month);
            //??????????????? ?????? ??????
            setUnselectedCalendar(year, month);

        }

        private Date getToday(){
            // ????????? ????????? ?????? ?????????.
            long now = System.currentTimeMillis();
            final Date date = new Date(now);

            return date;
        }

        public void setmSelectedDate(String selectedYMD){

            this.mSelectedDate = selectedYMD;
        }

        public String getmSelectedDate() {
            return mSelectedDate;
        }

        private void setCurrentYearMonth(){

            Date date = getToday();

            //???,???,?????? ?????? ??????
            final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
            final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
            final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

            //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
            String currentYear = curYearFormat.format(date);

            int temp = Integer.parseInt(curMonthFormat.format(date));
            String currentMonth = String.valueOf(temp);
            if(temp<10) {
                currentMonth = "0"+currentMonth;
            }
            String currentDay = curDayFormat.format(date);
            //??????????????? ??????
            //currentMonth="07";
            setmToday(currentYear+currentMonth+currentDay);
            //?????? ?????? ??????
            setMaxYear();
            //??????????????? ??????
            setmSelectedDate(currentYear+"/"+currentMonth+"/"+currentDay);
            month = currentMonth;
            //?????? ?????? ??????????????? ?????????

            Log.i(TAG,"month : " + temp + ", stirng : " + currentMonth);
            //???,?????? ????????? ?????? ?????? ?????? ??????,??????,????????? ????????? preference??? ????????????.
            //DB??????
            //loadScheduleData(currentYear, currentMonth);
            //?????? ?????? ????????? ??????
            setUnselectedCalendar(currentYear, currentMonth);

        }

        private void calculateToday(){
            Date date = getToday();

            //???,???,?????? ?????? ??????
            final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
            final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
            final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

            //?????? ???,?????? ?????? ????????????.(??????:??? ????????? +1???????????????)
            String currentYear = curYearFormat.format(date);

            int temp = Integer.parseInt(curMonthFormat.format(date));
            String currentMonth = String.valueOf(temp);
            if(temp<10) {
                currentMonth = "0"+currentMonth;
            }
            String currentDay = curDayFormat.format(date);
            //??????????????? ??????
            //currentMonth="07";
            setmToday(currentYear+currentMonth+currentDay);
        }

    public void setmToday(String s) {
            this.mToday = s;
        }

        public String getmToday() {
            return mToday;
        }

        public void parsingDate(String selectedYMD){

            setmSelectedDate(selectedYMD);
            year_month = selectedYMD.substring(0,7);

            //String test_date = "2021-08-15 09:15:00";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            try {
                date = dateFormat.parse(selectedYMD);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "parsingDate : " + date);

            divideYmd(selectedYMD);

        }

        public void divideYmd(String selectedYMD){

            String[] temp = new String[3];
            temp = selectedYMD.split("/");

            int year = Integer.parseInt(temp[0]);
            int month = Integer.parseInt(temp[1]);
            int date = Integer.parseInt(temp[2]);

            Log.i(TAG, "divideYMD : " + year + month + date);
            //Preference?????? ????????? ????????? ????????????
            //loadScheduleData(Integer.toString(year), Integer.toString(month));

            setCalendarDate(year, month, date);

        }


        public void setCalendarDate(int year, int month, int date) {

            //set(????????????, ??????(0-11))
            Log.i(TAG, "starting setCalendarDate : " + year + month + date);

            String monthStr = null;
            monthStr= String.valueOf(month);
            if(month<10) {
                monthStr = "0"+String.valueOf(month);
            }
            
            //???????????? ??????????????? ??????
            Calendar scheduledDate = Calendar.getInstance();
            scheduledDate.set(Calendar.YEAR, year);
            scheduledDate.set(Calendar.MONTH, month-1);
            scheduledDate.set(Calendar.DATE, date);

            //???????????? ??????????????? week??? ?????????
            int week = scheduledDate.get(Calendar.WEEK_OF_MONTH) + 1;

            Log.i(TAG, "num of week : " + week);

            //preference?????? ????????? ?????? ?????? ????????????
            SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            String key = String.valueOf(year) + monthStr;
            String jsonMonth = pref.getString(key, "");
            Log.i(TAG, jsonMonth);
            BaseDateInfoList baseDateInfoList = FromJsonToList(jsonMonth);

            //???????????? ???????????? full-span-row??? ????????? ???????????????
            boolean checker = false;

            for (int i = 0; i < scheduledDate.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                scheduledDate.set(Calendar.DATE, i + 1);
                int temp = scheduledDate.get(Calendar.WEEK_OF_MONTH);

                //???????????? ?????????????????? ????????? ??????????????? ??????????????? ???????????? ????????? ????????? ??????(????????? ????????? ?????? ??????)
                if (temp == week && !checker) {
                    for (int j = 0; j < baseDateInfoList.getBaseDateInfoList().size(); j++) {
                        Log.i(TAG, "??????!!! ???????????? : " + (i+1) + "??? ????????? ?????? : " + temp + ", ???????????? : " + date + "??? ????????? ?????? : " + week );

                        //????????? 200??? ?????? ????????? ?????? ??????
                        BaseDateInfo baseDateInfo = new BaseDateInfo();
                        baseDateInfo.setDate(200);
                        //i????????? ?????? ????????? i+1??? ????????? ???????????? ??????????????????
                        if(baseDateInfoList.getBaseDateInfoList().get(j).getDate()==(i+1)){
                        baseDateInfoList.getBaseDateInfoList().add(j, baseDateInfo);
                        //1?????? ??????
                        checker = true;
                        break;}
                    }
                }
            }
            //???????????? ??????
            int temp = scheduledDate.get(Calendar.WEEK_OF_MONTH);
            if((week-1)==temp){
                //????????? 200??? ?????? ????????? ?????? ??????
                BaseDateInfo baseDateInfo = new BaseDateInfo();
                baseDateInfo.setDate(200);
                baseDateInfoList.getBaseDateInfoList().add(baseDateInfo);
            }

            setUpRecView(baseDateInfoList);
        }

    /**
     * ?????? ?????? ????????? ??? ??? ??????
     *
     * @param month
     */
    private void setUnselectedCalendar(String year, String month) {
        Log.i(TAG, "setUnselectedCalendar");
        //?????? ?????? ?????? ????????????

        tvYearMonthTop.setText(year + "/" + month);
        year_month = tvYearMonthTop.getText().toString();

        //preference?????? ????????? ?????? ?????? ????????????
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String key = year + month;
        String jsonMonth = pref.getString(key, "");
        BaseDateInfoList baseDateInfoList = FromJsonToList(jsonMonth);

        Log.i(TAG, "year_month " + year_month);


        setUpRecView(baseDateInfoList);

    }

    private BaseDateInfoList FromJsonToList(String json) {

        //Preference??? ?????? ?????? ?????? ?????? ????????????

        Gson gson = new GsonBuilder().create();

        BaseDateInfoList baseDateInfoList = new BaseDateInfoList();

        baseDateInfoList = gson.fromJson(json, BaseDateInfoList.class);
        //Preference??? ????????? ????????? class ????????? ???????????? ??????

        return baseDateInfoList;

    }

        @Override protected void onResume() {
            super.onResume();
            Log.i(TAG, "onResume()");

//            if (adapter == null) {
//                setUpRecView();
//            }
        }

        private void setUpRecView(BaseDateInfoList baseDateInfoList) {

            Log.i(TAG, "starting setUpRecView" + baseDateInfoList.getBaseDateInfoList().size());
            adapter = new RecyclerViewAdapter(this, baseDateInfoList);
            mRecyclerViewNow = (RecyclerView) findViewById(R.id.rv_calendar);

            //?????? fullspan??? ?????? staggeredGridlayout manager ??????
            final StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerViewNow.setLayoutManager(layoutManager);

            //recyclerView.addOnScrollListener(scrollListener);
            mRecyclerViewNow.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //?????? ?????? ??????????
            mRecyclerViewNow.invalidate();

            //preference ?????? ??????
            SharedPreferences sp = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    editor.putString("when",year_month);
                    editor.commit();
                }
            });
        }

    private void loadScheduleData(String year, String month){

        progressDoalog.show();

        //CalenderInfoPresenter baseCalenderInfo = new CalenderInfoPresenter(mContext);
        Runnable runnable = new CalenderInfoPresenter(year, mContext);
        runnable.run();
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dismissProgressDialog();
        initView();
        //baseCalenderInfo.run(year, month, mContext);
        //baseCalenderInfo.setDaemon(true);
        //baseCalenderInfo.start();
        //preference??? ?????? ????????? ??? ?????? ???,?????? ??????, ????????? ???????????? ???????????????.

    }

    public void dismissProgressDialog(){
        progressDoalog.dismiss();
    }

    public boolean getmLunaChecker(){
        return mLunaChecker;
    }

    private void refreshActivity(){
        try {
            //?????? ???????????? ????????????
            Intent intent = getIntent();
            finish(); //?????? ???????????? ?????? ??????
            overridePendingTransition(0, 0); //????????? ??????????????? ?????????
            startActivity(intent); //?????? ???????????? ????????? ??????
            overridePendingTransition(0, 0); //????????? ??????????????? ?????????
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean leapYear(int year) {

        GregorianCalendar cal = new GregorianCalendar();

        if (cal.isLeapYear(year))
            //???????????????.
            return true;
        else
            //???????????????.
            return false;

    }

}