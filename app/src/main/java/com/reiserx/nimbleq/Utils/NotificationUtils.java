package com.reiserx.nimbleq.Utils;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.reiserx.nimbleq.Activities.ClassActivity;
import com.reiserx.nimbleq.Activities.MainActivity;
import com.reiserx.nimbleq.R;

import java.util.Map;


public class NotificationUtils {

    public void sendClassUpdat(Context context, String title, String content, int id) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel_id = "NimbleQ2";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Cloud messaging", NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("service");
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 5000, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notify_bulder = new NotificationCompat.Builder(context, channel_id);
        notify_bulder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentText(content)
                .setContentTitle(title)
                .setContentInfo("info");


        // Gets an instance of the NotificationManager service
        notificationManager.notify(id, notify_bulder.build());
    }

    public void sendClassUpdates(Context context, String title, String title1, String content, int id, String classID) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel_id = "NimbleQ1";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Class updates", NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("service");
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 5000, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(context, ClassActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(new Intent(context, MainActivity.class));
        stackBuilder.addNextIntent(intent);

        intent.putExtra("classID", classID);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notify_bulder = new NotificationCompat.Builder(context, channel_id);
        notify_bulder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentText(title1.concat("\n".concat(content)))
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentInfo("info")
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(title1.concat("\n".concat(content))));


        // Gets an instance of the NotificationManager service
        notificationManager.notify(id, notify_bulder.build());
    }
}
