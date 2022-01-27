package com.example.simpill;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class PillSupplyReceiver extends BroadcastReceiver {

    private static final long MONTH_IN_MS = 2629800000L;
    private static final int alarmCodeForSupply = 3;

    NotificationManagerCompat stockupNotificationManagerCompat;
    PillDBHelper myDatabase;
    DateTimeManager dateTimeManager;
    AlarmSetter alarmSetter;

    @Override
    public void onReceive(Context context, Intent intent) {

        stockupNotificationManagerCompat = NotificationManagerCompat.from(context);
        myDatabase = new PillDBHelper(context);
        dateTimeManager = new DateTimeManager();

        if (!intent.hasExtra(context.getString(R.string.pill_name)) && !intent.hasExtra(context.getString(R.string.notification_id))) {
            throw new UnknownError();
        } else {

            String pillName = intent.getStringExtra(context.getString(R.string.pill_name));
            int notificationCode = intent.getIntExtra(context.getString(R.string.notification_id), 0);

            PillDBHelper myDatabase = new PillDBHelper(context);
            alarmSetter = new AlarmSetter(context, pillName, notificationCode);

            if (!myDatabase.getPillName(pillName).equals("null")) {

                String pillDate = myDatabase.getPillDate(pillName);
                Calendar calendar = dateTimeManager.formatDateStringAsCalendar(context, dateTimeManager.getUserTimezone(), pillDate);
                calendar.add(Calendar.MONTH, 1);
                myDatabase.setPillDate(pillName, dateTimeManager.formatDateAsString(context, calendar.getTime()));

                Notification pillStockupNotification;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                    pillStockupNotification = new NotificationCompat.Builder(context, Simpill.PILL_STOCKUP_CHANNEL)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentTitle(pillName + " " + context.getString(R.string.stockup_notification_title))
                            .setContentText(context.getString(R.string.dont_forget_stockup) + " " + pillName + " " + context.getString(R.string.soon))
                            .setColor(500086)
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVibrate(new long[]{100, 300, 500, 300})
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .build();
                } else {
                    pillStockupNotification = new NotificationCompat.Builder(context, Simpill.PILL_STOCKUP_CHANNEL)
                            .setSmallIcon(R.drawable.pill_bottle_color_2)
                            .setContentTitle(pillName + " " + context.getString(R.string.stockup_notification_title))
                            .setContentText(context.getString(R.string.dont_forget_stockup) + " " + pillName + " " + context.getString(R.string.soon))
                            .setColor(500086)
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setVibrate(new long[]{100, 300, 500, 300})
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .build();
                }
                stockupNotificationManagerCompat.notify(pillName, notificationCode, pillStockupNotification);

                alarmSetter.setAlarms(alarmCodeForSupply);
            }
        }
    }
}
