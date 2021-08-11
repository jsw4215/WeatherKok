package com.example.weatherkok.when.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.weatherkok.R;
import com.example.weatherkok.when.models.base.BaseDateInfoList;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class YearRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String PREFERENCE_KEY = "WeatherKok.SharedPreference";
    private static final String TAG = "RecyclerViewAdapter";
    private BaseDateInfoList List;
    String mYearMonth;
    Context mContext;


    public YearRecyclerViewAdapter(Context context, BaseDateInfoList List, String yearMonth) {
        Log.i(TAG, "Constructor");
        this.mContext = context;
        this.List = List;
        this.mYearMonth = yearMonth;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        Log.i(TAG, "onCreateViewHolder");
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_year_calendar, viewGroup, false);
        return new SolarViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int positionItem) {

        //양력 날짜 가져오기
        int item = List.getBaseDateInfoList().get(positionItem).getDate();

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        //resultSplit[0]년도, [1] 월, [2] 일

        Calendar calendar = Calendar.getInstance();

        //일요일 빨강
        calendar.set(Calendar.YEAR, Integer.parseInt(mYearMonth.substring(0,4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(mYearMonth.substring(4))-1);
        calendar.set(Calendar.DATE, List.getBaseDateInfoList().get(positionItem).getDate());

        //맨 첫번째는 월을 나타내는 숫자로 full Span으로 표시할 것
        if(positionItem==0){
            //full span!!
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);

            ((SolarViewHolder)viewHolder).tv.setText(String.valueOf(item)+"월");
            ((SolarViewHolder)viewHolder).tv.setTextSize(15);
           int max = ((SolarViewHolder)viewHolder).tv.getMaxWidth();
            ((SolarViewHolder)viewHolder).tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ((SolarViewHolder)viewHolder).tv.setGravity(Gravity.CENTER);
        }
        //달력의 메모부분, 스케쥴 있을 경우 표시, 일요일 토요일 색상, 선택된 날짜 배경색, 오늘 날짜 검은색 등 모든 조건 분기문
        else if (item == 100 || item == 101 || item == 102 || item == 103 || item == 104 || item == 105 || item == 106 || item == 99) {
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

        } else {
            //표시 방법
            Log.i(TAG, "works");
            //일자 텍스트뷰에 표시
            ((SolarViewHolder) viewHolder).tv.setText(String.valueOf(item));

            setTextColor(viewHolder, positionItem);
        }
    }

    private void setTextColor(RecyclerView.ViewHolder viewHolder,int positionItem){


        Calendar calendar = Calendar.getInstance();

        //일요일 빨강
        calendar.set(Calendar.YEAR, Integer.parseInt(mYearMonth.substring(0,4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(mYearMonth.substring(4))-1);
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

    public class SolarViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        TextView tvScheduled;

        SolarViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.tv_solarcal_item);
            tvScheduled = view.findViewById(R.id.tv_solarcal_item_scheduled);

        }

    }

}
