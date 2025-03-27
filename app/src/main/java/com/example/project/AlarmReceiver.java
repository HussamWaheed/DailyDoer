package com.example.project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract task details from the incoming intent
        String title = intent.getStringExtra("title");
        String time = intent.getStringExtra("time");

        // Create notification channel for Android Oreo and above (required for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define notification channel properties
            NotificationChannel channel = new NotificationChannel(
                    "TASK_REMINDER_CHANNEL",  // Channel ID
                    "Task Reminders",           // User-visible channel name
                    NotificationManager.IMPORTANCE_HIGH  // Importance level
            );
            channel.setDescription("Notifies you when a task is due");

            // Get system notification manager and register the channel
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Create intent to open the dashboard when notification is clicked
        Intent openDashboard = new Intent(context, dashboardActivity.class);
        // Set flags to create new task and clear any existing ones
        openDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create pending intent that will be triggered when notification is clicked
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,  // Request code (0 for unique intent)
                openDashboard,
                // Flags to update existing intent if it exists and make it immutable
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification with all properties
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TASK_REMINDER_CHANNEL")
                .setSmallIcon(R.drawable.dd_logo)  // Small icon shown in status bar
                .setContentTitle(title)             // Title of the notification
                .setContentText("Today, " + time)   // Content text with task time
                .setPriority(NotificationCompat.PRIORITY_HIGH)  // High priority for heads-up notification
                .setContentIntent(pendingIntent)    // What to do when notification is clicked
                .setAutoCancel(true);              // Automatically dismiss when clicked

        // Check if app has notification permission (required for Android 13+)
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;  // Exit if permission not granted
        }

        // Display the notification with a unique ID (using current time in milliseconds)
        NotificationManagerCompat.from(context).notify(
                (int) System.currentTimeMillis(),  // Unique notification ID
                builder.build()                  // Built notification object
        );
    }
}