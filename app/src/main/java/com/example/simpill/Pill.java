/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.DatabaseHelper.ALARM;
import static com.example.simpill.DatabaseHelper.COLUMN_ALARMSSET;
import static com.example.simpill.DatabaseHelper.COLUMN_ALARM_TYPE;
import static com.example.simpill.DatabaseHelper.COLUMN_BOTTLECOLOR;
import static com.example.simpill.DatabaseHelper.COLUMN_CUSTOM_ALARM_URI;
import static com.example.simpill.DatabaseHelper.COLUMN_FREQUENCY;
import static com.example.simpill.DatabaseHelper.COLUMN_ISTAKEN;
import static com.example.simpill.DatabaseHelper.COLUMN_START_DATE;
import static com.example.simpill.DatabaseHelper.COLUMN_STOCKUP;
import static com.example.simpill.DatabaseHelper.COLUMN_SUPPLY;
import static com.example.simpill.DatabaseHelper.COLUMN_TIME;
import static com.example.simpill.DatabaseHelper.COLUMN_TIMETAKEN;
import static com.example.simpill.DatabaseHelper.COLUMN_TITLE;
import static com.example.simpill.DatabaseHelper.CUSTOM_ALARM;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Pill {

    public static final String PRIMARY_KEY_INTENT_KEY_STRING = "primaryKey";
    public static final String NOTIFICATION_ID_INTENT_KEY_STRING = "Notification ID";
    public static final String PILL_TAKEN_VIA_NOTIFICATION_INTENT_KEY =
            "Pill Taken From Notification Click";
    public static final String NULL_DB_ENTRY_STRING = "null";
    public static final int PILL_TAKEN_VALUE = 1;
    public static final int PILL_NOT_TAKEN_VALUE = 0;
    public static final Uri DEFAULT_ALARM_URI =
            Uri.parse("android.resource://com.winston69.simpill/" + R.raw.eas_alarm);

    private final ContentValues contentValues = new ContentValues(13);
    private final DateTimeManager dateTimeManager = new DateTimeManager();

    private String name = "";
    private int primaryKey = 0;
    private String startDate = NULL_DB_ENTRY_STRING;
    private String formattedStartDate = NULL_DB_ENTRY_STRING;
    private String stockupDate = NULL_DB_ENTRY_STRING;
    private String timeTaken = NULL_DB_ENTRY_STRING;
    private Uri customAlarmUri =
            Uri.parse("android.resource://com.winston69.simpill/" + R.raw.eas_alarm);
    private String[] timesArray = {""};
    private String times24HrFormat = NULL_DB_ENTRY_STRING;
    private String times12HrFormat = NULL_DB_ENTRY_STRING;
    private int frequency = 1;
    private int taken = 0;
    private int supply = -1;
    private int alarmType = DatabaseHelper.ALARM;
    private long[] alarmReminderTimes;
    private int[] alarmRequestCodes;
    private int alarmsSet = 0;
    private int bottleColor = 2;

    public Pill(
            String name,
            String[] timesArray,
            String startDate,
            String stockupDate,
            Uri customAlarmUri,
            int frequency,
            int taken,
            String timeTaken,
            int supply,
            int alarmType,
            int alarmsSet,
            int bottleColor) {
        setName(name);
        setTimesArray(timesArray);
        setAlarmReminderTimes();
        setStartDate(startDate);
        setStockupDate(stockupDate);
        setCustomAlarmUri(customAlarmUri);
        setFrequency(frequency);
        setTaken(taken);
        setTimeTaken(timeTaken);
        setSupply(supply);
        setAlarmType(alarmType);
        setAlarmsSet(alarmsSet);
        setAlarmRequestCodes();
        setBottleColor(bottleColor);
    }

    public Pill(
            int primaryKey,
            String name,
            String[] timesArray,
            String startDate,
            String stockupDate,
            Uri customAlarmUri,
            int frequency,
            int taken,
            String timeTaken,
            int supply,
            int alarmType,
            int alarmsSet,
            int bottleColor) {
        setPrimaryKey(primaryKey);
        setName(name);
        setTimesArray(timesArray);
        setAlarmReminderTimes();
        setStartDate(startDate);
        setStockupDate(stockupDate);
        setCustomAlarmUri(customAlarmUri);
        setFrequency(frequency);
        setTaken(taken);
        setTimeTaken(timeTaken);
        setSupply(supply);
        setAlarmType(alarmType);
        setAlarmsSet(alarmsSet);
        setAlarmRequestCodes();
        setBottleColor(bottleColor);
    }

    public Pill() {}

    public void takePill(Context context) {
        setSupply(getSupply() - 1);
        setTaken(PILL_TAKEN_VALUE);
        setTimeTaken(dateTimeManager.getCurrentTimeString());
        updatePillInDatabase(context);
    }

    public void resetPill(Context context, int recyclerViewPosition) {
        setSupply(getSupply() + 1);
        setTaken(PILL_NOT_TAKEN_VALUE);
        setTimeTaken(NULL_DB_ENTRY_STRING);
        setAlarm(context);
        setAlarmsSet(1);
        updatePillInDatabase(context);
        PillListener pillListener = (PillListener) context;
        pillListener.notifyResetPill(recyclerViewPosition);
    }

    public void autoResetPill(Context context) {
        setTaken(PILL_NOT_TAKEN_VALUE);
        setTimeTaken(NULL_DB_ENTRY_STRING);
        setAlarm(context);
        setAlarmsSet(1);
        updatePillInDatabase(context);
    }

    public void sendPillNotification(Context context) {
        NotificationManagerCompat pillNotificationManagerCompat =
                NotificationManagerCompat.from(context);
        Notification pillReminderNotification;

        if ((getAlarmType() == ALARM || getAlarmType() == CUSTOM_ALARM)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Settings.canDrawOverlays(context)) {
            context.startActivity(
                    new Intent(context, PillAlarmDisplay.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            .putExtra(PRIMARY_KEY_INTENT_KEY_STRING, getPrimaryKey())
                            .putExtra(NOTIFICATION_ID_INTENT_KEY_STRING, 0));
        }

        Intent openMainIntent =
                new Intent(context, MainActivity.class)
                        .putExtra(PILL_TAKEN_VIA_NOTIFICATION_INTENT_KEY, getPrimaryKey());
        @SuppressLint("InlinedApi")
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context, primaryKey, openMainIntent, PendingIntent.FLAG_IMMUTABLE);

        pillReminderNotification =
                new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                        .setSmallIcon(R.drawable.pill_bottle_color_2)
                        .setContentText(
                                context.getString(
                                        R.string.reminder_notification_description, getName()))
                        .setColor(500086)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(
                                                context.getString(
                                                        R.string.reminder_notification_description,
                                                        getName())))
                        .setCategory(NotificationCompat.CATEGORY_REMINDER)
                        .setLights(Color.RED, 500, 500)
                        .setVibrate(AudioHelper.vibratorPattern)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setOngoing(new SharedPrefs(context).getStickyNotificationsPref())
                        .setContentIntent(pendingIntent)
                        .addAction(
                                R.mipmap.ic_launcher,
                                context.getString(R.string.open),
                                pendingIntent)
                        .addAction(
                                R.mipmap.ic_launcher,
                                context.getString(R.string.dismiss),
                                pendingIntent)
                        .build();

        if (getTaken() == PILL_TAKEN_VALUE) {
            pillReminderNotification =
                    new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                            .setSmallIcon(R.drawable.pill_bottle_color_2)
                            .setColor(500086)
                            .setStyle(
                                    new NotificationCompat.BigTextStyle()
                                            .bigText(
                                                    context.getString(
                                                            R.string
                                                                    .reminder_already_taken_description,
                                                            getName())))
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setOngoing(false)
                            .setContentIntent(pendingIntent)
                            .setFullScreenIntent(pendingIntent, true)
                            .addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                            .build();
        }

        pillNotificationManagerCompat.notify(getName(), getPrimaryKey(), pillReminderNotification);
    }

    public void sendStockupNotification(Context context) {
        NotificationManagerCompat pillNotificationManagerCompat =
                NotificationManagerCompat.from(context);
        Notification pillReminderNotification;

        if (getAlarmType() == ALARM)
            context.startActivity(
                    new Intent(context, PillAlarmDisplay.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            .putExtra(PRIMARY_KEY_INTENT_KEY_STRING, getPrimaryKey())
                            .putExtra("Notification ID", 0));

        Intent openMainIntent =
                new Intent(context, MainActivity.class)
                        .putExtra(PILL_TAKEN_VIA_NOTIFICATION_INTENT_KEY, getPrimaryKey());
        @SuppressLint("InlinedApi")
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context, primaryKey, openMainIntent, PendingIntent.FLAG_IMMUTABLE);

        pillReminderNotification =
                new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                        .setSmallIcon(R.drawable.pill_bottle_color_2)
                        .setContentText(
                                context.getString(
                                        R.string.reminder_notification_description, getName()))
                        .setColor(500086)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(
                                                context.getString(
                                                        R.string.reminder_notification_description,
                                                        getName())))
                        .setCategory(NotificationCompat.CATEGORY_REMINDER)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setOngoing(new SharedPrefs(context).getStickyNotificationsPref())
                        .setContentIntent(pendingIntent)
                        .addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                        .addAction(R.mipmap.ic_launcher, "Dismiss", pendingIntent)
                        .build();

        if (getTaken() == PILL_TAKEN_VALUE) {
            pillReminderNotification =
                    new NotificationCompat.Builder(context, Simpill.PILL_REMINDER_CHANNEL)
                            .setSmallIcon(R.drawable.pill_bottle_color_2)
                            .setColor(500086)
                            .setStyle(
                                    new NotificationCompat.BigTextStyle()
                                            .bigText(
                                                    context.getString(
                                                            R.string
                                                                    .reminder_already_taken_description,
                                                            getName())))
                            .setCategory(NotificationCompat.CATEGORY_REMINDER)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setOngoing(false)
                            .setContentIntent(pendingIntent)
                            .setFullScreenIntent(pendingIntent, true)
                            .addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                            .build();
        }

        pillNotificationManagerCompat.notify(getName(), getPrimaryKey(), pillReminderNotification);
    }

    public void deleteActiveNotifications(Context context) {
        for (int currentNumber = 1; currentNumber < getTimesArray().length + 1; currentNumber++) {
            NotificationManagerCompat.from(context)
                    .cancel(getName(), primaryKey * 10 * 10 * 10 + currentNumber);
        }
    }

    public void setAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        for (int index = 0; index < getAlarmReminderTimes().length; index++) {
            int frequency = getFrequency();
            int requestCode = getAlarmRequestCodes()[index];
            long reminderTime = getAlarmReminderTimes()[index];

            Log.d("PILL_ALARM", "at " + dateTimeManager.formatLongAsDateTimeString(reminderTime));
            Log.d("PILL_ALARM", "with request code" + requestCode);

            @SuppressLint("InlinedApi")
            PendingIntent pillAlarmPendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            requestCode,
                            new Intent(context, ReceiverPillAlarm.class)
                                    .putExtra(PRIMARY_KEY_INTENT_KEY_STRING, getPrimaryKey())
                                    .putExtra(NOTIFICATION_ID_INTENT_KEY_STRING, requestCode),
                            PendingIntent.FLAG_IMMUTABLE);

            alarmManager.cancel(pillAlarmPendingIntent); // cancel old alarms

            if (frequency <= 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, reminderTime, pillAlarmPendingIntent);
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP, reminderTime, pillAlarmPendingIntent);
                }
            } else {
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        AlarmManager.INTERVAL_DAY * frequency,
                        pillAlarmPendingIntent);
            }
        }
        for (int currentNumber = 0; currentNumber < alarmReminderTimes.length; currentNumber++) {
            int requestCode = getAlarmRequestCodes()[currentNumber];

            Intent startAutoResetReceiver = new Intent(context, ReceiverPillAutoReset.class);
            startAutoResetReceiver.putExtra(PRIMARY_KEY_INTENT_KEY_STRING, getPrimaryKey());
            startAutoResetReceiver.putExtra(NOTIFICATION_ID_INTENT_KEY_STRING, requestCode);

            @SuppressLint("InlinedApi")
            PendingIntent autoResetPendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            requestCode,
                            startAutoResetReceiver,
                            PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(autoResetPendingIntent); // cancel old auto resets

            int nextNumber;
            if (currentNumber < alarmReminderTimes.length - 1) {
                nextNumber = currentNumber + 1;
            } else {
                nextNumber = 0;
            }

            long resetTime = alarmReminderTimes[nextNumber] - AlarmManager.INTERVAL_FIFTEEN_MINUTES;

            while (resetTime <= System.currentTimeMillis()) {
                resetTime = resetTime + AlarmManager.INTERVAL_DAY;
            }

            // If last reminder, reset time = current time + 24 hours - 15m.
            if (currentNumber == alarmReminderTimes.length - 1
                    && (System.currentTimeMillis()
                            < (resetTime
                                    + AlarmManager.INTERVAL_DAY
                                    - AlarmManager.INTERVAL_FIFTEEN_MINUTES))) {
                resetTime = resetTime + AlarmManager.INTERVAL_DAY;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, resetTime, autoResetPendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, resetTime, autoResetPendingIntent);
            }
        }
    }

    public void setStockupAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (!getStockupDate().equalsIgnoreCase(NULL_DB_ENTRY_STRING)) {
            Intent startPillSupplyReceiver = new Intent(context, ReceiverPillSupply.class);
            startPillSupplyReceiver.putExtra(PRIMARY_KEY_INTENT_KEY_STRING, getPrimaryKey());

            @SuppressLint("InlinedApi")
            PendingIntent pillSupplyPendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            getPrimaryKey(),
                            startPillSupplyReceiver,
                            PendingIntent.FLAG_IMMUTABLE);

            alarmManager.cancel(pillSupplyPendingIntent);

            long supplyReminderTime =
                    dateTimeManager.formatDateTimeStringAsLong(getStockupDate() + " 12:00");

            if (supplyReminderTime < System.currentTimeMillis()) {
                supplyReminderTime = supplyReminderTime + 2629746000L;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, supplyReminderTime, pillSupplyPendingIntent);
            } else {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP, supplyReminderTime, pillSupplyPendingIntent);
            }
        }
    }

    public void cancelAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int index = 0; index < getAlarmReminderTimes().length; index++) {
            int requestCode = getAlarmRequestCodes()[index];

            @SuppressLint("InlinedApi")
            PendingIntent pillAlarmPendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            requestCode,
                            new Intent(context, ReceiverPillAlarm.class)
                                    .putExtra(PRIMARY_KEY_INTENT_KEY_STRING, getPrimaryKey())
                                    .putExtra(NOTIFICATION_ID_INTENT_KEY_STRING, requestCode),
                            PendingIntent.FLAG_IMMUTABLE);

            alarmManager.cancel(pillAlarmPendingIntent);
        }

        Intent startPillSupplyReceiver = new Intent(context, ReceiverPillSupply.class);
        startPillSupplyReceiver.putExtra(PRIMARY_KEY_INTENT_KEY_STRING, getPrimaryKey());

        @SuppressLint("InlinedApi")
        PendingIntent pillSupplyPendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        getPrimaryKey(),
                        startPillSupplyReceiver,
                        PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pillSupplyPendingIntent);
    }
    
    public Pill addToDatabase(Context context) {
        setContentValues();
        setAlarmRequestCodes();
        Pill pill = new DatabaseHelper(context).addPill(this);
        pill.setAlarm(context);
        pill.setStockupAlarm(context);
        pill.setAlarmsSet(1);
        return pill;
    }

    public void updatePillInDatabase(Context context) {
        setContentValues();
        new DatabaseHelper(context).updatePill(this);
    }

    public void deletePillFromDatabase(Context context, int recyclerViewPosition) {
        cancelAlarms(context);
        new DatabaseHelper(context).deletePill(this);
        PillListener pillListener = (PillListener) context;
        pillListener.notifyDeletedPill(this, recyclerViewPosition);
    }

    private void setAlarmReminderTimes() {
        this.alarmReminderTimes = new long[timesArray.length];
        for (int index = 0; index < timesArray.length; index++) {
            long pillReminderTime =
                    dateTimeManager.convertTimeToCurrentDateTimeInMillis(this.timesArray[index]);
            while (pillReminderTime <= System.currentTimeMillis()) {
                pillReminderTime = pillReminderTime + AlarmManager.INTERVAL_DAY;
            }
            this.alarmReminderTimes[index] = pillReminderTime;
        }
    }

    public long[] getAlarmReminderTimes() {
        return this.alarmReminderTimes;
    }

    //            @SuppressLint("InlinedApi")
    //            PendingIntent pillAlarmPendingIntent =
    //                    PendingIntent.getBroadcast(
    //                            context,
    //                            pill.getAlarmRequestCode(),
    //                            new Intent(context, ReceiverPillAlarm.class)
    //                                    .putExtra(PRIMARY_KEY_INTENT_KEY_STRING,
    // pill.getPrimaryKey())
    //                                    .putExtra(
    //                                            context.getString(R.string.notification_id),
    //                                            pill.getAlarmRequestCode()),
    //                            PendingIntent.FLAG_IMMUTABLE);

    // cancel any previous alarms before creating new ones
    //            alarmManager.cancel(pillAlarmPendingIntent);
    //
    //            long pillReminderTime =
    //                    dateTimeManager.convertTimeToCurrentDateTimeInMillis(reminderTime);
    //            while (pillReminderTime <= System.currentTimeMillis()) {
    //                pillReminderTime = pillReminderTime + AlarmManager.INTERVAL_DAY;
    //            }
    //
    //            if (frequency <= 1) {
    //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    //                    alarmManager.setExactAndAllowWhileIdle(
    //                            AlarmManager.RTC_WAKEUP, pillReminderTime,
    // pillAlarmPendingIntent);
    //                } else {
    //                    alarmManager.setExact(
    //                            AlarmManager.RTC_WAKEUP, pillReminderTime,
    // pillAlarmPendingIntent);
    //                }
    //            } else {
    //                alarmManager.setRepeating(
    //                        AlarmManager.RTC_WAKEUP,
    //                        pillReminderTime,
    //                        AlarmManager.INTERVAL_DAY * frequency,
    //                        pillAlarmPendingIntent);
    //            }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        contentValues.put(COLUMN_TITLE, name);
    }

    public int getPrimaryKey() {
        if (primaryKey <= 0) {

        }

        return this.primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getStartDate() {
        return startDate == null ? NULL_DB_ENTRY_STRING : startDate;
    }

    public void setStartDate(String startDate) {
        if (startDate == null) startDate = NULL_DB_ENTRY_STRING;
        this.startDate = startDate;
        if (!startDate.equals(NULL_DB_ENTRY_STRING)) {
            setFormattedStartDate(
                    dateTimeManager.convertISODateStringToLocallyFormattedString(startDate));
        }
        contentValues.put(COLUMN_START_DATE, startDate);
    }

    public String getFormattedStartDate() {
        return formattedStartDate;
    }

    public void setFormattedStartDate(String formattedStartDate) {
        this.formattedStartDate = formattedStartDate;
    }

    public String getStockupDate() {
        return stockupDate == null || stockupDate.equals(NULL_DB_ENTRY_STRING)
                ? NULL_DB_ENTRY_STRING
                : dateTimeManager.formatDateTimeStringAsLong(stockupDate + " 12:00")
                                > System.currentTimeMillis()
                        ? stockupDate
                        : dateTimeManager.addMonthToDateString(stockupDate);
    }

    public void setStockupDate(String stockupDate) {
        this.stockupDate = stockupDate;
        contentValues.put(COLUMN_STOCKUP, stockupDate);
    }

    public String getTimeTaken() {
        return timeTaken == null ? NULL_DB_ENTRY_STRING : timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        timeTaken = timeTaken == null ? NULL_DB_ENTRY_STRING : timeTaken;
        this.timeTaken = timeTaken;
        contentValues.put(COLUMN_TIMETAKEN, timeTaken);
    }

    public Uri getCustomAlarmUri() {
        return customAlarmUri == null ? DEFAULT_ALARM_URI : customAlarmUri;
    }

    public void setCustomAlarmUri(Uri alarmUri) {
        alarmUri = alarmUri == null ? DEFAULT_ALARM_URI : alarmUri;
        this.customAlarmUri = alarmUri;
        contentValues.put(COLUMN_CUSTOM_ALARM_URI, alarmUri.toString());
    }

    public String[] getTimesArray() {
        return timesArray;
    }

    public void setTimesArray(String[] timesArray) {
        this.timesArray = timesArray;
        ArrayHelper arrayHelper = new ArrayHelper();
        String times24HrFormat = arrayHelper.convertArrayToString(timesArray.clone());
        contentValues.put(COLUMN_TIME, times24HrFormat);
        setTimes24HrFormat(arrayHelper.convertArrayToString(timesArray.clone()));
        setTimes12HrFormat(
                arrayHelper.convertArrayToString(
                        arrayHelper.convert24HrArrayTo12HrArray(timesArray.clone())));
        setAlarmReminderTimes();
    }

    public String getTimes24HrFormat() {
        return this.times24HrFormat;
    }

    private void setTimes24HrFormat(String times24HrFormat) {
        this.times24HrFormat = times24HrFormat;
    }

    public String getTimes12HrFormat() {
        return this.times12HrFormat;
    }

    private void setTimes12HrFormat(String times12HrFormat) {
        this.times12HrFormat = times12HrFormat;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
        contentValues.put(COLUMN_FREQUENCY, frequency);
    }

    public int getTaken() {
        return taken;
    }

    public void setTaken(int taken) {
        this.taken = taken;
        contentValues.put(COLUMN_ISTAKEN, taken);
    }

    public int getSupply() {
        return supply;
    }

    public void setSupply(int supply) {
        this.supply = supply;
        contentValues.put(COLUMN_SUPPLY, supply);
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
        contentValues.put(COLUMN_ALARM_TYPE, alarmType);
    }

    public int getBottleColor() {
        return bottleColor;
    }

    public void setBottleColor(int bottleColor) {
        this.bottleColor = bottleColor;
        contentValues.put(COLUMN_BOTTLECOLOR, bottleColor);
    }

    public Drawable getBottleDrawable(Context context) {
        int bottleColor = getBottleColor();
        int bottleDrawableId;

        switch (bottleColor) {
            case 1:
                bottleDrawableId = R.drawable.pill_bottle_color_1;
                break;
            case 2:
            default:
                bottleDrawableId = R.drawable.pill_bottle_color_2;
                break;
            case 3:
                bottleDrawableId = R.drawable.pill_bottle_color_3;
                break;
            case 4:
                bottleDrawableId = R.drawable.pill_bottle_color_4;
                break;
            case 5:
                bottleDrawableId = R.drawable.pill_bottle_color_5;
                break;
            case 6:
                bottleDrawableId = R.drawable.pill_bottle_color_6;
                break;
            case 7:
                bottleDrawableId = R.drawable.pill_bottle_color_7;
                break;
            case 8:
                bottleDrawableId = R.drawable.pill_bottle_color_8;
                break;
            case 9:
                bottleDrawableId = R.drawable.pill_bottle_color_9;
                break;
            case 10:
                bottleDrawableId = R.drawable.pill_bottle_color_10;
                break;
            case 11:
                bottleDrawableId = R.drawable.pill_bottle_color_11;
                break;
            case 12:
                bottleDrawableId = R.drawable.pill_bottle_color_12;
                break;
        }
        return AppCompatResources.getDrawable(context, bottleDrawableId);
    }

    public int getAlarmsSet() {
        return alarmsSet;
    }

    public void setAlarmsSet(int alarmsSet) {
        this.alarmsSet = alarmsSet;
        contentValues.put(COLUMN_ALARMSSET, alarmsSet);
    }

    public int[] getAlarmRequestCodes() {
        return this.alarmRequestCodes;
    }

    public void setAlarmRequestCodes() {
        this.alarmRequestCodes = new int[getAlarmReminderTimes().length];
        for (int index = 0; index < getAlarmReminderTimes().length; index++) {
            this.alarmRequestCodes[index] = this.primaryKey * 10 * 10 * 10 + index;
        }
    }

    public ContentValues getContentValues() {
        return contentValues;
    }

    public void setContentValues() {
        contentValues.put(COLUMN_TITLE, getName());
        contentValues.put(COLUMN_TIME, getTimes24HrFormat());
        contentValues.put(COLUMN_START_DATE, getStartDate());
        contentValues.put(COLUMN_STOCKUP, getStockupDate());
        contentValues.put(COLUMN_CUSTOM_ALARM_URI, getCustomAlarmUri().toString());
        contentValues.put(COLUMN_FREQUENCY, getFrequency());
        contentValues.put(COLUMN_ISTAKEN, getTaken());
        contentValues.put(COLUMN_TIMETAKEN, getTimeTaken());
        contentValues.put(COLUMN_SUPPLY, getSupply());
        contentValues.put(COLUMN_ALARM_TYPE, getAlarmType());
        contentValues.put(COLUMN_ALARMSSET, getAlarmsSet());
        contentValues.put(COLUMN_BOTTLECOLOR, getBottleColor());
    }

    public interface PillListener {

        void notifyAddedPill(Pill pill);

        void notifyDeletedPill(Pill pill, int position);

        void notifyResetPill(int position);
    }
}
