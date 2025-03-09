package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class calendarPage_Adapter extends RecyclerView.Adapter<calendarPage_Adapter.MyViewHolder> {

    ArrayList<dataSets> dataSet;
    Context context;

    public calendarPage_Adapter(ArrayList<dataSets> dataSet){
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public calendarPage_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_todolist_layout, parent, false);
        return new calendarPage_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull calendarPage_Adapter.MyViewHolder holder, int position) {
        dataSets data = dataSet.get(position);
        holder.title.setText(data.getTitle());
        holder.date.setText(data.getDate());
        holder.duration.setText(data.getTime().toString());
        holder.description.setText(data.getDescription());
        holder.checkBox.setText("Done");
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
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

