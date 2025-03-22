package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class listActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageButton image_btn;
    ArrayList<dataSets> dataSet = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        recyclerView = findViewById(R.id.recyclerView1);
        image_btn = findViewById(R.id.image_btn);
        dbhelper database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        Cursor cursor = database.displayData();

        if (cursor.getCount() == 0) {
        } else {
            while (cursor.moveToNext()) {
                String ti = cursor.getString(1);
                String de = cursor.getString(2);
                String da = cursor.getString(3);
                String tm = cursor.getString(4);
                dataSet.add(new dataSets(ti, de, da, tm));
            }
        }
        if(dataSet.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Task On the Date!", Toast.LENGTH_SHORT).show();
        }
        //sort datalist to make sure the tasks is ordered by time.
        Collections.sort(dataSet, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                dataSets d1 = (dataSets) o1;
                dataSets d2 = (dataSets) o2;

                if (d1.getDate().compareToIgnoreCase(d2.getDate())==0){
                    return d1.getTime().compareToIgnoreCase(d2.getTime());
                }else{
                    return d1.getDate().compareToIgnoreCase(d2.getDate());
                }
            }
        });
        cursor.close();
        calendarPage_Adapter myAdapter = new calendarPage_Adapter(dataSet, listActivity.this);
        recyclerView.setAdapter(myAdapter);

        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard = new Intent(listActivity.this, dashboardActivity.class);
                startActivity(dashboard);
                finish();
            }
        });
    }
}
