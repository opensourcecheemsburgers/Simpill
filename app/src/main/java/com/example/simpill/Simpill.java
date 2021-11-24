package com.example.simpill;

import android.app.Application;
import android.os.Build;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;


public class Simpill extends Application {

    public static final String PILL_REMINDER_CHANNEL = "pillReminderChannel";
    public static final String PILL_STOCKUP_CHANNEL = "pillStockup";

    public static final String THEME_PREF_BOOLEAN = "Theme Preference Boolean";
    public static final String IS_24HR_BOOLEAN = "Is 24Hr Boolean";

    public static final String USER_THEME = "User Theme";
    public static final String USER_IS_24HR = "User Is24Hr";

    private boolean userThemeBoolean;
    private boolean is24Hr;

    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    void createNotificationChannels() {

        System.out.println("Creating Notification Channels");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelCompat pillReminderNotificationChannel = new NotificationChannelCompat.Builder(PILL_REMINDER_CHANNEL, NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setName("Medication Reminder")
                    .setDescription("This notification channel is used for reminding the user about their pills.")
                    .build();

            NotificationChannelCompat pillStockupNotificationChannel = new NotificationChannelCompat.Builder(PILL_STOCKUP_CHANNEL, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                    .setName("Refill Reminder")
                    .setDescription("This notification channel is used for reminding the user to refill their medication supply.")
                    .build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.createNotificationChannel(pillReminderNotificationChannel);
            notificationManagerCompat.createNotificationChannel(pillStockupNotificationChannel);
        }
    }

    public boolean getCustomTheme() {
        return userThemeBoolean;
    }

    public boolean getUserIs24Hr() {
        return is24Hr;
    }

    public void setUserIs24Hr(Boolean is24Hr) {
        this.is24Hr = is24Hr;
    }

    public void setCustomTheme(Boolean customTheme) {
        this.userThemeBoolean = customTheme;
    }
}
