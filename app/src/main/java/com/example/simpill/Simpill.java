/* (C) 2022 */
package com.example.simpill;

import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Simpill extends Application {
    public static final String PILL_REMINDER_CHANNEL = "pillReminderChannel";
    public static final String PILL_EXTRA_REMINDERS_CHANNEL = "pillExtraRemindershannel";
    public static final String PILL_STOCKUP_CHANNEL = "pillStockupChannel";

    public static final int BLUE_THEME = 1;
    public static final int BLACK_THEME = 2;
    public static final int GREY_THEME = 3;
    public static final int PURPLE_THEME = 4;

    public static final String IS_CRASH_INTENT_KEY_STRING = "crash?";
    public static final String CRASH_DATA_INTENT_KEY_STRING = "crash_data";

    public static final String LINE_SEPARATOR = "\n";

    public Simpill() {
        Thread.UncaughtExceptionHandler SIMPILL_THREAD_EXCEPTION_HANDLER =
                (t, e) -> {
                    StringWriter stackTrace = new StringWriter();
                    e.printStackTrace(new PrintWriter(stackTrace));
                    StringBuilder errorReport = new StringBuilder();
                    errorReport.append("Error: ").append(LINE_SEPARATOR);
                    errorReport.append(stackTrace);
                    String ts = new DateTimeManager().getCurrentDateAndTimeString();
                    errorReport.append("Time: ").append(ts);
                    errorReport
                            .append(LINE_SEPARATOR)
                            .append("Device info: ")
                            .append(LINE_SEPARATOR);
                    errorReport.append("Brand: ").append(Build.BRAND);
                    errorReport.append(LINE_SEPARATOR);
                    errorReport.append("Device: ").append(Build.DEVICE);
                    errorReport.append(LINE_SEPARATOR);
                    errorReport.append("Model: ").append(Build.MODEL);
                    errorReport.append(LINE_SEPARATOR);
                    errorReport.append("Id: ").append(Build.ID);
                    errorReport.append(LINE_SEPARATOR);
                    errorReport.append("Product: ").append(Build.PRODUCT);
                    errorReport.append(LINE_SEPARATOR);
                    errorReport
                            .append(LINE_SEPARATOR)
                            .append("Build info: ")
                            .append(LINE_SEPARATOR);
                    errorReport.append("SDK: ").append(Build.VERSION.SDK);
                    errorReport.append(LINE_SEPARATOR);
                    errorReport.append("Release: ").append(Build.VERSION.RELEASE);
                    errorReport.append(LINE_SEPARATOR);
                    errorReport.append("Incremental: ").append(Build.VERSION.INCREMENTAL);
                    errorReport.append(LINE_SEPARATOR);

                    PackageManager packageManager = this.getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
                    ComponentName componentName = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                    mainIntent.putExtra(IS_CRASH_INTENT_KEY_STRING, true);
                    mainIntent.putExtra(CRASH_DATA_INTENT_KEY_STRING, String.valueOf(errorReport));
                    this.startActivity(mainIntent);
                    Runtime.getRuntime().exit(0);
                };
        Thread.setDefaultUncaughtExceptionHandler(SIMPILL_THREAD_EXCEPTION_HANDLER);
    }

    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    void createNotificationChannels() {
        System.out.println("Creating Notification Channels");

        Uri soundUri =
                Uri.parse(
                        ContentResolver.SCHEME_ANDROID_RESOURCE
                                + "://"
                                + getApplicationContext().getPackageName()
                                + "/"
                                + R.raw.eas_alarm);
        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .setFlags(AudioAttributes.FLAG_HW_AV_SYNC)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelCompat pillReminderNotificationChannel =
                    new NotificationChannelCompat.Builder(
                                    PILL_REMINDER_CHANNEL, NotificationManagerCompat.IMPORTANCE_MAX)
                            .setName("Medication Reminder")
                            .setVibrationPattern(AudioHelper.vibratorPattern)
                            .setVibrationEnabled(true)
                            .setSound(soundUri, audioAttributes)
                            .setLightColor(50086)
                            .setLightsEnabled(true)
                            .setShowBadge(true)
                            .setDescription(
                                    "This notification channel is used for reminding the user"
                                            + " about their medication.")
                            .build();

            NotificationChannelCompat pillStockupNotificationChannel =
                    new NotificationChannelCompat.Builder(
                                    PILL_STOCKUP_CHANNEL, NotificationManagerCompat.IMPORTANCE_MIN)
                            .setName("Refill Reminder")
                            .setDescription(
                                    "This notification channel is used for reminding the user to"
                                            + " refill their medication supply.")
                            .build();

            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(this);
            notificationManagerCompat.createNotificationChannel(pillReminderNotificationChannel);
            notificationManagerCompat.createNotificationChannel(pillStockupNotificationChannel);
        }
    }
}
