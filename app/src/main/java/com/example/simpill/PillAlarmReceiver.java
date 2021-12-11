package com.example.simpill;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Date;

public class PillAlarmReceiver extends BroadcastReceiver {

    NotificationManagerCompat pillNotificationManagerCompat;
    AlarmSetter alarmSetter;
    DateTimeManager dateTimeManager;
    private static final int alarmCodeForReminder = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        pillNotificationManagerCompat = NotificationManagerCompat.from(context);

        Notification pillReminderNotification;

        if (!(intent.hasExtra(context.getString(R.string.pill_name)) && intent.hasExtra(context.getString(R.string.notification_id)))) {
            throw new UnknownError();
        } else {
            String pillName = intent.getStringExtra(context.getString(R.string.pill_name));
            int notificationCode = intent.getIntExtra(context.getString(R.string.notification_id), 0);

            PillDBHelper myDatabase = new PillDBHelper(context);
            alarmSetter = new AlarmSetter(context, pillName, notificationCode);
            dateTimeManager = new DateTimeManager();

            if (!myDatabase.getPillName(pillName).equals("null") || myDatabase.getPillName(pillName).equals(pillName)) {
                Intent openMainIntent = new Intent(context, MainActivity.class);
                openMainIntent.putExtra(context.getString(R.string.notification_pill_name), pillName);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationCode, openMainIntent, PendingIntent.FLAG_IMMUTABLE);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                    pillReminderNotification = new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentText(context.getString(R.string.time_for_medication) + " " + pillName)
                            .setColor(500086)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.time_for_medication) + " " + pillName))
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVibrate(new long[]{100, 300, 500, 300})
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                            .build();
                } else {
                    pillReminderNotification = new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                            .setSmallIcon(R.drawable.pill_bottle_color_2)
                            .setContentText(context.getString(R.string.time_for_medication) + " " + pillName)
                            .setColor(500086)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.time_for_medication) + " " + pillName))
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVibrate(new long[]{100, 300, 500, 300})
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                            .build();
                }

                pillNotificationManagerCompat.notify(pillName, notificationCode, pillReminderNotification);
                alarmSetter.setAlarms(alarmCodeForReminder);
            }
        }
    }
}
