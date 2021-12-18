package com.example.simpill;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Date;

public class PillAlarmReceiver extends BroadcastReceiver {

    Simpill simpill;
    AlarmSetter alarmSetter;
    DateTimeManager dateTimeManager;

    NotificationManagerCompat pillNotificationManagerCompat;

    private static final int alarmCodeForReminder = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!(intent.hasExtra(context.getString(R.string.pill_name)) && intent.hasExtra(context.getString(R.string.notification_id)))) {
            throw new UnknownError();
        }

        else {
            String pillName = intent.getStringExtra(context.getString(R.string.pill_name));
            int notificationCode = intent.getIntExtra(context.getString(R.string.notification_id), 0);

            PillDBHelper myDatabase = new PillDBHelper(context);
            alarmSetter = new AlarmSetter(context, pillName, notificationCode);
            dateTimeManager = new DateTimeManager();
            simpill = new Simpill();

            if (!myDatabase.getPillName(pillName).equals("null") || myDatabase.getPillName(pillName).equals(pillName)) {

                pillNotificationManagerCompat = NotificationManagerCompat.from(context);
                Notification pillReminderNotification;

                SharedPreferences permanentNotificationsPref= context.getSharedPreferences(Simpill.PERMANENT_NOTIFICATIONS_BOOLEAN, context.MODE_PRIVATE);
                Boolean permanentNotifications = permanentNotificationsPref.getBoolean(Simpill.USER_PERMANENT_NOTIFICATIONS, false);
                simpill.setUserPermanentNotifications(permanentNotifications);

                //Open Button for Notification
                Intent openMainIntent = new Intent(context, MainActivity.class);
                openMainIntent.putExtra(context.getString(R.string.notification_pill_name), pillName);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationCode, openMainIntent, PendingIntent.FLAG_IMMUTABLE);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH && simpill.getUserPermanentNotifications() && myDatabase.getIsTaken(pillName) == 0) {
                    pillReminderNotification = new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentText(context.getString(R.string.time_for_medication) + " " + pillName)
                            .setColor(500086)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.time_for_medication) + " " + pillName))
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVibrate(new long[]{100, 300, 500, 300})
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setOngoing(true)
                            .setContentIntent(pendingIntent)
                            .addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                            .build();
                }
                else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH && simpill.getUserPermanentNotifications() && myDatabase.getIsTaken(pillName) == 0) {
                    pillReminderNotification = new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                            .setSmallIcon(R.drawable.pill_bottle_color_2)
                            .setContentText(context.getString(R.string.time_for_medication) + " " + pillName)
                            .setColor(500086)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.time_for_medication) + " " + pillName))
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVibrate(new long[]{100, 300, 500, 300})
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setOngoing(true)
                            .setContentIntent(pendingIntent)
                            .addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                            .build();
                }
                else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH && !simpill.getUserPermanentNotifications() && myDatabase.getIsTaken(pillName) == 0) {
                    pillReminderNotification = new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentText(context.getString(R.string.time_for_medication) + " " + pillName)
                            .setColor(500086)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.time_for_medication) + " " + pillName))
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVibrate(new long[]{100, 300, 500, 300})
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setOngoing(false)
                            .setContentIntent(pendingIntent)
                            .addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                            .build();
                }
                else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH && !simpill.getUserPermanentNotifications() && myDatabase.getIsTaken(pillName) == 0) {
                    pillReminderNotification = new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                            .setSmallIcon(R.drawable.pill_bottle_color_2)
                            .setContentText(context.getString(R.string.time_for_medication) + " " + pillName)
                            .setColor(500086)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.time_for_medication) + " " + pillName))
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVibrate(new long[]{100, 300, 500, 300})
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setOngoing(false)
                            .setContentIntent(pendingIntent)
                            .addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                            .build();
                }
                else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH && myDatabase.getIsTaken(pillName) == 1) {
                    pillReminderNotification = new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentText(pillName + " already taken :)")
                            .setColor(500086)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(pillName + " already taken :)"))
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVibrate(new long[]{100, 300, 500, 300})
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setOngoing(false)
                            .setContentIntent(pendingIntent)
                            .addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                            .build();
                }
                else {
                    pillReminderNotification = new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                            .setSmallIcon(R.drawable.pill_bottle_color_2)
                            .setContentText(pillName + " already taken :)")
                            .setColor(500086)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(pillName + " already taken :)"))
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVibrate(new long[]{100, 300, 500, 300})
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setOngoing(false)
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
