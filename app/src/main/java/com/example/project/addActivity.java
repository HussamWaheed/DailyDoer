package com.example.project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class addActivity extends AppCompatActivity {
    EditText title, description;
    TextView topBar;
    DatePicker date;
    TimePicker time;
    Switch sw_btn;
    Button sub_btn, re_btn, back_btn; // New "Back" button
    ImageButton image_btn;  // Back arrow button
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
        // Initialize views
        title = findViewById(R.id.ed_title);
        description = findViewById(R.id.ed_description);
        date = findViewById(R.id.date_pick);
        time = findViewById(R.id.time_pick);
        sw_btn = findViewById(R.id.swbtn);
        sub_btn = findViewById(R.id.submit);
        re_btn = findViewById(R.id.reset);
        back_btn = findViewById(R.id.back_button);  // New "Back" button
        image_btn = findViewById(R.id.image_btn);  // The back arrow button
        topBar = findViewById(R.id.title);
        spinnerImportance = findViewById(R.id.spinner_importance);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.importance_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImportance.setAdapter(adapter);

        // **Set up the back button (image_btn)**
        image_btn.setOnClickListener(v -> {
            // Navigating to MainActivity without any animation
            Intent intent = new Intent(addActivity.this, dashboardActivity.class);
            startActivity(intent);
            finish(); // Finish current activity to prevent back press animation
        });

        // **Set up the "Back" button**
        back_btn.setOnClickListener(v -> {
            // Navigate to the main menu or home screen
            Intent intent = new Intent(addActivity.this, dashboardActivity.class); // Replace MainActivity.class with your main activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);  // Avoid transition animation
            startActivity(intent);
            finish();  // Finish the current activity
        });

        Intent intent = getIntent();
        boolean isUpdate = intent.getBooleanExtra("update", false);

        // Handle update logic if needed
        if (isUpdate) {
            topBar.setText("Update the Task");
            sub_btn.setText("Update");
            title.setText(intent.getStringExtra("title"));
            description.setText(intent.getStringExtra("description"));
            title.setEnabled(false);
        }

        dbhelper database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        // Handle switch for enabling/disabling the time picker
        sw_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            time.setEnabled(!isChecked);
        });

        // Handle task submission
        sub_btn.setOnClickListener(v -> {
            String title1 = title.getText().toString().trim();
            String desc = description.getText().toString().trim();
            String importance = spinnerImportance.getSelectedItem().toString();
            String date1 = String.format("%02d-%02d-%d", date.getMonth() + 1, date.getDayOfMonth(), date.getYear());
            String time1 = time.isEnabled() ? String.format("%02d:%02d", time.getHour(), time.getMinute()) : "All Day";

            if (title1.isEmpty()) {
                Toast.makeText(v.getContext(), "Please type a title!", Toast.LENGTH_LONG).show();
                return;
            }

            long result;
            if (isUpdate) {
                result = database.editItem(title1, desc, date1, time1, "false", importance);
                Toast.makeText(getApplicationContext(), result < 0 ? "Task is Not Updated!" : "Task is Updated!", Toast.LENGTH_LONG).show();
            } else {
                result = database.addItems(title1, desc, date1, time1, "false", importance);
                Toast.makeText(getApplicationContext(), result < 0 ? "Task is Not Added!" : "Task is Added!", Toast.LENGTH_LONG).show();
                if (result >= 0) {
                    scheduleTaskAlarm(addActivity.this, title1, date1, time1);
                }
            }

            // Reset the switch after task submission
            sw_btn.setChecked(false);
            time.setEnabled(true);

            // Finish the activity after submitting
            finish();
        });

        // Reset button logic
        re_btn.setOnClickListener(v -> {
            title.setText("");
            description.setText("");
        });
    }

    private void scheduleTaskAlarm(Context context, String title, String date, String time) {
        if (time.equals("All Day")) return;

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
        try {
            Date dateTime = sdf.parse(date + " " + time);
            if (dateTime != null) {
                long triggerTime = dateTime.getTime();
                long now = System.currentTimeMillis();

                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra("title", title);
                intent.putExtra("time", time);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        (int) System.currentTimeMillis(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}