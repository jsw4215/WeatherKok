package com.example.weatherkok.when.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.weatherkok.R;
import com.example.weatherkok.when.CalendarActivity;
import com.example.weatherkok.when.models.Schedule;
import com.example.weatherkok.when.models.ScheduleList;
import com.example.weatherkok.when.models.base.BaseDateInfoList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    private static final String TAG = "RecyclerViewAdapter";
    private BaseDateInfoList List;
    Context mContext;
    String mSelected;
    String mToday;
    String mYMD;
    String[] resultSplit = new String[3];
    public static final int SOLAR = 0;
    public static final int LUNA = 1;


    public RecyclerViewAdapter(Context context, BaseDateInfoList List) {
        Log.i(TAG, "Constructor");
        this.mContext = context;
        this.List = List;
        mSelected = ((CalendarActivity) mContext).getmSelectedDate();
        mToday = ((CalendarActivity) mContext).getmToday();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        if(type==LUNA){
            Log.i(TAG, "onCreateViewHolder");
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_main_calendar, viewGroup, false);
            return new ViewHolder(view);

        }else {
            Log.i(TAG, "onCreateViewHolder");
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_solar_calendar, viewGroup, false);
            return new SolarViewHolder(view);

        }


    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int positionItem) {

        //양력 날짜 가져오기
        int item = List.getBaseDateInfoList().get(positionItem).getDate();

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        //resultSplit[0]년도, [1] 월, [2] 일

        resultSplit = String.valueOf(mSelected).split("/");

        Calendar calendar = Calendar.getInstance();

        //일요일 빨강
        calendar.set(Calendar.YEAR, Integer.parseInt(resultSplit[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(resultSplit[1]));
        calendar.set(Calendar.DATE, List.getBaseDateInfoList().get(positionItem).getDate());

        int seldate = Integer.parseInt(resultSplit[2]);

        //양력달력 음력달력 분기
        if(viewHolder instanceof ViewHolder) {

            //달력의 메모부분, 스케쥴 있을 경우 표시, 일요일 토요일 색상, 선택된 날짜 배경색, 오늘 날짜 검은색 등 모든 조건 분기문
            if (item == 200) {
                Log.i(TAG, "메시지 빈칸 for selected : " + positionItem + item + mSelected + "seldate : " + seldate);
                //**선택한 일자를 가진 리스트의 순서가 몇번째인지 찾아내기
                int x = 999;
                for (int i = 0; i < List.getBaseDateInfoList().size(); i++) {
                    if (List.getBaseDateInfoList().get(i).getDate() == seldate) {
                        x = i;
                        break;
                    }
                }
                //선택된 날짜의 주차를 계산 후 아래칸의 메모공간을 만들어야함
                //선택되었을 때 특정일자 정보 표시

                //full span!!
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);

                //스케쥴 문자열로 구현
                //어디로? : ㅇㅇㅇ, 누구랑? : ㅇㅇㅇ, ㅇㅇㅇ

                String scheduleMemo;

                String friends = "";
                //친구 여러명일시, 문자열 만들기
                //널체크.. 그냥 add "" 넣어서 우회
                if (List.getBaseDateInfoList().get(x).getSchedule() != null) {
                    if (List.getBaseDateInfoList().get(x).getSchedule().getWho() != null) {
                        for (int i = 0; i < List.getBaseDateInfoList().get(x).getSchedule().getWho().size(); i++) {
                            friends += List.getBaseDateInfoList().get(x).getSchedule().getWho().get(i);
                            //마지막엔 콤마 없이
                            if ((i + 1) != List.getBaseDateInfoList().get(x).getSchedule().getWho().size()) {
                                friends += ", ";
                            }
                        }
                    }
                }
                //스케쥴 있으면 문자열 메모 생성
                if (List.getBaseDateInfoList().get(x).getSchedule() != null) {
                    if (List.getBaseDateInfoList().get(x).getSchedule().getWhere() != null) {
                        scheduleMemo = "어디로? : " + List.getBaseDateInfoList().get(x).getSchedule().getWhere() + ", 누구랑? : " + friends;
                        ((ViewHolder) viewHolder).tv.setText(scheduleMemo);
                    } else {
                        scheduleMemo = "어디로? : " + ", 누구랑? : " + friends;
                        ((ViewHolder) viewHolder).tv.setText(scheduleMemo);
                    }
                }
                //스케쥴이 없을 시 분기 구현
                if (List.getBaseDateInfoList().get(x).getSchedule() == null) {
                    String luna = makeLuna(x);
                    if (List.getBaseDateInfoList().get(x).getNameOfDay() != null) {
                        String holiday = List.getBaseDateInfoList().get(x).getNameOfDay();
                        //공휴일 빨간색
                        ((ViewHolder) viewHolder).tv.setText("음력 : " + luna + ", " + holiday);
                    } else {

                        ((ViewHolder) viewHolder).tv.setText("음력 : " + luna);
                    }
                }
                //요일 구분하기
            } else if (item == 100 || item == 101 || item == 102 || item == 103 || item == 104 || item == 105 || item == 106 || item == 99) {
                Log.i(TAG, "요일 : " + item);
                switch (item) {
                    case 100: {
                        ((ViewHolder) viewHolder).tv.setText("일");
                        ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                        break;
                    }

                    case 101: {
                        ((ViewHolder) viewHolder).tv.setText("월");
                        break;
                    }
                    case 102: {
                        ((ViewHolder) viewHolder).tv.setText("화");
                        break;
                    }
                    case 103: {
                        ((ViewHolder) viewHolder).tv.setText("수");
                        break;
                    }
                    case 104: {
                        ((ViewHolder) viewHolder).tv.setText("목");
                        break;
                    }
                    case 105: {
                        ((ViewHolder) viewHolder).tv.setText("금");
                        break;
                    }
                    case 106: {
                        ((ViewHolder) viewHolder).tv.setText("토");
                        ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.purple_700));
                        break;
                    }
                    case 99: {
                        ((ViewHolder) viewHolder).tv.setText("");
                        break;
                    }
                }

            }
            //스케쥴 있는 날짜라면,
            else if ((List.getBaseDateInfoList().get(positionItem).getSchedule()) != null
                    && (List.getBaseDateInfoList().get(positionItem).getSchedule().getWhere()) != null
                    && !(List.getBaseDateInfoList().get(positionItem).getSchedule().getWhere()).equals("")) {
                Log.i(TAG, "schedule dot" + item + "일");
                //item = item.substring(1);
                ((ViewHolder) viewHolder).tv.setText(String.valueOf(item));
                ((ViewHolder) viewHolder).tvScheduled.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.selected_dot));

                //음력표시 -> 나중에 버튼 클릭시 보이는 것으로 변경할 것
                if (List.getBaseDateInfoList().get(positionItem).getLuna() != null
                        && !List.getBaseDateInfoList().get(positionItem).getLuna().equals("")) {
                    String luna = makeLuna(positionItem);
                    ((ViewHolder) viewHolder).tvLuna.setText(luna);
                }

                int day = calendar.get(Calendar.DAY_OF_WEEK);
                if (day == 1) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                }

                //토요일 파랑
                if (day == 7) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.purple_700));
                }

                //공휴일 빨강
                if (!TextUtils.isEmpty(List.getBaseDateInfoList().get(positionItem).getNameOfDay())) {
                ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                }

                //오늘 날짜 black
                if (mToday.equals(String.valueOf(item))) { //오늘 day 텍스트 컬러 변경
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                if (seldate == List.getBaseDateInfoList().get(positionItem).getDate()) {
                    ((ViewHolder) viewHolder).tv.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.bg_selected_calendar_day));
                }
                //사용자가 선택한 날짜일 경우
            } else if (seldate == List.getBaseDateInfoList().get(positionItem).getDate()) {
                //날짜 세팅하고
                ((ViewHolder) viewHolder).tv.setText(String.valueOf(item));

                //오늘 날짜 black
                if (mToday.equals(String.valueOf(item))) { //오늘 day 텍스트 컬러 변경
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                //음력표시 -> 나중에 버튼 클릭시 보이는 것으로 변경할 것
                if (List.getBaseDateInfoList().get(positionItem).getLuna() != null
                        && !List.getBaseDateInfoList().get(positionItem).getLuna().equals("")) {
                    ((ViewHolder) viewHolder).tvLuna.setText(makeLuna(positionItem));
                }
                //공휴일 빨강
                if (!TextUtils.isEmpty(List.getBaseDateInfoList().get(positionItem).getNameOfDay())) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                }
                //배그라운드 표시
                Log.i(TAG, "selected date");
                ((ViewHolder) viewHolder).tv.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.bg_selected_calendar_day));

            } else {
                //스케쥴도 없고 선택된 날짜도 아닌경우
                Log.i(TAG, "works");
                //일자 텍스트뷰에 표시
                ((ViewHolder) viewHolder).tv.setText(String.valueOf(item));

                //오늘 날짜 black
                if (mToday.equals(item)) { //오늘 day 텍스트 컬러 변경
                    ((ViewHolder)viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                //음력표시 -> 나중에 버튼 클릭시 보이는 것으로 변경할 것
                if (List.getBaseDateInfoList().get(positionItem).getLuna() != null
                        && !List.getBaseDateInfoList().get(positionItem).getLuna().equals("")) {
                    ((ViewHolder) viewHolder).tvLuna.setText(makeLuna(positionItem));
                }

                //일요일 빨강
                calendar.set(Calendar.YEAR, Integer.parseInt(resultSplit[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(resultSplit[1]) - 1);
                calendar.set(Calendar.DATE, List.getBaseDateInfoList().get(positionItem).getDate());

                int day = calendar.get(Calendar.DAY_OF_WEEK);
                if (day == 1) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                }

                //토요일 파랑
                if (day == 7) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.purple_700));
                }
                //공휴일 빨강
                if (!TextUtils.isEmpty(List.getBaseDateInfoList().get(positionItem).getNameOfDay())) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                }
            }
            //양력 달력
        }else {

            //달력의 메모부분, 스케쥴 있을 경우 표시, 일요일 토요일 색상, 선택된 날짜 배경색, 오늘 날짜 검은색 등 모든 조건 분기문
            if (item == 200) {
                Log.i(TAG, "메시지 빈칸 for selected : " + positionItem + item + mSelected + "seldate : " + seldate);
                //**선택한 일자를 가진 리스트의 순서가 몇번째인지 찾아내기
                int x = 999;
                for (int i = 0; i < List.getBaseDateInfoList().size(); i++) {
                    if (List.getBaseDateInfoList().get(i).getDate() == seldate) {
                        x = i;
                        break;
                    }
                }
                //선택된 날짜의 주차를 계산 후 아래칸의 메모공간을 만들어야함
                //선택되었을 때 특정일자 정보 표시

                //full span!!
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);

                //스케쥴 문자열로 구현
                //어디로? : ㅇㅇㅇ, 누구랑? : ㅇㅇㅇ, ㅇㅇㅇ

                String scheduleMemo;

                String friends = "";
                //친구 여러명일시, 문자열 만들기
                //널체크.. 그냥 add "" 넣어서 우회
                if (List.getBaseDateInfoList().get(x).getSchedule() != null) {
                    if (List.getBaseDateInfoList().get(x).getSchedule().getWho() != null) {
                        for (int i = 0; i < List.getBaseDateInfoList().get(x).getSchedule().getWho().size(); i++) {
                            friends += List.getBaseDateInfoList().get(x).getSchedule().getWho().get(i);
                            //마지막엔 콤마 없이
                            if ((i + 1) != List.getBaseDateInfoList().get(x).getSchedule().getWho().size()) {
                                friends += ", ";
                            }
                        }
                    }
                }
                //스케쥴 있으면 문자열 메모 생성
                if (List.getBaseDateInfoList().get(x).getSchedule() != null) {
                    if (List.getBaseDateInfoList().get(x).getSchedule().getWhere() != null) {
                        scheduleMemo = "어디로? : " + List.getBaseDateInfoList().get(x).getSchedule().getWhere() + ", 누구랑? : " + friends;
                        ((SolarViewHolder) viewHolder).tv.setText(scheduleMemo);
                    } else {
                        scheduleMemo = "어디로? : " + ", 누구랑? : " + friends;
                        ((SolarViewHolder) viewHolder).tv.setText(scheduleMemo);
                    }
                }
                //스케쥴이 없을 시 분기 구현
                if (List.getBaseDateInfoList().get(x).getSchedule() == null) {
                    String luna = makeLuna(x);
                    if (List.getBaseDateInfoList().get(x).getNameOfDay() != null) {
                        String holiday = List.getBaseDateInfoList().get(x).getNameOfDay();
                        //공휴일 빨간색
                        ((SolarViewHolder) viewHolder).tv.setText("음력 : " + luna + ", " + holiday);
                    } else {
                        ((SolarViewHolder) viewHolder).tv.setText("음력 : " + luna);
                    }
                }
                //요일 구분하기
            } else if (item == 100 || item == 101 || item == 102 || item == 103 || item == 104 || item == 105 || item == 106 || item == 99) {
                Log.i(TAG, "요일 : " + item);
                switch (item) {
                    case 100: {
                        ((SolarViewHolder) viewHolder).tv.setText("일");
                        ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                        break;
                    }

                    case 101: {
                        ((SolarViewHolder) viewHolder).tv.setText("월");
                        break;
                    }
                    case 102: {
                        ((SolarViewHolder) viewHolder).tv.setText("화");
                        break;
                    }
                    case 103: {
                        ((SolarViewHolder) viewHolder).tv.setText("수");
                        break;
                    }
                    case 104: {
                        ((SolarViewHolder) viewHolder).tv.setText("목");
                        break;
                    }
                    case 105: {
                        ((SolarViewHolder) viewHolder).tv.setText("금");
                        break;
                    }
                    case 106: {
                        ((SolarViewHolder) viewHolder).tv.setText("토");
                        ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.purple_700));
                        break;
                    }
                    case 99: {
                        ((SolarViewHolder) viewHolder).tv.setText("");
                        break;
                    }
                }

            }
            //스케쥴 있는 날짜라면,
            else if ((List.getBaseDateInfoList().get(positionItem).getSchedule()) != null
                    && (List.getBaseDateInfoList().get(positionItem).getSchedule().getWhere()) != null
                    && !(List.getBaseDateInfoList().get(positionItem).getSchedule().getWhere()).equals("")) {
                Log.i(TAG, "schedule dot" + item + "일");
                //item = item.substring(1);
                ((SolarViewHolder) viewHolder).tv.setText(String.valueOf(item));
                ((SolarViewHolder) viewHolder).tvScheduled.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.selected_dot));

                setTextColor(viewHolder, positionItem);

                //오늘 날짜 black
                if (mToday.equals(String.valueOf(item))) { //오늘 day 텍스트 컬러 변경
                    ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                if (seldate == List.getBaseDateInfoList().get(positionItem).getDate()) {
                    ((SolarViewHolder) viewHolder).tv.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.bg_selected_calendar_day));
                }
                //사용자가 선택한 날짜일 경우
            } else if (seldate == List.getBaseDateInfoList().get(positionItem).getDate()) {
                //날짜 세팅하고
                ((SolarViewHolder) viewHolder).tv.setText(String.valueOf(item));

                //오늘 날짜 black
                if (mToday.equals(String.valueOf(item))) { //오늘 day 텍스트 컬러 변경
                    ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                setTextColor(viewHolder, positionItem);

//                //공휴일 빨강
//                if (!TextUtils.isEmpty(List.getBaseDateInfoList().get(positionItem).getNameOfDay())) {
//                    ((YearRecyclerViewAdapter.SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
//                }
                //배그라운드 표시
                Log.i(TAG, "selected date");
                ((SolarViewHolder) viewHolder).tv.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.bg_selected_calendar_day));

            } else {
                //스케쥴도 없고 선택된 날짜도 아닌경우
                Log.i(TAG, "works");
                //일자 텍스트뷰에 표시
                ((SolarViewHolder) viewHolder).tv.setText(String.valueOf(item));

                //오늘 날짜 black
                if (mToday.equals(item)) { //오늘 day 텍스트 컬러 변경
                    ((SolarViewHolder)viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                setTextColor(viewHolder, positionItem);

            }
        }

    }


    private void setTextColor(RecyclerView.ViewHolder viewHolder,int positionItem){


        Calendar calendar = Calendar.getInstance();

        //일요일 빨강
        calendar.set(Calendar.YEAR, Integer.parseInt(resultSplit[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(resultSplit[1]) - 1);
        calendar.set(Calendar.DATE, List.getBaseDateInfoList().get(positionItem).getDate());

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
        }

        //토요일 파랑
        if (day == 7) {
            ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.purple_700));
        }

        //공휴일 빨강
        if (!TextUtils.isEmpty(List.getBaseDateInfoList().get(positionItem).getNameOfDay())) {
            ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
        }

    }

    @Override
    public int getItemCount() {
        return List.getBaseDateInfoList().size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class SolarViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        TextView tvScheduled;
        String where_from_holder;

        SolarViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.tv_solarcal_item);
            tvScheduled = view.findViewById(R.id.tv_solarcal_item_scheduled);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = view.getContext();

                    where_from_holder = tv.getText().toString();
                    int pos = getAdapterPosition();
                    Log.i(TAG, "pos : " + pos);
                    if (pos != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, pos);
                    }
                    //preference 동작 준비
                    SharedPreferences pref = context.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    //preference에 저장

                    String date = pref.getString("when", "");
                    Log.i("when should be y/m : ", date);

                    //특정 스케쥴을 저장할 리스트도 구현하기
                    String temp = tv.getText().toString();

                    //일자 한자리수일때 앞에 0 붙이기
                    if (temp.length() < 2) {
                        temp = "0" + temp;
                    }

                    date = date + "/" + temp;
                    editor.putString("when", date);
                    mYMD = date;
                    // 메모리에 있는 데이터를 저장장치에 저장한다.
                    editor.commit();

                    String temp2 = pref.getString("when", "");

                    Log.i(TAG, "from the preference : " + temp2);
                    if(!(temp.equals("월")||temp.equals("화")||temp.equals("수")||temp.equals("목")||
                            temp.equals("금")||temp.equals("토")||temp.equals("일"))) {
                        //set the selected date on the calendar.
                        ((CalendarActivity) mContext).parsingDate(temp2);
                    }
                }
            });
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        TextView tvScheduled;
        TextView tvLuna;
        String where_from_holder;

        ViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.tv_item);
            tvScheduled = view.findViewById(R.id.tv_item_scheduled);
            tvLuna = view.findViewById(R.id.tv_item_luna);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = view.getContext();

                    where_from_holder = tv.getText().toString();
                    int pos = getAdapterPosition();
                    Log.i(TAG, "pos : " + pos);
                    if (pos != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, pos);
                    }
                    //preference 동작 준비
                    SharedPreferences pref = context.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    // key값에 value값을 저장한다.
                    // String, boolean, int, float, long 값 모두 저장가능하다.
                    //기존에 있던 주소를 잠시 저장해두고,
                    //preference에 저장

                    String date = pref.getString("when", "");
                    Log.i("when should be y/m : ", date);

                    //특정 스케쥴을 저장할 리스트도 구현하기
                    String temp = tv.getText().toString();

                    //일자 한자리수일때 앞에 0 붙이기
                    if (temp.length() < 2) {
                        temp = "0" + temp;
                    }

                    date = date + "/" + temp;
                    editor.putString("when", date);
                    mYMD = date;
                    // 메모리에 있는 데이터를 저장장치에 저장한다.
                    editor.commit();

                    String temp2 = pref.getString("when", "");

                    Log.i(TAG, "from the preference : " + temp2);
                    if(!(temp.equals("월")||temp.equals("화")||temp.equals("수")||temp.equals("목")||
                            temp.equals("금")||temp.equals("토")||temp.equals("일"))) {
                        //set the selected date on the calendar.
                        ((CalendarActivity) mContext).parsingDate(temp2);
                    }
                }
            });
        }

    }

    private String makeLuna(int positionItem){
        String luna = List.getBaseDateInfoList().get(positionItem).getLuna();
        String[] lunas = new String[2];
        lunas[0] = luna.substring(0,2);
        if(lunas[0].startsWith("0")){lunas[0]=lunas[0].substring(1);}
        lunas[1]=luna.substring(2);
        luna = lunas[0] + "." + lunas[1];

        return luna;
    }

    @Override
    public int getItemViewType(int position) {

        if(((CalendarActivity)mContext).getmLunaChecker()){
            return LUNA;
        }else {
            return SOLAR;
        }

    }
}
