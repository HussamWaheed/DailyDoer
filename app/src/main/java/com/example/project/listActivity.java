package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



//Displays list of tasks completed or pending
    // Also implements task deletion activity
public class listActivity extends AppCompatActivity implements calendarPage_Adapter.TaskDeleteListener {

    // UI Components
    RecyclerView recyclerView;       // Displays the list of tasks
    ImageButton image_btn;          // Back navigation button
    TextView title;                 // Activity title text
    FloatingActionButton fab_add;   // Button to add new tasks

    // Data
    ArrayList<dataSets> dataSet = new ArrayList<>(); // List of tasks to display
    private dbhelper database;      // Database helper instance
    private boolean showCompleted;  // Flag to determine if showing completed tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView1);
        image_btn = findViewById(R.id.image_btn);
        fab_add = findViewById(R.id.fab_add);
        title = findViewById(R.id.title);

        // Determine if we should show completed or pending tasks
        Intent intent = getIntent();
        showCompleted = intent.getStringExtra("page") != null &&
                intent.getStringExtra("page").equals("complete");

        // Initialize database
        database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        // Set up RecyclerView layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Update title if showing completed tasks
        if (showCompleted) {
            title.setText("Complete Tasks");
        }

        // Load and display tasks
        refreshTaskList();

        // Show message if no tasks found
        if (dataSet.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No Tasks!", Toast.LENGTH_SHORT).show();
        }

        // Set up RecyclerView adapter
        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, this);
        recyclerView.setAdapter(myAdapter);

        // Back button returns to dashboard
        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard = new Intent(listActivity.this, dashboardActivity.class);
                startActivity(dashboard);
            }
        });

        // Add button launches add task activity
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPage = new Intent(listActivity.this, addActivity.class);
                addPage.putExtra("page", "list"); // Indicate we came from list view
                startActivity(addPage);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh task list when returning to activity
        refreshTaskList();
        // Update adapter with new data
        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, this);
        recyclerView.setAdapter(myAdapter);
    }



    // Callback for when a task is deleted.
    //uses taskID as primary ID
    @Override
    public void onDeleteTask(int taskId) {
        // Log pre-deletion state
        boolean existsBefore = database.taskExists(taskId);
        Log.d("DELETION", "Task exists before delete: " + existsBefore);

        try {
            // Attempt direct deletion first
            if (database.deleteTask(taskId)) {
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
            }
            // If that fails, try archiving then deleting
            else if (database.moveToDeletedTasks(taskId) && database.deleteTask(taskId)) {
                Toast.makeText(this, "Task archived and deleted", Toast.LENGTH_SHORT).show();
            } else {
                // Log and show error if both methods fail
                boolean existsAfter = database.taskExists(taskId);
                Log.d("DELETION", "Task exists after delete attempts: " + existsAfter);
                Toast.makeText(this, "Delete failed - task still exists", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            // Log any exceptions during deletion
            Log.e("DELETION", "Error during deletion", e);
            Toast.makeText(this, "Delete error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    //Sorts tasks by date and time and refreshes tasklist from database
    private void refreshTaskList() {
        dataSet.clear(); // Clear existing data

        // Get appropriate cursor based on completed/pending filter
        Cursor cursor = showCompleted ?
                database.displayData("true") :
                database.displayData("false");

        // Populate dataSet from cursor
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String ti = cursor.getString(1);
                String de = cursor.getString(2);
                String da = cursor.getString(3);
                String tm = cursor.getString(4);
                String importance = cursor.getString(6);

                dataSet.add(new dataSets(id, ti, de, da, tm, importance));
            } while (cursor.moveToNext());
        }

        // Always close cursor when done
        if (cursor != null) {
            cursor.close();
        }

        // Sort tasks by date, then by time
        Collections.sort(dataSet, new Comparator<dataSets>() {
            @Override
            public int compare(dataSets d1, dataSets d2) {
                // First compare dates
                int dateCompare = d1.getDate().compareToIgnoreCase(d2.getDate());
                if (dateCompare == 0) {
                    // If dates are equal, compare times
                    return d1.getTime().compareToIgnoreCase(d2.getTime());
                } else {
                    return dateCompare;
                }
            }
        });
    }
}
