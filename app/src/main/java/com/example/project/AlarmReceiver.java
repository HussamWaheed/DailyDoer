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
        String title = intent.getStringExtra("title");
        String time = intent.getStringExtra("time");

        //create notification channel if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "TASK_REMINDER_CHANNEL",
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifies you when a task is due");
            //get the systems notification manager and register the notification channel
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //create the intent to open the dashboard when the notification is clicked
        Intent openDashboard = new Intent(context, listActivity.class);
        openDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //wrap intent in pendingIntent so it can be launched later
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openDashboard,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        //create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TASK_REMINDER_CHANNEL")
                .setSmallIcon(R.drawable.dd_logo)
                .setContentTitle(title)
                .setContentText("Today, " + time)
                .setPriority(NotificationCompat.PRIORITY_HIGH)//set priority
                .setContentIntent(pendingIntent)//opens dashboard
                .setAutoCancel(true)//automatically dismisses the notification when clicked
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//make visible on lock screen
                .setFullScreenIntent(pendingIntent, true);


        //check if app ahs permission to post notifications
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //if granted, display notification
        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
    }
}
