package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class calendarPage extends AppCompatActivity {
    ArrayList<dataSets> dataSet = new ArrayList<>();
    RecyclerView recyclerView;
    CalendarView calendarView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);

        recyclerView = findViewById(R.id.recyclerView);
        calendarView = findViewById(R.id.calendarView);
        textView = findViewById(R.id.textView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        dataSet.add(new dataSets("Task 1", "3-21-2025", "1:00 - 3:00pm", "task 1"));
        dataSet.add(new dataSets("Task 2", "3-21-2025","10:00 - 11:30am","task 2"));
        dataSet.add(new dataSets("Task 2", "3-22-2025","10:00 - 11:30am","task 1"));
        dataSet.add(new dataSets("Task 3", "3-23-2025","8:15 - 3:00pm","task 1"));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                String date = (month+1)+"-"+day+"-"+year;
                ArrayList<dataSets> dateList = new ArrayList<>();
                for (dataSets data:dataSet){
                    if (data.getDate().equals(date)){
                        dateList.add(data);
                    }
                }
                calendarPage_Adapter myAdapter = new calendarPage_Adapter(dateList);
                myAdapter.setDataSet(dataSet);
                recyclerView.setAdapter(myAdapter);
            }
        });

    }
}
