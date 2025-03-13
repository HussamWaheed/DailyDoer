package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class calendarPage_Adapter extends RecyclerView.Adapter<calendarPage_Adapter.MyViewHolder> {

    ArrayList<dataSets> dateList;

    ArrayList<dataSets> dataSet;

    public calendarPage_Adapter(ArrayList<dataSets> dateList){
        this.dateList = dateList;
    }

    public void setDataSet(ArrayList<dataSets> dataSet){
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_todolist_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        dataSets data= dateList.get(position);
        holder.title.setText(data.getTitle());
        holder.date.setText(data.getDate());
        holder.duration.setText(data.getTime());
        holder.description.setText(data.getDescription());
        holder.checkBox.setText("Done");
        holder.linearLayout.setVisibility(View.VISIBLE);
        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (holder.checkBox.isChecked()){
                if (dataSet.contains(dateList.get(position))){
                    dataSet.remove(dateList.get(position));
                }
                dateList.remove(position);
                holder.linearLayout.setVisibility(View.GONE);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, dateList.size());

            }
        });

    }


    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title, date, duration, description;
        LinearLayout linearLayout;

        CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearlayout);
            title =itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            duration = itemView.findViewById(R.id.duration);
            description = itemView.findViewById(R.id.description);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

    }

}

