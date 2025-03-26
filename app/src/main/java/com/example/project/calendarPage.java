package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class calendarPage extends AppCompatActivity {
    RecyclerView recyclerView;
    CalendarView calendarView;
    TextView textView;
    FloatingActionButton fab_add;

    ImageButton image_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);

        recyclerView = findViewById(R.id.recyclerView);
        calendarView = findViewById(R.id.calendarView);
        textView = findViewById(R.id.textView);
        fab_add = findViewById(R.id.fab_addTask);
        image_btn = findViewById(R.id.image_btn);
        Intent intent = getIntent();
        dbhelper database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        //set linearlayout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //when floating button is clicked, jump to add_task page
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPage = new Intent(calendarPage.this, addActivity.class);
                addPage.putExtra("page", "calendar");
                startActivity(addPage);
            }
        });

        //when pick date on calendar view, only get tasks on the date.
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                ArrayList<dataSets> dataSet = new ArrayList<>();
                //format the date string
                String date = String.format("%02d-%02d-%d",month+1, day, year);
                //output all the uncompleted tasks
                Cursor cursor = database.displayData("false");
                if (cursor.getCount() == 0) {
                } else {
                    while (cursor.moveToNext()) {
                        String ti = cursor.getString(1);
                        String de = cursor.getString(2);
                        String da = cursor.getString(3);
                        String tm = cursor.getString(4);
                        String status = cursor.getString(5);
                        String importance = cursor.getString(6);
                        if (da.equals(date)) {
                            dataSet.add(new dataSets(ti, de, da, tm, importance));
                        }
                    }
                }
                if(dataSet.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No Task On the Date!", Toast.LENGTH_SHORT).show();
                }
                //sort datalist ascending to make sure the tasks is ordered by time.
                Collections.sort(dataSet, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        dataSets d1 = (dataSets) o1;
                        dataSets d2 = (dataSets) o2;
                        return d1.getTime().compareToIgnoreCase(d2.getTime());
                    }
                });
                cursor.close();
                calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, calendarPage.this);
                recyclerView.setAdapter(myAdapter);
            }
        });
        //back to the last page
        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard = new Intent(calendarPage.this, dashboardActivity.class);
                startActivity(dashboard);
                finish();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        dbhelper database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();
        ArrayList<dataSets> dataSet = new ArrayList<>();
        Cursor cursor = database.displayData("false");
        long date = calendarView.getDate();
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(calendar.MONTH);
        int day = calendar.get(calendar.DAY_OF_MONTH);
        int year = calendar.get(calendar.YEAR);
        String date1 = String.format("%02d-%02d-%d",month+1, day, year);
        if (cursor.getCount() == 0) {
        } else {
            while (cursor.moveToNext()) {
                String ti = cursor.getString(1);
                String de = cursor.getString(2);
                String da = cursor.getString(3);
                String tm = cursor.getString(4);
                String status = cursor.getString(5);
                String importance = cursor.getString(6);
                if (da.equals(date1)) {
                    dataSet.add(new dataSets(ti, de, da, tm, importance));
                }
            }
        }
        cursor.close();
        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, calendarPage.this);
        recyclerView.setAdapter(myAdapter);
    }


}
