package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class calendarPage extends AppCompatActivity implements calendarPage_Adapter.TaskDeleteListener {
    RecyclerView recyclerView;
    CalendarView calendarView;
    TextView textView;
    FloatingActionButton fab_add;
    ImageButton image_btn;
    dbhelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);

        recyclerView = findViewById(R.id.recyclerView);
        calendarView = findViewById(R.id.calendarView);
        textView = findViewById(R.id.textView);
        fab_add = findViewById(R.id.fab_addTask);
        image_btn = findViewById(R.id.image_btn);

        database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPage = new Intent(calendarPage.this, addActivity.class);
                addPage.putExtra("page", "calendar");
                startActivity(addPage);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                refreshTasksForDate(month, day, year);
            }
        });

        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard = new Intent(calendarPage.this, dashboardActivity.class);
                startActivity(dashboard);
            }
        });

        // Load tasks for current date initially
        Calendar calendar = Calendar.getInstance();
        refreshTasksForDate(
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR)
        );
    }

    private void refreshTasksForDate(int month, int day, int year) {
        ArrayList<dataSets> dataSet = new ArrayList<>();
        String date = String.format("%02d-%02d-%d", month+1, day, year);

        Cursor cursor = database.displayData("false");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);  // Get the task ID
                String ti = cursor.getString(1);
                String de = cursor.getString(2);
                String da = cursor.getString(3);
                String tm = cursor.getString(4);
                String importance = cursor.getString(6);

                if (da.equals(date)) {
                    dataSet.add(new dataSets(id, ti, de, da, tm, importance));
                }
            }
        }
        cursor.close();

        if(dataSet.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Task On the Date!", Toast.LENGTH_SHORT).show();
        }

        Collections.sort(dataSet, new Comparator<dataSets>() {
            @Override
            public int compare(dataSets d1, dataSets d2) {
                return d1.getTime().compareToIgnoreCase(d2.getTime());
            }
        });

        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, this);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        refreshTasksForDate(
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR)
        );
    }

    @Override
    public void onDeleteTask(int taskId) {
        // Debug: Check if task exists before deletion
        boolean existsBefore = database.taskExists(taskId);
        Log.d("DELETION", "Task exists before delete: " + existsBefore);

        try {
            // Try direct deletion first
            if (database.deleteTask(taskId)) {
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                // Refresh the task list
                Calendar calendar = Calendar.getInstance();
                refreshTasksForDate(
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.YEAR)
                );
            }
            // If that fails, try archiving then deleting
            else if (database.moveToDeletedTasks(taskId) && database.deleteTask(taskId)) {
                Toast.makeText(this, "Task archived and deleted", Toast.LENGTH_SHORT).show();
                // Refresh the task list
                Calendar calendar = Calendar.getInstance();
                refreshTasksForDate(
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.YEAR)
                );
            } else {
                boolean existsAfter = database.taskExists(taskId);
                Log.d("DELETION", "Task exists after delete attempts: " + existsAfter);
                Toast.makeText(this, "Delete failed", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("DELETION", "Error during deletion", e);
            Toast.makeText(this, "Delete error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
