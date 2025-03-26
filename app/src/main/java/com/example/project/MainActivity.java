package com.example.project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.image);
        //insert a gif file as the logo, and Glide is external library
        Glide.with(this).load(R.drawable.logo_gif).into(image);

        //used to delay the action that let welcomepage holds 3 sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, dashboardActivity.class);
                startActivity(intent);
            }
        },3000);
        //create notification channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "TASK_REMINDER_CHANNEL",
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifies you when a task is due");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}