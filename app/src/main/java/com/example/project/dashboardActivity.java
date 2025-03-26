package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class dashboardActivity extends AppCompatActivity {
    Button add_btn, complete_btn, calendar_btn, all_btn;
    Button focus_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        add_btn = findViewById(R.id.add_btn);
        complete_btn = findViewById(R.id.complete_btn);
        calendar_btn = findViewById(R.id.calendar_btn);
        all_btn = findViewById(R.id.all_btn);
        focus_btn = findViewById(R.id.focus_btn);

        //jump to add task page
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPage = new Intent(dashboardActivity.this, addActivity.class);
                addPage.putExtra("page", "dash");
                startActivity(addPage);
                finish();
            }
        });
        //jump to calendar page
        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarPage = new Intent(dashboardActivity.this, calendarPage.class);
                startActivity(calendarPage);
                finish();
            }
        });

        //jump to list page
        all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listPage = new Intent(dashboardActivity.this, listActivity.class);
                listPage.putExtra("page", "list");
                startActivity(listPage);
                finish();
            }
        });

        //jump to complete task page
        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listPage = new Intent(dashboardActivity.this, listActivity.class);
                listPage.putExtra("page", "complete");
                startActivity(listPage);
                finish();
            }
        });

        //go to focus timer page
        focus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent focusPage = new Intent(dashboardActivity.this, FocusActivity.class);
                //intent.putExtra("page", "dash");
                focusPage.putExtra("page", "focus");
                startActivity(focusPage);
                finish();
            }
        });


    }
}
