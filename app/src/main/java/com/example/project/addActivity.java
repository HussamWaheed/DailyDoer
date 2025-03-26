package com.example.project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class addActivity extends AppCompatActivity {
    EditText title, description;

    TextView topBar;
    DatePicker date;
    TimePicker time;
    Switch sw_btn;
    Button sub_btn, re_btn;
    ImageButton image_btn;
    Spinner spinnerImportance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);
        //request SCHEDULE_EXACT_ALARM permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent); //will take user to the allow exact alarm setting
            }
        }

        title = findViewById(R.id.ed_title);
        description = findViewById(R.id.ed_description);
        date = findViewById(R.id.date_pick);
        time = findViewById(R.id.time_pick);
        sw_btn = findViewById(R.id.swbtn);
        sub_btn = findViewById(R.id.submit);
        re_btn = findViewById(R.id.reset);
        image_btn = findViewById(R.id.image_btn);
        topBar = findViewById(R.id.title);
        spinnerImportance = findViewById(R.id.spinner_importance);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.importance_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImportance.setAdapter(adapter);

        Intent intent = getIntent();
        //if update status is false means getIntent() from the adding actions such as, add buttons, otherwise, change texts to update, and set text for title and description.
        if (intent.getBooleanExtra("update",false)){
            topBar.setText("Update the Task");
            sub_btn.setText("Update");
            title.setText(intent.getStringExtra("title"));
            description.setText(intent.getStringExtra("description"));
            title.setEnabled(false);
        }
        //set database
        dbhelper database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        //when switch button is checked, means the task is all day without specific time line, set timepicker to false.
        sw_btn.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            time.setEnabled(false);

        }));
        //when submit button is clicked, submit all the content to the database and output it on the screen
        sub_btn.setOnClickListener(v -> {
            String title1 = title.getText().toString().trim();
            String desc = description.getText().toString().trim();
            String importance = spinnerImportance.getSelectedItem().toString();
            String date1 = String.format("%02d-%02d-%d", date.getMonth() + 1, date.getDayOfMonth(), date.getYear());
            String time1 = time.isEnabled() ? String.format("%02d:%02d", time.getHour(), time.getMinute()) : "All Day";

            if (title1.isEmpty() || date1.isEmpty()) {
                Toast.makeText(v.getContext(), "Please type a title or select a date!", Toast.LENGTH_LONG).show();
            } else {
                long result;
                if (intent.getBooleanExtra("update", false)) {
                    result = database.editItem(title1, desc, date1, time1, "false", importance);
                    if(result < 0){
                        Toast.makeText(getApplicationContext(),"Task is Not Updated!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Task is Updated!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    result = database.addItems(title1, desc, date1, time1, "false", importance);
                    if(result < 0){
                        Toast.makeText(getApplicationContext(),"Task is Not Added!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Task is Added!", Toast.LENGTH_LONG).show();
                        scheduleTaskAlarm(addActivity.this, title1, date1, time1); // set alarm after task is saved
                    }
                }

                // Reset the switch and handle its change listener
                sw_btn.setOnCheckedChangeListener((buttonView, isChecked) -> time.setEnabled(!isChecked));

                // Navigation logic after saving or updating the task
                if ("calendar".equals(intent.getStringExtra("page"))) {
                    startActivity(new Intent(addActivity.this, calendarPage.class));
                } else if ("dash".equals(intent.getStringExtra("page"))) {
                    startActivity(new Intent(addActivity.this, dashboardActivity.class));
                } else {
                    startActivity(new Intent(addActivity.this, listActivity.class));
                }
                finish(); // Ensure you finish the activity to clear it from the back stack
            }

        });


        //reset button, reset inputs
        re_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
                description.setText("");
            }
        });
    //impage button: go back to the last page
        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.getStringExtra("page").equals("calendar")){
                    Intent calendarView = new Intent(addActivity.this, calendarPage.class);
                    startActivity(calendarView);
                }else if (intent.getStringExtra("page").equals("dash")){
                    Intent dashBoardView = new Intent(addActivity.this, dashboardActivity.class);
                    startActivity(dashBoardView);
                }else{
                    Intent listView = new Intent(addActivity.this, listActivity.class);
                    startActivity(listView);
                }
                finish();
            }
        });
    }
    //schedule an alarm notification for a specific task based on its date and time
    private void scheduleTaskAlarm(Context context, String title, String date, String time) {
        if (time.equals("All Day")) {//skip if marked as all day
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
        try {
            //parse date time string into a Date object
            Date dateTime = sdf.parse(date + " " + time);
            if (dateTime != null) {
                long triggerTime = dateTime.getTime();
                long now = System.currentTimeMillis();//current system time

                //create an intent that will be broadcast when the alarm goes off
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra("title", title);
                intent.putExtra("time", time);


                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        (int) System.currentTimeMillis(), // unique ID per task to avoid overwrite
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                //schedule an alarm that goes of even if device is idle
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
