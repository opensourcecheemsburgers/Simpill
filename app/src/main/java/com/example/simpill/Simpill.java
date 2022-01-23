package com.example.simpill;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;


public class Simpill extends Application {

    public static final String PILL_REMINDER_CHANNEL = "pillReminderChannel";
    public static final String PILL_EXTRA_REMINDERS_CHANNEL = "pillExtraRemindershannel";
    public static final String PILL_STOCKUP_CHANNEL = "pillStockupChannel";

    public static final String THEME_PREF_BOOLEAN = "Theme Preference Boolean";
    public static final String IS_24HR_BOOLEAN = "Is 24Hr Boolean";
    public static final String PERMANENT_NOTIFICATIONS_BOOLEAN = "Permanent Notification Boolean";

    public static final String USER_THEME = "User Theme";
    public static final String USER_IS_24HR = "User Is24Hr";
    public static final String USER_PERMANENT_NOTIFICATIONS = "User PermanentNotifications";

    private boolean userThemeBoolean;
    private boolean is24Hr;
    private boolean permanentNotifications;

    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        handleUncaughtException();
    }

    void handleUncaughtException() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->

                Toast.makeText(getApplicationContext(), "An unknown error has occurred in Simpill :( Tell the dev to write better code.", Toast.LENGTH_LONG).show());
    }

    void createNotificationChannels() {

        System.out.println("Creating Notification Channels");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelCompat pillReminderNotificationChannel = new NotificationChannelCompat.Builder(PILL_REMINDER_CHANNEL, NotificationManagerCompat.IMPORTANCE_MAX)
                    .setName("Medication Reminder")
                    .setDescription("This notification channel is used for reminding the user about their medication.")
                    .build();

            NotificationChannelCompat pillExtraRemindersNotificationChannel = new NotificationChannelCompat.Builder(PILL_EXTRA_REMINDERS_CHANNEL, NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setName("Extra Medication Reminders")
                    .setDescription("This notification channel is used for extra reminders after the initial reminder.")
                    .build();

            NotificationChannelCompat pillStockupNotificationChannel = new NotificationChannelCompat.Builder(PILL_STOCKUP_CHANNEL, NotificationManagerCompat.IMPORTANCE_MAX)
                    .setName("Refill Reminder")
                    .setDescription("This notification channel is used for reminding the user to refill their medication supply.")
                    .build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.createNotificationChannel(pillReminderNotificationChannel);
            notificationManagerCompat.createNotificationChannel(pillStockupNotificationChannel);
            notificationManagerCompat.createNotificationChannel(pillExtraRemindersNotificationChannel);
        }
    }

    public boolean getCustomTheme() {
        return userThemeBoolean;
    }
    public boolean getUserIs24Hr() {
        return is24Hr;
    }
    public boolean getUserPermanentNotifications() {
        return permanentNotifications;
    }

    public void setUserIs24Hr(Boolean is24Hr) {
        this.is24Hr = is24Hr;
    }
    public void setCustomTheme(Boolean customTheme) {
        this.userThemeBoolean = customTheme;
    }
    public void setUserPermanentNotifications(Boolean permanentNotifications) {
        this.permanentNotifications = permanentNotifications;
    }


}
