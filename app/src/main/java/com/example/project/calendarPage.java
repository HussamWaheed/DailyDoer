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
    // UI Component Declarations
    RecyclerView recyclerView;       // For displaying task list
    CalendarView calendarView;       // For date selection
    TextView textView;               // For displaying information
    FloatingActionButton fab_add;    // Button to add new tasks
    ImageButton image_btn;          // Button for navigation
    dbhelper database;              // Database helper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);

        // Initialize UI components by finding their views
        recyclerView = findViewById(R.id.recyclerView);
        calendarView = findViewById(R.id.calendarView);
        textView = findViewById(R.id.textView);
        fab_add = findViewById(R.id.fab_addTask);
        image_btn = findViewById(R.id.image_btn);

        // Initialize database helper
        database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();  // Ensure database is accessible

        // Set up RecyclerView layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Set click listener for the add task button
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch add activity with context that we're coming from calendar page
                Intent addPage = new Intent(calendarPage.this, addActivity.class);
                addPage.putExtra("page", "calendar");
                startActivity(addPage);
            }
        });

        // Set listener for calendar date changes
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                // Refresh task list when a new date is selected
                refreshTasksForDate(month, day, year);
            }
        });

        // Set click listener for navigation button
        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to dashboard
                Intent dashboard = new Intent(calendarPage.this, dashboardActivity.class);
                startActivity(dashboard);
            }
        });

        // Load tasks for current date when activity starts
        Calendar calendar = Calendar.getInstance();
        refreshTasksForDate(
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR)
        );
    }

    /**
     * Refreshes the task list for the selected date
     * @param month The selected month (0-11)
     * @param day The selected day of month
     * @param year The selected year
     */
    private void refreshTasksForDate(int month, int day, int year) {
        ArrayList<dataSets> dataSet = new ArrayList<>();
        // Format date as MM-DD-YYYY
        String date = String.format("%02d-%02d-%d", month+1, day, year);

        // Get all incomplete tasks from database
        Cursor cursor = database.displayData("false");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Extract task details from cursor
                int id = cursor.getInt(0);  // Task ID
                String ti = cursor.getString(1);  // Title
                String de = cursor.getString(2);  // Description
                String da = cursor.getString(3);  // Date
                String tm = cursor.getString(4);  // Time
                String importance = cursor.getString(6);  // Importance level

                // Add to list if task matches selected date
                if (da.equals(date)) {
                    dataSet.add(new dataSets(id, ti, de, da, tm, importance));
                }
            }
        }
        cursor.close();  // Always close cursor when done

        // Show message if no tasks found for selected date
        if(dataSet.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Task On the Date!", Toast.LENGTH_SHORT).show();
        }

        // Sort tasks by time (earliest first)
        Collections.sort(dataSet, new Comparator<dataSets>() {
            @Override
            public int compare(dataSets d1, dataSets d2) {
                return d1.getTime().compareToIgnoreCase(d2.getTime());
            }
        });

        // Create and set adapter with sorted task list
        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, this);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        // Refresh task list when activity resumes (e.g., returning from add/edit)
        Calendar calendar = Calendar.getInstance();
        refreshTasksForDate(
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR)
        );
    }

    /**
     * Callback method for when a task is deleted from the adapter
     * @param taskId The ID of the task to delete
     */
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
            // If direct deletion fails, try archiving then deleting
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
                // If both methods fail, log and show error
                boolean existsAfter = database.taskExists(taskId);
                Log.d("DELETION", "Task exists after delete attempts: " + existsAfter);
                Toast.makeText(this, "Delete failed", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            // Log any exceptions during deletion
            Log.e("DELETION", "Error during deletion", e);
            Toast.makeText(this, "Delete error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
