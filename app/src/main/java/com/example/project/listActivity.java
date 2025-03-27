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
    private boolean showCompleted; // Add this class variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        recyclerView = findViewById(R.id.recyclerView1);
        image_btn = findViewById(R.id.image_btn);
        fab_add = findViewById(R.id.fab_add);
        title = findViewById(R.id.title);

        Intent intent = getIntent();
        showCompleted = intent.getStringExtra("page") != null &&
                intent.getStringExtra("page").equals("complete");

        database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (showCompleted) {
            title.setText("Complete Tasks");
        }

        refreshTaskList(); // Use the updated method

        if (dataSet.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No Tasks!", Toast.LENGTH_SHORT).show();
        }

        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, this);
        recyclerView.setAdapter(myAdapter);

        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard = new Intent(com.example.project.listActivity.this, dashboardActivity.class);
                startActivity(dashboard);
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPage = new Intent(com.example.project.listActivity.this, addActivity.class);
                addPage.putExtra("page", "list");
                startActivity(addPage);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTaskList();
        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, this);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onDeleteTask(int taskId) {
        boolean existsBefore = database.taskExists(taskId);
        Log.d("DELETION", "Task exists before delete: " + existsBefore);

        try {
            if (database.deleteTask(taskId)) {
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
            }
            else if (database.moveToDeletedTasks(taskId) && database.deleteTask(taskId)) {
                Toast.makeText(this, "Task archived and deleted", Toast.LENGTH_SHORT).show();
            } else {
                boolean existsAfter = database.taskExists(taskId);
                Log.d("DELETION", "Task exists after delete attempts: " + existsAfter);
                Toast.makeText(this, "Delete failed - task still exists", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("DELETION", "Error during deletion", e);
            Toast.makeText(this, "Delete error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void refreshTaskList() {
        dataSet.clear();
        Cursor cursor = showCompleted ? database.complete_displayData("true") : database.displayData("false");

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
        if (cursor != null) {
            cursor.close();
        }

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
