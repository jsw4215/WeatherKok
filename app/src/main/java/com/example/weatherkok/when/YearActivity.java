package com.example.weatherkok.when;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.weatherkok.R;
import com.example.weatherkok.src.BaseActivity;
import com.example.weatherkok.when.models.base.BaseDateInfo;
import com.example.weatherkok.when.models.base.BaseDateInfoList;
import com.example.weatherkok.when.utils.CalenderInfoPresenter;
import com.example.weatherkok.when.utils.DialogDateSelector;
import com.example.weatherkok.when.utils.YearRecyclerViewAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class YearActivity extends BaseActivity{
    private static final String TAG = CalendarActivity.class.getSimpleName();
    YearRecyclerViewAdapter adapter;
    Context mContext;
    String mSelectedDate;
    String mToday="";
    boolean mLunaChecker=false;
    RecyclerView Jan;
    RecyclerView Feb;
    RecyclerView Mar;
    RecyclerView Apr;
    RecyclerView May;
    RecyclerView Jun;
    RecyclerView Jul;
    RecyclerView Aug;
    RecyclerView Sep;
    RecyclerView Oct;
    RecyclerView Nov;
    RecyclerView Dec;
    TextView mTvDialog;
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getBaseContext();

        initView();

        Intent intent = getIntent();
        //null이든 null이 아니든 intent를 가져온다.
        if(!TextUtils.isEmpty(intent.getStringExtra("year"))) {

            String year = intent.getStringExtra("year");
            setCurrentYearMonth(year);
            setYearCalendar(year);

        } else {

            Date date = getToday();
            //연,월,일을 따로 저장
            //오늘 표시위해서
            final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
            //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
            String currentYear = curYearFormat.format(date);
            setYearCalendar(currentYear);

        }



        mTvDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogDateSelector dialog = new DialogDateSelector(YearActivity.this);
                dialog.show();

            }
        });
    }

    private void initView(){
        setContentView(R.layout.activity_year);

        mTvDialog = findViewById(R.id.tv_year_dialog);

        Jan = findViewById(R.id.rv_year_jan);
        Feb = findViewById(R.id.rv_year_feb);
        Mar = findViewById(R.id.rv_year_march);
        Apr = findViewById(R.id.rv_year_april);
        May = findViewById(R.id.rv_year_may);
        Jun = findViewById(R.id.rv_year_june);
        Jul = findViewById(R.id.rv_year_july);
        Aug = findViewById(R.id.rv_year_aug);
        Sep = findViewById(R.id.rv_year_sep);
        Oct = findViewById(R.id.rv_year_oct);
        Nov = findViewById(R.id.rv_year_nov);
        Dec = findViewById(R.id.rv_year_dec);

    }

    private Date getToday(){
        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        return date;
    }

    public String getmSelectedDate() {
        return mSelectedDate;
    }

    private void setCurrentYearMonth(String year){

        Date date = getToday();

        //연,월,일을 따로 저장
        //오늘 표시위해서 유지해야함
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //현재 년,월을 구해 저장한다.(주의:월 정보는 +1해줘야한다)
        String currentYear = curYearFormat.format(date);


        if(year==null||year.isEmpty()||year.equals("")){
            year = currentYear;
        }


        int temp = Integer.parseInt(curMonthFormat.format(date));
        String currentMonth = String.valueOf(temp);
        if(temp<10) {
            currentMonth = "0"+currentMonth;
        }
        String currentDay = curDayFormat.format(date);
        //오늘일자에 저장
        //currentMonth="07";
        setmToday(currentYear+currentMonth+currentDay);
        //선택일자에 저장
        //setmSelectedDate(currentYear+"/"+currentMonth+"/"+currentDay);

        //현재 날짜 텍스트뷰에 뿌려줌

        Log.i(TAG,"month : " + temp + ", stirng : " + currentMonth);
        //년,월의 정보를 갖고 해당 월의 양력,음력,공휴일 정보를 preference에 세팅한다.
        //DB세팅
        loadScheduleData(year, currentMonth);
        //달력 정보 리스트 세팅
        setUnselectedCalendar(year, currentMonth);

    }

    public void setmToday(String s) {
        this.mToday = s;
    }

    public String getmToday() {
        return mToday;
    }

    /**
     * 해당 월에 표시할 일 수 구함
     *
     * @param month
     */
    private BaseDateInfoList setUnselectedCalendar(String year, String month) {
        Log.i(TAG, "setUnselectedCalendar");
        //한달 모든 정보 가져오기

        //preference에서 한달의 모든 정보 가져오기
        SharedPreferences pref = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String key = year + month;
        String jsonMonth = pref.getString(key, "");
        BaseDateInfoList baseDateInfoList = FromJsonToList(jsonMonth);

        //연간 달력에서는 보이지 않는 리스트에서 요일 없애기
        removeDayAddMonth(baseDateInfoList, month);

        Log.i(TAG, "list size : " + String.valueOf(baseDateInfoList.getBaseDateInfoList().size()));

        return baseDateInfoList;
    }

    private BaseDateInfoList removeDayAddMonth(BaseDateInfoList baseDateInfoList, String month){

        ArrayList<BaseDateInfo> temp = new ArrayList<>();

        temp = baseDateInfoList.getBaseDateInfoList();

        for(int i =0;i<7;i++) {
            temp.remove(0);
        }

        BaseDateInfo monthObj = new BaseDateInfo();
        monthObj.setDate(Integer.parseInt(month));
        temp.add(0,monthObj);

        baseDateInfoList.setBaseDateInfoList(temp);

        return baseDateInfoList;
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

    private void setUpRecView(BaseDateInfoList baseDateInfoList, int rv, RecyclerView recyclerView, String yearMonth) {

        Log.i(TAG, "starting setUpRecView" + baseDateInfoList.getBaseDateInfoList().size());
        adapter = new YearRecyclerViewAdapter(this, baseDateInfoList, yearMonth);
        recyclerView = (RecyclerView) findViewById(rv);

        //빈칸 fullspan을 위한 staggeredGridlayout manager 사용
        final StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //뷰를 다시 그린다?
        recyclerView.invalidate();

        //preference 동작 준비
        SharedPreferences sp = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

    }

    private void loadScheduleData(String year, String month){

        CalenderInfoPresenter baseCalenderInfo = new CalenderInfoPresenter();
        //preference에 더미 데이터 및 해당 년,월의 음력, 공휴일 데이터를 생성해준다.
        baseCalenderInfo.initCal(year, month, mContext);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setYearCalendar(String year){
        //클릭리스너 처리
        loadScheduleData(year, "01");
        BaseDateInfoList janList = setUnselectedCalendar(year, "01");
        int janRv = R.id.rv_year_jan;
        setUpRecView(janList, janRv, Jan, year+"01");

        loadScheduleData(year, "02");
        BaseDateInfoList febList = setUnselectedCalendar(year, "02");
        int febRv = R.id.rv_year_feb;
        setUpRecView(febList, febRv, Feb, year +"02");

        loadScheduleData(year, "03");
        BaseDateInfoList marList = setUnselectedCalendar(year, "03");
        int marRv = R.id.rv_year_march;
        setUpRecView(marList, marRv, Mar, year + "03");

        loadScheduleData(year, "04");
        BaseDateInfoList aprList = setUnselectedCalendar(year, "04");
        int aprRv = R.id.rv_year_april;
        setUpRecView(aprList, aprRv, Apr, year + "04");

        loadScheduleData(year, "05");
        BaseDateInfoList mayList = setUnselectedCalendar(year, "05");
        int mayRv = R.id.rv_year_may;
        setUpRecView(mayList, mayRv, May, year + "05");

        loadScheduleData(year, "06");
        BaseDateInfoList junList = setUnselectedCalendar(year, "06");
        int junRv = R.id.rv_year_june;
        setUpRecView(junList, junRv, Jun, year + "06");

        loadScheduleData(year, "07");
        BaseDateInfoList julList = setUnselectedCalendar(year, "07");
        int julRv = R.id.rv_year_july;
        setUpRecView(julList, julRv, Jul, year + "07");

        loadScheduleData(year, "08");
        BaseDateInfoList augList = setUnselectedCalendar(year, "08");
        int augRv = R.id.rv_year_aug;
        setUpRecView(augList, augRv, Aug, year + "08");

        loadScheduleData(year, "09");
        BaseDateInfoList sepList = setUnselectedCalendar(year, "09");
        int sepRv = R.id.rv_year_sep;
        setUpRecView(sepList, sepRv, Sep, year +"09");

        loadScheduleData(year, "10");
        BaseDateInfoList octList = setUnselectedCalendar(year, "10");
        int octRv = R.id.rv_year_oct;
        setUpRecView(octList, octRv, Oct, year + "10");

        loadScheduleData(year, "11");
        BaseDateInfoList novList = setUnselectedCalendar(year, "11");
        int novRv = R.id.rv_year_nov;
        setUpRecView(novList, novRv, Nov, year + "11");

        loadScheduleData(year, "12");
        BaseDateInfoList decList = setUnselectedCalendar(year, "12");
        int decRv = R.id.rv_year_dec;
        setUpRecView(decList, decRv, Dec, year + "12");
    }

}