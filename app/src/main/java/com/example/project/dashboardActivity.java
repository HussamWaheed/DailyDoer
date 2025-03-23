package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class dashboardActivity extends AppCompatActivity {
    Button add_btn, complete_btn, calendar_btn, all_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        add_btn = findViewById(R.id.add_btn);
        complete_btn = findViewById(R.id.complete_btn);
        calendar_btn = findViewById(R.id.calendar_btn);
        all_btn = findViewById(R.id.all_btn);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPage = new Intent(dashboardActivity.this, addActivity.class);
                addPage.putExtra("add", "dash");
                startActivity(addPage);
                finish();
            }
        });

        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarPage = new Intent(dashboardActivity.this, calendarPage.class);
                startActivity(calendarPage);
                finish();
            }
        });

        all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listPage = new Intent(dashboardActivity.this, listActivity.class);
                startActivity(listPage);
                finish();
            }
        });

        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listPage = new Intent(dashboardActivity.this, completeActivity.class);
                startActivity(listPage);
                finish();
            }
        });

    }
}
