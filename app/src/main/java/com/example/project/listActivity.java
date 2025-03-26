package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class listActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageButton image_btn;

    TextView title;

    FloatingActionButton fab_add;

    ArrayList<dataSets> dataSet = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        recyclerView = findViewById(R.id.recyclerView1);
        image_btn = findViewById(R.id.image_btn);
        fab_add = findViewById(R.id.fab_add);
        title = findViewById(R.id.title);
        Intent intent = getIntent();
        dbhelper database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        Cursor cursor = database.displayData("false");
        //if getExtra from complete page
        if (intent.getStringExtra("page")!= null && intent.getStringExtra("page").equals("complete")){
            title.setText("Complete Tasks");
            cursor = database.complete_displayData("true");
        }
        //display the tasks
        if (cursor.getCount() == 0) {
        } else {
            while (cursor.moveToNext()) {
                String ti = cursor.getString(1);
                String de = cursor.getString(2);
                String da = cursor.getString(3);
                String tm = cursor.getString(4);
                String status = cursor.getString(5);
                String importance = cursor.getString(6);
                dataSet.add(new dataSets(ti, de, da, tm, importance));
            }
        }
        if(dataSet.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Tasks!", Toast.LENGTH_SHORT).show();
        }
        //sort datalist to make sure the tasks is ordered by date and time.
        Collections.sort(dataSet, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                dataSets d1 = (dataSets) o1;
                dataSets d2 = (dataSets) o2;
                //compare the date and time, to make sure the time the tasks lists ascending by date and time
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
        //when image button is clicked, go back to the dashboard
        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboard = new Intent(listActivity.this, dashboardActivity.class);
                startActivity(dashboard);
                finish();
            }
        });

        //when floating action button is clicked, jump to add_task page
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPage = new Intent(listActivity.this, addActivity.class);
                addPage.putExtra("page", "list");
                startActivity(addPage);
                finish();
            }
        });

    }
}
