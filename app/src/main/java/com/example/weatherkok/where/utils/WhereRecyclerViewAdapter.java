package com.example.weatherkok.where.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherkok.R;
import com.example.weatherkok.main.MainActivity;
import com.example.weatherkok.where.WhereActivity;
import com.example.weatherkok.where.WhereService;
import com.example.weatherkok.where.interfaces.WhereContract;
import com.example.weatherkok.where.interfaces.WhereRetrofitInterface;
import com.example.weatherkok.where.models.Record;
import com.example.weatherkok.where.models.Result;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class WhereRecyclerViewAdapter extends RecyclerView.Adapter<WhereRecyclerViewAdapter.Holder> {
    String TAG = "WhereRecycleViewAdapter";

    Context mContext;
    ArrayList<String> mWhereList;
    //Preference를 불러오기 위한 키
    public static String PREFERENCE_KEY = "WeatherKok.SharedPreference";

    public WhereRecyclerViewAdapter(ArrayList<String> mWhereList) {
        this.mWhereList = mWhereList;
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.where_contents, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WhereRecyclerViewAdapter.Holder holder, int position) {
        holder.tv.setText(mWhereList.get(position));
    }

    @Override
    public int getItemCount() {
        return mWhereList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position,boolean seJong);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tv;
        String where_from_holder;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_where_contents);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    where_from_holder = tv.getText().toString();
                    boolean seJong = false;

                    if(where_from_holder.startsWith("세종")){
                        seJong = true;
                    }

                    SharedPreferences pref = context.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);

                    // SharedPreferences 의 데이터를 저장/편집 하기위해 Editor 변수를 선언한다.
                    SharedPreferences.Editor editor = pref.edit();
                    // key값에 value값을 저장한다.
                    // String, boolean, int, float, long 값 모두 저장가능하다.
                    //기존에 있던 주소를 잠시 저장해두고,
                    String temp = pref.getString("where", "");
                    //이어붙인다.
                    if(temp!="") {
                        where_from_holder = temp + " " + where_from_holder;
                    }
                    editor.putString("where", where_from_holder);

                    // 메모리에 있는 데이터를 저장장치에 저장한다.
                    editor.commit();

                    String temp2 = pref.getString("where", where_from_holder);

                    Log.i(TAG, "from the preference : " + temp2);

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v,pos,seJong);
                    }

                }
            });

        }
    }

}
