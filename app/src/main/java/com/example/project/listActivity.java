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

public class listActivity extends AppCompatActivity implements calendarPage_Adapter.TaskDeleteListener {
    RecyclerView recyclerView;
    ImageButton image_btn;
    TextView title;
    FloatingActionButton fab_add;

    ArrayList<dataSets> dataSet = new ArrayList<>();
    private dbhelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        recyclerView = findViewById(R.id.recyclerView1);
        image_btn = findViewById(R.id.image_btn);
        fab_add = findViewById(R.id.fab_add);
        title = findViewById(R.id.title);

        Intent intent = getIntent();

        // Changed to class variable initialization
        database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        Cursor cursor = database.displayData("false");

        // Check if the intent came from the "complete" page
        if (intent.getStringExtra("page") != null && intent.getStringExtra("page").equals("complete")) {
            title.setText("Complete Tasks");
            cursor = database.complete_displayData("true");
        }

        // Display tasks from the database
        if (cursor.getCount() == 0) {
            // No tasks to display
        } else {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);  // 'id' as int
                String ti = cursor.getString(1);
                String de = cursor.getString(2);
                String da = cursor.getString(3);
                String tm = cursor.getString(4);
                String status = cursor.getString(5);
                String importance = cursor.getString(6);

                // Pass 'id' as int to the dataSets constructor
                dataSet.add(new dataSets(id, ti, de, da, tm, importance));
            }
        }

        if (dataSet.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No Tasks!", Toast.LENGTH_SHORT).show();
        }

        // Sort the task list by date and time
        Collections.sort(dataSet, new Comparator<dataSets>() {
            @Override
            public int compare(dataSets d1, dataSets d2) {
                if (d1.getDate().compareToIgnoreCase(d2.getDate()) == 0) {
                    return d1.getTime().compareToIgnoreCase(d2.getTime());
                } else {
                    return d1.getDate().compareToIgnoreCase(d2.getDate());
                }
            }
        });

        cursor.close();

        // Pass 'this' as listener now we implement the interface
        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, this);
        recyclerView.setAdapter(myAdapter);

        // When image button is clicked, go back to the dashboard
        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard = new Intent(listActivity.this, dashboardActivity.class);
                startActivity(dashboard);
                //finish();
            }
        });

        // When floating action button is clicked, jump to the add_task page
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPage = new Intent(listActivity.this, addActivity.class);
                addPage.putExtra("page", "list");
                startActivity(addPage);
                //finish();
            }
        });
    }

    // Handle task deletion from adapter
    @Override
    public void onDeleteTask(int taskId) {
        // Debug: Check if task exists before deletion
        boolean existsBefore = database.taskExists(taskId);
        Log.d("DELETION", "Task exists before delete: " + existsBefore);

        try {
            // Try direct deletion first
            if (database.deleteTask(taskId)) {
                refreshTaskList();
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
            }
            // If that fails, try archiving then deleting
            else if (database.moveToDeletedTasks(taskId) && database.deleteTask(taskId)) {
                refreshTaskList();
                Toast.makeText(this, "Task archived and deleted", Toast.LENGTH_SHORT).show();
            } else {
                // Debug why it failed
                boolean existsAfter = database.taskExists(taskId);
                Log.d("DELETION", "Task exists after delete attempts: " + existsAfter);
                Toast.makeText(this, "Delete failed - task still exists", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("DELETION", "Error during deletion", e);
            Toast.makeText(this, "Delete error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Refresh the task list after deletion
    private void refreshTaskList() {
        dataSet.clear();
        Cursor cursor = database.displayData("false");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0); // Get _id from database
                String ti = cursor.getString(1);
                String de = cursor.getString(2);
                String da = cursor.getString(3);
                String tm = cursor.getString(4);
                dataSet.add(new dataSets(String.valueOf(id), ti, de, da, tm));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Sort the updated task list by date and time
        Collections.sort(dataSet, new Comparator<dataSets>() {
            @Override
            public int compare(dataSets d1, dataSets d2) {
                if (d1.getDate().compareToIgnoreCase(d2.getDate()) == 0) {
                    return d1.getTime().compareToIgnoreCase(d2.getTime());
                } else {
                    return d1.getDate().compareToIgnoreCase(d2.getDate());
                }
            }
        });
    }
}

