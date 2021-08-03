package com.example.weatherkok.where.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherkok.R;

import java.util.ArrayList;

public class WhereRecyclerViewAdapter extends RecyclerView.Adapter<WhereRecyclerViewAdapter.Holder> {

    Context mContext;
    ArrayList<String> mWhereList;

    public WhereRecyclerViewAdapter(ArrayList<String> mWhereList) {
        this.mWhereList = mWhereList;
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
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


    class Holder extends RecyclerView.ViewHolder {
        TextView tv;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_where_contents);
        }
    }


}
