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
    // UI component declarations
    EditText title, description;
    TextView topBar;
    DatePicker date;
    TimePicker time;
    Switch sw_btn;
    Button sub_btn, re_btn, back_btn;
    ImageButton image_btn;
    Spinner spinnerImportance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);

        // Initialize all UI components by finding their views from the layout
        title = findViewById(R.id.ed_title);
        description = findViewById(R.id.ed_description);
        date = findViewById(R.id.date_pick);
        time = findViewById(R.id.time_pick);
        sw_btn = findViewById(R.id.swbtn);
        sub_btn = findViewById(R.id.submit);
        re_btn = findViewById(R.id.reset);
        back_btn = findViewById(R.id.back_button);
        image_btn = findViewById(R.id.image_btn);
        topBar = findViewById(R.id.title);
        spinnerImportance = findViewById(R.id.spinner_importance);

        // Set up the spinner (dropdown) for task importance levels
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.importance_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImportance.setAdapter(adapter);

        // Set click listener for the image button to navigate back to dashboard
        image_btn.setOnClickListener(v -> {
            Intent intent = new Intent(addActivity.this, dashboardActivity.class);
            startActivity(intent);
            finish(); // Close current activity
        });

        // Set click listener for back button to navigate back to dashboard without animation
        back_btn.setOnClickListener(v -> {
            Intent intent = new Intent(addActivity.this, dashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        });

        // Check if this activity was launched for updating an existing task
        Intent intent = getIntent();
        boolean isUpdate = intent.getBooleanExtra("update", false);

        // If in update mode, populate fields with existing task data
        if (isUpdate) {
            topBar.setText("Update the Task");
            sub_btn.setText("Update");
            title.setText(intent.getStringExtra("title"));
            description.setText(intent.getStringExtra("description"));
            title.setEnabled(false); // Disable title editing during update
        }

        // Initialize database helper
        dbhelper database = new dbhelper(getApplicationContext());
        database.getReadableDatabase();

        // Set listener for the switch button to toggle time picker availability
        sw_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            time.setEnabled(!isChecked); // Disable time picker when switch is on (All Day)
        });

        // Set click listener for submit/update button
        sub_btn.setOnClickListener(v -> {
            // Get all input values
            String title1 = title.getText().toString().trim();
            String desc = description.getText().toString().trim();
            String importance = spinnerImportance.getSelectedItem().toString();
            String date1 = String.format("%02d-%02d-%d", date.getMonth() + 1, date.getDayOfMonth(), date.getYear());
            String time1 = time.isEnabled() ? String.format("%02d:%02d", time.getHour(), time.getMinute()) : "All Day";

            // Validate title is not empty
            if (title1.isEmpty()) {
                Toast.makeText(v.getContext(), "Please type a title!", Toast.LENGTH_LONG).show();
                return;
            }

            // Perform database operation based on update or add mode
            long result;
            if (isUpdate) {
                result = database.editItem(title1, desc, date1, time1, "false", importance);
                Toast.makeText(getApplicationContext(), result < 0 ? "Task is Not Updated!" : "Task is Updated!", Toast.LENGTH_LONG).show();
            } else {
                result = database.addItems(title1, desc, date1, time1, "false", importance);
                Toast.makeText(getApplicationContext(), result < 0 ? "Task is Not Added!" : "Task is Added!", Toast.LENGTH_LONG).show();
                if (result >= 0) {
                    // Schedule alarm only for new tasks with specific time
                    scheduleTaskAlarm(addActivity.this, title1, date1, time1);
                }
            }

            // Reset UI elements
            sw_btn.setChecked(false);
            time.setEnabled(true);

            // Close the activity
            finish();
        });

        // Set click listener for reset button to clear input fields
        re_btn.setOnClickListener(v -> {
            title.setText("");
            description.setText("");
        });
    }

    // Helper method to schedule an alarm for a task
    private void scheduleTaskAlarm(Context context, String title, String date, String time) {
        if (time.equals("All Day")) return; // No alarm needed for all-day tasks

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
        try {
            // Parse the date and time string into a Date object
            Date dateTime = sdf.parse(date + " " + time);
            if (dateTime != null) {
                long triggerTime = dateTime.getTime();
                long now = System.currentTimeMillis();

                // Create intent for the alarm receiver
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra("title", title);
                intent.putExtra("time", time);

                // Create pending intent for the alarm
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        (int) System.currentTimeMillis(), // Unique request code
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                // Schedule the exact alarm
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
