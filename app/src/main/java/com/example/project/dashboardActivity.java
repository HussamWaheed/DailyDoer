package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Main dashboard activity that serves as the central navigation hub
 * Provides buttons to access all major features of the application
 */
public class dashboardActivity extends AppCompatActivity {
    // UI Component Declarations
    Button add_btn;         // Button to add new tasks
    Button complete_btn;    // Button to view completed tasks
    Button calendar_btn;    // Button to access calendar view
    Button all_btn;         // Button to view all tasks
    Button focus_btn;       // Button to access focus timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);  // Set the layout for this activity

        // Initialize all buttons by finding their views
        add_btn = findViewById(R.id.add_btn);
        complete_btn = findViewById(R.id.complete_btn);
        calendar_btn = findViewById(R.id.calendar_btn);
        all_btn = findViewById(R.id.all_btn);
        focus_btn = findViewById(R.id.focus_btn);

        // Set click listener for Add Task button
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to launch addActivity
                Intent addPage = new Intent(dashboardActivity.this, addActivity.class);
                // Pass context information that we're coming from dashboard
                addPage.putExtra("page", "dash");
                startActivity(addPage);
                // Note: finish() is commented out to allow returning to dashboard
            }
        });

        // Set click listener for Calendar button
        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to launch calendar view
                Intent calendarPage = new Intent(dashboardActivity.this, calendarPage.class);
                startActivity(calendarPage);
            }
        });

        // Set click listener for All Tasks button
        all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to launch listActivity in "all tasks" mode
                Intent listPage = new Intent(dashboardActivity.this, listActivity.class);
                listPage.putExtra("page", "list");  // Indicate we want to see all tasks
                startActivity(listPage);
            }
        });

        // Set click listener for Completed Tasks button
        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to launch listActivity in "completed tasks" mode
                Intent listPage = new Intent(dashboardActivity.this, listActivity.class);
                listPage.putExtra("page", "complete");  // Filter for completed tasks
                startActivity(listPage);
            }
        });

        // Set click listener for Focus Timer button
        focus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to launch focus timer activity
                Intent focusPage = new Intent(dashboardActivity.this, FocusActivity.class);
                focusPage.putExtra("page", "focus");  // Indicate focus mode
                startActivity(focusPage);
            }
        });
    }
}