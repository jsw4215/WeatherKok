package com.example.weatherkok.when;

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
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.weatherkok.R;
import com.example.weatherkok.main.MainActivity;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.base.BaseDateInfo;
import com.example.weatherkok.when.models.base.BaseDateInfoList;
import com.example.weatherkok.when.utils.CalenderInfoPresenter;
import com.example.weatherkok.when.utils.DialogDateSelector;
import com.example.weatherkok.when.utils.RecyclerViewAdapter;
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
        ImageView mTvDialog;
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

            initView();


            mCal = Calendar.getInstance();


            SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            //when 초기화


            Intent intent = getIntent();
            //인텐트 체크 후 없으면, 현재 월, 있으면 해당 월 달력 띄우기
            if(!TextUtils.isEmpty(intent.getStringExtra("year"))
                    &&!TextUtils.isEmpty(intent.getStringExtra("month"))){
                String year = intent.getStringExtra("year");
                String month = intent.getStringExtra("month");
                initForNotCurr(year, month);

            } else {
                editor.putString("temp_when","");
                editor.commit();
                setCurrentYearMonth();
            }


            //클릭리스너 처리

            mTvLuna.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mTvLuna.isChecked()){
                    mTvLuna.setChecked(true);
                    mTvSolar.setChecked(false);
                    //뷰에 visible invisible
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
                        mTvSolar.setChecked(true);
                        mTvLuna.setChecked(false);
                        //뷰에 visible invisible
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
                    //Preference에 저장
                    //누구랑, 어디서와 함께 저장하기 위해 우선 temp로 가져갈것
                    editor.putString("temp_when", mSelectedDate);
                    editor.commit();

                    //인텐트로 Main으로 이동
                    Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                    intent.putExtra("fromApp",true);
                    finish();
                    startActivity(intent);
                }
            });

            mTvDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogDateSelector dialog = new DialogDateSelector(CalendarActivity.this);
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
                    if(b==0){b=12; a=a-1;}
                    yearForAfter = String.valueOf(a);
                    monthForAfter = String.valueOf(b);
                    if(monthForAfter.length()<2){
                        monthForAfter="0"+monthForAfter;
                    }
                    if(!(a<2010)){
                        Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("year",yearForAfter);
                        intent.putExtra("month",monthForAfter);
                        finish();
                        overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        startActivity(intent);
                        overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
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
                    if(b==13){b=1; a=a+1;}
                    yearForBefore = String.valueOf(a);
                    monthForBefore = String.valueOf(b);
                    if(monthForBefore.length()<2){
                        monthForBefore="0"+monthForBefore;
                    }

                    if(!(a>mMaxYear)){
                        Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("year",yearForBefore);
                        intent.putExtra("month",monthForBefore);
                        finish();
                        overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                        startActivity(intent);
                        overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
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

        }

        private void initForNotCurr(String year, String month){

            SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            //선택날짜 아직 없지만, 어댑터에서 필요하니 월년 정보 넣어두고
            setmSelectedDate(year+ "/" +month+ "/" +"00");
            year_month=year + "/" + month;
            tvYearMonthTop.setText(year_month);
            //월,년 키값 넣어두고
            editor.putString("when",year+"/"+month);
            //temp 초기화
            editor.putString("temp_when","");
            editor.commit();
            //오늘날짜 계산
            calculateToday();
            //최대 년도 설정
            setMaxYear();
            //DB 세팅 - 처음 보는 날짜일 경우
            loadScheduleData(year,month);
            //달력데이터 뷰에 세팅
            setUnselectedCalendar(year, month);

        }

        private Date getToday(){
            // 오늘에 날짜를 세팅 해준다.
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

            //연,월,일을 따로 저장
            final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
            final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
            final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

            //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
            String currentYear = curYearFormat.format(date);

            int temp = Integer.parseInt(curMonthFormat.format(date));
            String currentMonth = String.valueOf(temp);
            if(temp<10) {
                currentMonth = "0"+currentMonth;
            }
            String currentDay = curDayFormat.format(date);
            //오늘일자에 저장
            //currentMonth="07";
            setmToday(currentYear+currentMonth+currentDay);
            //최대 년도 설정
            setMaxYear();
            //선택일자에 저장
            setmSelectedDate(currentYear+"/"+currentMonth+"/"+currentDay);

            //현재 날짜 텍스트뷰에 뿌려줌
            tvYearMonthTop.setText(currentYear + "/" + currentMonth);
            year_month = tvYearMonthTop.getText().toString();
            Log.i(TAG,"month : " + temp + ", stirng : " + currentMonth);
            //년,월의 정보를 갖고 해당 월의 양력,음력,공휴일 정보를 preference에 세팅한다.
            //DB세팅
            loadScheduleData(currentYear, currentMonth);
            //달력 정보 리스트 세팅
            setUnselectedCalendar(currentYear, currentMonth);

        }

        private void calculateToday(){
            Date date = getToday();

            //연,월,일을 따로 저장
            final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
            final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
            final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

            //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
            String currentYear = curYearFormat.format(date);

            int temp = Integer.parseInt(curMonthFormat.format(date));
            String currentMonth = String.valueOf(temp);
            if(temp<10) {
                currentMonth = "0"+currentMonth;
            }
            String currentDay = curDayFormat.format(date);
            //오늘일자에 저장
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
            //Preference에서 스케줄 데이터 불러오기
            //loadScheduleData(Integer.toString(year), Integer.toString(month));

            setCalendarDate(year, month, date);

        }


        public void setCalendarDate(int year, int month, int date) {

            //set(월별달력, 몇월(0-11))
            Log.i(TAG, "starting setCalendarDate : " + year + month + date);

            String monthStr = null;
            if(month<10) {
                monthStr = "0"+String.valueOf(month);
            }
            
            //약속일자 몇째주인지 확인
            Calendar scheduledDate = Calendar.getInstance();
            scheduledDate.set(Calendar.YEAR, year);
            scheduledDate.set(Calendar.MONTH, month-1);
            scheduledDate.set(Calendar.DATE, date);

            //약속일자 몇째주인지 week로 받아옴
            int week = scheduledDate.get(Calendar.WEEK_OF_MONTH) + 1;

            Log.i(TAG, "num of week : " + week);

            //preference에서 한달의 모든 정보 가져오기
            SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            String key = String.valueOf(year) + monthStr;
            String jsonMonth = pref.getString(key, "");
            Log.i(TAG, jsonMonth);
            BaseDateInfoList baseDateInfoList = FromJsonToList(jsonMonth);

            //스케쥴을 보여주는 full-span-row가 한번만 실행되도록
            boolean checker = false;

            for (int i = 0; i < scheduledDate.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                scheduledDate.set(Calendar.DATE, i + 1);
                int temp = scheduledDate.get(Calendar.WEEK_OF_MONTH);

                //약속일자 몇째주인지와 달력에 뿌리는일자 몇째주인지 비교해서 같으면 빈공간 투입(클릭시 메시지 띄울 공간)
                if (temp == week && !checker) {
                    for (int j = 0; j < baseDateInfoList.getBaseDateInfoList().size(); j++) {
                        Log.i(TAG, "빈칸!!! 비교일자 : " + (i+1) + "일 몇째주 주말 : " + temp + ", 선택일자 : " + date + "일 몇째주 주말 : " + week );

                        //날짜에 200을 넣어 빈공간 투입 구분
                        BaseDateInfo baseDateInfo = new BaseDateInfo();
                        baseDateInfo.setDate(200);
                        //i번째가 아닌 날짜가 i+1일 인것과 매칭해서 집어넣어야함
                        if(baseDateInfoList.getBaseDateInfoList().get(j).getDate()==(i+1)){
                        baseDateInfoList.getBaseDateInfoList().add(j, baseDateInfo);
                        //1번만 실행
                        checker = true;
                        break;}
                    }
                }
            }
            //마지막주 확인
            int temp = scheduledDate.get(Calendar.WEEK_OF_MONTH);
            if((week-1)==temp){
                //날짜에 200을 넣어 빈공간 투입 구분
                BaseDateInfo baseDateInfo = new BaseDateInfo();
                baseDateInfo.setDate(200);
                baseDateInfoList.getBaseDateInfoList().add(baseDateInfo);
            }

            setUpRecView(baseDateInfoList);
        }

    /**
     * 해당 월에 표시할 일 수 구함
     *
     * @param month
     */
    private void setUnselectedCalendar(String year, String month) {
        Log.i(TAG, "setUnselectedCalendar");
        //한달 모든 정보 가져오기

        //preference에서 한달의 모든 정보 가져오기
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String key = year + month;
        String jsonMonth = pref.getString(key, "");
        BaseDateInfoList baseDateInfoList = FromJsonToList(jsonMonth);

        Log.i(TAG, "year_month " + year_month);


        setUpRecView(baseDateInfoList);

    }

    private BaseDateInfoList FromJsonToList(String json) {

        //Preference에 한달 모든 정보 객체 불러오기

        Gson gson = new GsonBuilder().create();

        BaseDateInfoList baseDateInfoList = new BaseDateInfoList();

        baseDateInfoList = gson.fromJson(json, BaseDateInfoList.class);
        //Preference에 저장된 데이터 class 형태로 불러오기 완료

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

            //빈칸 fullspan을 위한 staggeredGridlayout manager 사용
            final StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerViewNow.setLayoutManager(layoutManager);

            //recyclerView.addOnScrollListener(scrollListener);
            mRecyclerViewNow.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //뷰를 다시 그린다?
            mRecyclerViewNow.invalidate();

            //preference 동작 준비
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

        CalenderInfoPresenter baseCalenderInfo = new CalenderInfoPresenter();
        //preference에 더미 데이터 및 해당 년,월의 음력, 공휴일 데이터를 생성해준다.
        baseCalenderInfo.initCal(year, month, mContext);

    }


    public boolean getmLunaChecker(){
        return mLunaChecker;
    }

    private void refreshActivity(){
        try {
            //현재 액티비티 새로고침
            Intent intent = getIntent();
            finish(); //현재 액티비티 종료 실시
            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
            startActivity(intent); //현재 액티비티 재실행 실시
            overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean leapYear(int year) {

        GregorianCalendar cal = new GregorianCalendar();

        if (cal.isLeapYear(year))
            //윤년입니다.
            return true;
        else
            //평년입니다.
            return false;

    }

}