package com.devpilot.weatherkok.when.utils;

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

import com.devpilot.weatherkok.R;
import com.devpilot.weatherkok.when.CalendarActivity;
import com.devpilot.weatherkok.when.models.base.BaseDateInfoList;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

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

        //?????? ?????? ????????????
        int item = List.getBaseDateInfoList().get(positionItem).getDate();

        SharedPreferences pref = mContext.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

        //resultSplit[0]??????, [1] ???, [2] ???

        resultSplit = String.valueOf(mSelected).split("/");

        Calendar calendar = Calendar.getInstance();

        //????????? ??????
        calendar.set(Calendar.YEAR, Integer.parseInt(resultSplit[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(resultSplit[1]));
        calendar.set(Calendar.DATE, List.getBaseDateInfoList().get(positionItem).getDate());

        int seldate = Integer.parseInt(resultSplit[2]);

        //???????????? ???????????? ??????
        if(viewHolder instanceof ViewHolder) {

            //????????? ????????????, ????????? ?????? ?????? ??????, ????????? ????????? ??????, ????????? ?????? ?????????, ?????? ?????? ????????? ??? ?????? ?????? ?????????
            if (item == 200) {
                Log.i(TAG, "????????? ?????? for selected : " + positionItem + item + mSelected + "seldate : " + seldate);
                //**????????? ????????? ?????? ???????????? ????????? ??????????????? ????????????
                int x = 999;
                for (int i = 0; i < List.getBaseDateInfoList().size(); i++) {
                    if (List.getBaseDateInfoList().get(i).getDate() == seldate) {
                        x = i;
                        break;
                    }
                }
                //????????? ????????? ????????? ?????? ??? ???????????? ??????????????? ???????????????
                //??????????????? ??? ???????????? ?????? ??????

                //full span!!
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);

                //????????? ???????????? ??????
                //?????????? : ?????????, ?????????? : ?????????, ?????????

                String scheduleMemo;

                String friends = "";
                //?????? ???????????????, ????????? ?????????
                //?????????.. ?????? add "" ????????? ??????
                if(List.getBaseDateInfoList().get(x) != null) {
                    if (List.getBaseDateInfoList().get(x).getSchedule() != null) {
                        if (List.getBaseDateInfoList().get(x).getSchedule().getWho() != null) {
                            for (int i = 0; i < List.getBaseDateInfoList().get(x).getSchedule().getWho().size(); i++) {
                                friends += List.getBaseDateInfoList().get(x).getSchedule().getWho().get(i);
                                //???????????? ?????? ??????
                                if ((i + 1) != List.getBaseDateInfoList().get(x).getSchedule().getWho().size()) {
                                    friends += ", ";
                                }
                            }
                        }
                    }
                }
                //????????? ????????? ????????? ?????? ??????
                if (List.getBaseDateInfoList().get(x).getSchedule() != null) {
                    if (List.getBaseDateInfoList().get(x).getSchedule().getWhere() != null) {
                        scheduleMemo = "????????? : " + List.getBaseDateInfoList().get(x).getSchedule().getWhere();
                        ((ViewHolder) viewHolder).tv.setText(scheduleMemo);
                    } else {
                        scheduleMemo = "????????? : ";
                        ((ViewHolder) viewHolder).tv.setText(scheduleMemo);
                    }
                }
                //???????????? ?????? ??? ?????? ??????
                if (List.getBaseDateInfoList().get(x).getSchedule() == null) {
                    String luna = makeLuna(x);
                    if (List.getBaseDateInfoList().get(x).getNameOfDay() != null) {
                        String holiday = List.getBaseDateInfoList().get(x).getNameOfDay();
                        //????????? ?????????
                        ((ViewHolder) viewHolder).tv.setText("?????? : " + luna + ", " + holiday);
                    } else {

                        ((ViewHolder) viewHolder).tv.setText("?????? : " + luna);
                    }
                }
                //?????? ????????????
            } else if (item == 100 || item == 101 || item == 102 || item == 103 || item == 104 || item == 105 || item == 106 || item == 99) {
                Log.i(TAG, "?????? : " + item);
                switch (item) {
                    case 100: {
                        ((ViewHolder) viewHolder).tv.setText("???");
                        ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                        break;
                    }

                    case 101: {
                        ((ViewHolder) viewHolder).tv.setText("???");
                        break;
                    }
                    case 102: {
                        ((ViewHolder) viewHolder).tv.setText("???");
                        break;
                    }
                    case 103: {
                        ((ViewHolder) viewHolder).tv.setText("???");
                        break;
                    }
                    case 104: {
                        ((ViewHolder) viewHolder).tv.setText("???");
                        break;
                    }
                    case 105: {
                        ((ViewHolder) viewHolder).tv.setText("???");
                        break;
                    }
                    case 106: {
                        ((ViewHolder) viewHolder).tv.setText("???");
                        ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.purple_700));
                        break;
                    }
                    case 99: {
                        ((ViewHolder) viewHolder).tv.setText("");
                        break;
                    }
                }

            }
            //????????? ?????? ????????????,
            else if ((List.getBaseDateInfoList().get(positionItem).getSchedule()) != null
                    && (List.getBaseDateInfoList().get(positionItem).getSchedule().getWhere()) != null
                    && !(List.getBaseDateInfoList().get(positionItem).getSchedule().getWhere()).equals("")) {
                Log.i(TAG, "schedule dot" + item + "???");
                //item = item.substring(1);
                ((ViewHolder) viewHolder).tv.setText(String.valueOf(item));
                ((ViewHolder) viewHolder).tvScheduled.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.selected_dot));

                //???????????? -> ????????? ?????? ????????? ????????? ????????? ????????? ???
                if (List.getBaseDateInfoList().get(positionItem).getLuna() != null
                        && !List.getBaseDateInfoList().get(positionItem).getLuna().equals("")) {
                    String luna = makeLuna(positionItem);
                    ((ViewHolder) viewHolder).tvLuna.setText(luna);
                }

                int day = calendar.get(Calendar.DAY_OF_WEEK);
                if (day == 1) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                }

                //????????? ??????
                if (day == 7) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.purple_700));
                }

                //????????? ??????
                if (!TextUtils.isEmpty(List.getBaseDateInfoList().get(positionItem).getNameOfDay())) {
                ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                }
                Drawable today = mContext.getResources().getDrawable(R.drawable.bg_btn_yellow);
                //?????? ?????? black
                if (mToday.equals(String.valueOf(item))) { //?????? day ????????? ?????? ??????
                    ((ViewHolder) viewHolder).tv.setBackground(today);
                }

                if (seldate == List.getBaseDateInfoList().get(positionItem).getDate()) {
                    ((ViewHolder) viewHolder).tv.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.bg_selected_calendar_day));
                }
                //???????????? ????????? ????????? ??????
            } else if (seldate == List.getBaseDateInfoList().get(positionItem).getDate()) {
                //?????? ????????????
                ((ViewHolder) viewHolder).tv.setText(String.valueOf(item));

                //?????? ?????? black
                if (mToday.equals(String.valueOf(item))) { //?????? day ????????? ?????? ??????
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                //???????????? -> ????????? ?????? ????????? ????????? ????????? ????????? ???
                if (List.getBaseDateInfoList().get(positionItem).getLuna() != null
                        && !List.getBaseDateInfoList().get(positionItem).getLuna().equals("")) {
                    ((ViewHolder) viewHolder).tvLuna.setText(makeLuna(positionItem));
                }
                //????????? ??????
                if (!TextUtils.isEmpty(List.getBaseDateInfoList().get(positionItem).getNameOfDay())) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                }
                //??????????????? ??????
                Log.i(TAG, "selected date");
                ((ViewHolder) viewHolder).tv.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.bg_selected_calendar_day));

            } else {
                //???????????? ?????? ????????? ????????? ????????????
                Log.i(TAG, "works");
                //?????? ??????????????? ??????
                ((ViewHolder) viewHolder).tv.setText(String.valueOf(item));

                //?????? ?????? black
                if (mToday.equals(item)) { //?????? day ????????? ?????? ??????
                    ((ViewHolder)viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                //???????????? -> ????????? ?????? ????????? ????????? ????????? ????????? ???
                if (List.getBaseDateInfoList().get(positionItem).getLuna() != null
                        && !List.getBaseDateInfoList().get(positionItem).getLuna().equals("")) {
                    ((ViewHolder) viewHolder).tvLuna.setText(makeLuna(positionItem));
                }

                //????????? ??????
                calendar.set(Calendar.YEAR, Integer.parseInt(resultSplit[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(resultSplit[1]) - 1);
                calendar.set(Calendar.DATE, List.getBaseDateInfoList().get(positionItem).getDate());

                int day = calendar.get(Calendar.DAY_OF_WEEK);
                if (day == 1) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                }

                //????????? ??????
                if (day == 7) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.purple_700));
                }
                //????????? ??????
                if (!TextUtils.isEmpty(List.getBaseDateInfoList().get(positionItem).getNameOfDay())) {
                    ((ViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                }
            }
            //?????? ??????
        }else {

            //????????? ????????????, ????????? ?????? ?????? ??????, ????????? ????????? ??????, ????????? ?????? ?????????, ?????? ?????? ????????? ??? ?????? ?????? ?????????
            if (item == 200) {
                Log.i(TAG, "????????? ?????? for selected : " + positionItem + item + mSelected + "seldate : " + seldate);
                //**????????? ????????? ?????? ???????????? ????????? ??????????????? ????????????
                int x = 999;
                for (int i = 0; i < List.getBaseDateInfoList().size(); i++) {
                    if (List.getBaseDateInfoList().get(i).getDate() == seldate) {
                        x = i;
                        break;
                    }
                }
                //????????? ????????? ????????? ?????? ??? ???????????? ??????????????? ???????????????
                //??????????????? ??? ???????????? ?????? ??????

                //full span!!
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);

                //????????? ???????????? ??????
                //?????????? : ?????????, ?????????? : ?????????, ?????????

                String scheduleMemo;

                String friends = "";
                //?????? ???????????????, ????????? ?????????
                //?????????.. ?????? add "" ????????? ??????
                if (List.getBaseDateInfoList().get(x).getSchedule() != null) {
                    if (List.getBaseDateInfoList().get(x).getSchedule().getWho() != null) {
                        for (int i = 0; i < List.getBaseDateInfoList().get(x).getSchedule().getWho().size(); i++) {
                            friends += List.getBaseDateInfoList().get(x).getSchedule().getWho().get(i);
                            //???????????? ?????? ??????
                            if ((i + 1) != List.getBaseDateInfoList().get(x).getSchedule().getWho().size()) {
                                friends += ", ";
                            }
                        }
                    }
                }
                //????????? ????????? ????????? ?????? ??????
                if (List.getBaseDateInfoList().get(x).getSchedule() != null) {
                    if (List.getBaseDateInfoList().get(x).getSchedule().getWhere() != null) {
                        scheduleMemo = "????????? : " + List.getBaseDateInfoList().get(x).getSchedule().getWhere();
                        ((SolarViewHolder) viewHolder).tv.setText(scheduleMemo);
                    } else {
                        scheduleMemo = "????????? : ";
                        ((SolarViewHolder) viewHolder).tv.setText(scheduleMemo);
                    }
                }
                //???????????? ?????? ??? ?????? ??????
                if (List.getBaseDateInfoList().get(x).getSchedule() == null) {
                    String luna = makeLuna(x);
                    if (List.getBaseDateInfoList().get(x).getNameOfDay() != null) {
                        String holiday = List.getBaseDateInfoList().get(x).getNameOfDay();
                        //????????? ?????????
                        ((SolarViewHolder) viewHolder).tv.setText("?????? : " + luna + ", " + holiday);
                    } else {
                        ((SolarViewHolder) viewHolder).tv.setText("?????? : " + luna);
                    }
                }
                //?????? ????????????
            } else if (item == 100 || item == 101 || item == 102 || item == 103 || item == 104 || item == 105 || item == 106 || item == 99) {
                Log.i(TAG, "?????? : " + item);
                switch (item) {
                    case 100: {
                        ((SolarViewHolder) viewHolder).tv.setText("???");
                        ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
                        break;
                    }

                    case 101: {
                        ((SolarViewHolder) viewHolder).tv.setText("???");
                        break;
                    }
                    case 102: {
                        ((SolarViewHolder) viewHolder).tv.setText("???");
                        break;
                    }
                    case 103: {
                        ((SolarViewHolder) viewHolder).tv.setText("???");
                        break;
                    }
                    case 104: {
                        ((SolarViewHolder) viewHolder).tv.setText("???");
                        break;
                    }
                    case 105: {
                        ((SolarViewHolder) viewHolder).tv.setText("???");
                        break;
                    }
                    case 106: {
                        ((SolarViewHolder) viewHolder).tv.setText("???");
                        ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.purple_700));
                        break;
                    }
                    case 99: {
                        ((SolarViewHolder) viewHolder).tv.setText("");
                        break;
                    }
                }

            }
            //????????? ?????? ????????????,
            else if ((List.getBaseDateInfoList().get(positionItem).getSchedule()) != null
                    && (List.getBaseDateInfoList().get(positionItem).getSchedule().getWhere()) != null
                    && !(List.getBaseDateInfoList().get(positionItem).getSchedule().getWhere()).equals("")) {
                Log.i(TAG, "schedule dot" + item + "???");
                //item = item.substring(1);
                ((SolarViewHolder) viewHolder).tv.setText(String.valueOf(item));
                ((SolarViewHolder) viewHolder).tvScheduled.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.selected_dot));

                setTextColor(viewHolder, positionItem);

                //?????? ?????? black
                if (mToday.equals(String.valueOf(item))) { //?????? day ????????? ?????? ??????
                    ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                if (seldate == List.getBaseDateInfoList().get(positionItem).getDate()) {
                    ((SolarViewHolder) viewHolder).tv.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.bg_selected_calendar_day));
                }
                //???????????? ????????? ????????? ??????
            } else if (seldate == List.getBaseDateInfoList().get(positionItem).getDate()) {
                //?????? ????????????
                ((SolarViewHolder) viewHolder).tv.setText(String.valueOf(item));

                //?????? ?????? black
                if (mToday.equals(String.valueOf(item))) { //?????? day ????????? ?????? ??????
                    ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                setTextColor(viewHolder, positionItem);

//                //????????? ??????
//                if (!TextUtils.isEmpty(List.getBaseDateInfoList().get(positionItem).getNameOfDay())) {
//                    ((YearRecyclerViewAdapter.SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
//                }
                //??????????????? ??????
                Log.i(TAG, "selected date");
                ((SolarViewHolder) viewHolder).tv.setBackground((Drawable) mContext.getResources().getDrawable(R.drawable.bg_selected_calendar_day));

            } else {
                //???????????? ?????? ????????? ????????? ????????????
                Log.i(TAG, "works");
                //?????? ??????????????? ??????
                ((SolarViewHolder) viewHolder).tv.setText(String.valueOf(item));

                //?????? ?????? black
                if (mToday.equals(item)) { //?????? day ????????? ?????? ??????
                    ((SolarViewHolder)viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.black));
                }

                setTextColor(viewHolder, positionItem);

            }
        }

    }


    private void setTextColor(RecyclerView.ViewHolder viewHolder,int positionItem){


        Calendar calendar = Calendar.getInstance();

        //????????? ??????
        calendar.set(Calendar.YEAR, Integer.parseInt(resultSplit[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(resultSplit[1]) - 1);
        calendar.set(Calendar.DATE, List.getBaseDateInfoList().get(positionItem).getDate());

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) {
            ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.red));
        }

        //????????? ??????
        if (day == 7) {
            ((SolarViewHolder) viewHolder).tv.setTextColor(mContext.getResources().getColor(R.color.purple_700));
        }

        //????????? ??????
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
                    //preference ?????? ??????
                    SharedPreferences pref = context.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    //preference??? ??????

                    String date = pref.getString("when", "");
                    Log.i("when should be y/m : ", date);

                    //?????? ???????????? ????????? ???????????? ????????????
                    String temp = tv.getText().toString();

                    //?????? ?????????????????? ?????? 0 ?????????
                    if (temp.length() < 2) {
                        temp = "0" + temp;
                    }

                    date = date + "/" + temp;
                    editor.putString("when", date);
                    mYMD = date;
                    // ???????????? ?????? ???????????? ??????????????? ????????????.
                    editor.commit();

                    String temp2 = pref.getString("when", "");

                    Log.i(TAG, "from the preference : " + temp2);
                    if(!(temp.equals("???")||temp.equals("???")||temp.equals("???")||temp.equals("???")||
                            temp.equals("???")||temp.equals("???")||temp.equals("???"))) {
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
                    //preference ?????? ??????
                    SharedPreferences pref = context.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    // key?????? value?????? ????????????.
                    // String, boolean, int, float, long ??? ?????? ??????????????????.
                    //????????? ?????? ????????? ?????? ???????????????,
                    //preference??? ??????

                    String date = pref.getString("when", "");
                    Log.i("when should be y/m : ", date);

                    //?????? ???????????? ????????? ???????????? ????????????
                    String temp = tv.getText().toString();

                    //?????? ?????????????????? ?????? 0 ?????????
                    if (temp.length() < 2) {
                        temp = "0" + temp;
                    }

                    date = date + "/" + temp;
                    editor.putString("when", date);
                    mYMD = date;
                    // ???????????? ?????? ???????????? ??????????????? ????????????.
                    editor.commit();

                    String temp2 = pref.getString("when", "");

                    Log.i(TAG, "from the preference : " + temp2);
                    if(!(temp.equals("???")||temp.equals("???")||temp.equals("???")||temp.equals("???")||
                            temp.equals("???")||temp.equals("???")||temp.equals("???"))) {
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
