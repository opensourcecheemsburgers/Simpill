package com.example.simpill;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;


public class Simpill extends Application {

    public static final String PILL_REMINDER_CHANNEL = "pillReminderChannel";
    public static final String PILL_EXTRA_REMINDERS_CHANNEL = "pillExtraRemindershannel";
    public static final String PILL_STOCKUP_CHANNEL = "pillStockupChannel";

    public static final String OPEN_COUNT_FILENAME = "open_count";
    public static final String DARK_DIALOGS_FILENAME = "dark_dialogs";
    public static final String SELECTED_THEME_FILENAME = "User Theme";
    public static final String IS_24HR_BOOLEAN_FILENAME = "Is 24Hr Boolean";
    public static final String PERMANENT_NOTIFICATIONS_BOOLEAN = "Permanent Notification Boolean";

    public static final String DARK_DIALOGS_TAG = "Dark Dialogs";
    public static final String OPEN_COUNT_TAG = "Open Count";
    public static final String USER_THEME_TAG = "User Theme";
    public static final String USER_IS_24HR_TAG = "User Is24Hr";
    public static final String USER_PERMANENT_NOTIFICATIONS_TAG = "User PermanentNotifications";

    public final int BLUE_THEME = 1;
    public final int BLACK_THEME = 2;
    public final int GREY_THEME = 3;
    public final int PURPLE_THEME = 4;

    private int openCount, theme;
    private boolean is24Hr, permanentNotifications, darkDialogs;

    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
        handleUncaughtException();
    }

    void handleUncaughtException() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> new Toasts().showCustomToast(this, getString(R.string.unknown_error_toast)));
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

    public int getCustomTheme() {
        return theme;
    }
    public boolean getUserIs24Hr() {
        return is24Hr;
    }
    public boolean getUserPermanentNotifications() {
        return permanentNotifications;
    }
    public int getOpenCount() {
        return openCount;
    }
    public boolean getDarkDialogs() {
        return darkDialogs;
    }

    public void setUserIs24Hr(Boolean is24Hr) {
        this.is24Hr = is24Hr;
    }
    public void setCustomTheme(int theme) {
        this.theme = theme;
    }
    public void setUserPermanentNotifications(Boolean permanentNotifications) {
        this.permanentNotifications = permanentNotifications;
    }
    public void setOpenCount(int openCount) {
        this.openCount = openCount;
    }
    public void setDarkDialogs(boolean darkDialogs) {
        this.darkDialogs = darkDialogs;
    }


}
