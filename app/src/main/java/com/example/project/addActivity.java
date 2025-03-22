package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class addActivity extends AppCompatActivity {
    EditText title, description;
    DatePicker date;
    TimePicker time;
    Switch sw_btn;
    Button sub_btn, re_btn;
    ImageButton image_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);
        title = findViewById(R.id.ed_title);
        description = findViewById(R.id.ed_description);
        date = findViewById(R.id.date_pick);
        time = findViewById(R.id.time_pick);
        sw_btn = findViewById(R.id.swbtn);
        sub_btn = findViewById(R.id.submit);
        re_btn = findViewById(R.id.reset);
        image_btn = findViewById(R.id.image_btn);
        Intent intent = getIntent();

        dbhelper database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        sw_btn.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            time.setEnabled(false);

        }));
        sub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title1 = title.getText().toString();
                String desc = description.getText().toString();
                String date1 = String.format("%02d-%02d-%d", date.getMonth()+1, date.getDayOfMonth(), date.getYear());
                String time1 = "All Day";
                if (title1.isEmpty() || date1.isEmpty()){
                    Toast.makeText(v.getContext(), "Please type a title or select a date!", Toast.LENGTH_LONG).show();
                }else{
                    if (time.isEnabled()){
                        time1 = String.format("%02d:%02d", time.getHour(), time.getMinute());
                    }
                    long row = database.addItems(title1, desc, date1, time1);
                    if(row<0){
                        Toast.makeText(getApplicationContext(),"Task is Not Added!", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"Task is Added!", Toast.LENGTH_LONG).show();
                    }

                    if (intent.getStringExtra("add").equals("calendar")){
                        Intent calendarView = new Intent(addActivity.this, calendarPage.class);
                        startActivity(calendarView);
                    }else if (intent.getStringExtra("add").equals("dash")){
                        Intent dashBoardView = new Intent(addActivity.this, listActivity.class);
                        startActivity(dashBoardView);
                    }

                    finish();
                }
            }
        });

        re_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
                description.setText("");
            }
        });

        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.getStringExtra("add").equals("calendar")){
                    Intent calendarView = new Intent(addActivity.this, calendarPage.class);
                    startActivity(calendarView);
                }else if (intent.getStringExtra("add").equals("dash")){
                    Intent dashBoardView = new Intent(addActivity.this, dashboardActivity.class);
                    startActivity(dashBoardView);
                }
                finish();
            }
        });
    }
}
