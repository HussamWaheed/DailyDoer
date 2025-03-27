package com.example.project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

/**
 * MainActivity serves as the splash screen/launcher activity for the application.
 * Responsibilities:
 * - Displays app logo with animation
 * - Initializes notification channels
 * - Transitions to main dashboard after delay
 */
public class MainActivity extends AppCompatActivity {

    private ImageView image; // View for displaying animated logo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize logo image view
        image = findViewById(R.id.image);

        // Load and display animated GIF logo using Glide library
        // Glide handles memory-efficient GIF loading and playback
        Glide.with(this)
                .load(R.drawable.logo_gif)  // Load GIF resource
                .into(image);               // Display in ImageView

        // Create delayed transition to dashboard
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Launch dashboard activity after delay
                Intent intent = new Intent(MainActivity.this, dashboardActivity.class);
                startActivity(intent);

                // Optional: finish() to prevent returning to splash screen
                // finish();
            }
        }, 3000); // 3 second delay (3000 milliseconds)

        // Create notification channel for Android 8.0+ (Oreo)
        createNotificationChannel();
    }

    /**
     * Creates notification channel required for Android 8.0+
     * Channels are mandatory for displaying notifications on newer Android versions
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Configure channel properties
            NotificationChannel channel = new NotificationChannel(
                    "TASK_REMINDER_CHANNEL",  // Channel ID
                    "Task Reminders",           // User-visible name
                    NotificationManager.IMPORTANCE_DEFAULT  // Importance level
            );
            channel.setDescription("Notifies you when a task is due");

            // Register channel with system
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Optional: Clean up resources when leaving activity
        if (image != null) {
            Glide.with(this).clear(image);
        }
    }
}