package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Date;

public class calendarPage extends AppCompatActivity {
    ArrayList<dataSets> dataSet = new ArrayList<>();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        dataSet.add(new dataSets("Task 1", "Mar.21st, 2025", "1:00 - 3:00pm", "task 1"));
        dataSet.add(new dataSets("Task 2", "Mar.22nd, 2025","10:00 - 11:30am","task 2"));
        dataSet.add(new dataSets("Task 3", "Mar.23rd, 2025","8:15 - 3:00pm","task 3"));

        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet);
        recyclerView.setAdapter(myAdapter);

    }
}
