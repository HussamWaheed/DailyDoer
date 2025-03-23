package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class completeActivity extends AppCompatActivity {
    ImageButton image_btn;
    RecyclerView recyclerView;
    ArrayList<dataSets> dataSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_layout);
        image_btn = findViewById(R.id.image_btn);
        recyclerView = findViewById(R.id.recyclerView1);

        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard = new Intent(completeActivity.this, dashboardActivity.class);
                startActivity(dashboard);
                finish();
            }
        });

        dbhelper database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, completeActivity.this);
        recyclerView.setAdapter(myAdapter);

    }
}
