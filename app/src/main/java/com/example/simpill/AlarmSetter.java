package com.example.simpill;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;
import java.util.TimeZone;

public class AlarmSetter {

    private static final long MONTH_IN_MS = 2592000000L;
    private static final long DAY_IN_MS = 86400000L;
    private static final long HALF_DAY_IN_MS = 43200000L;

    Context myContext;
    AlarmManager alarmManager;
    PillDBHelper myDatabase;
    DateTimeManager dateTimeManager;

    TimeZone userTimezone;
    String pillName;
    int requestCode;
    Calendar pillTimeCal, supplyDateCal;

    AlarmSetter(Context myContext, String pillName, int requestCode) {
        this.pillName = pillName;
        this.requestCode = requestCode;
        this.myContext = myContext;
    }

    public void setAlarms() {
        System.out.println(pillName);
        initAll();
        if (!myDatabase.getAlarmsSet(pillName)) {
            setPillReminder();
            setAutoReset();
            setSupplyReminder();
            myDatabase.setAlarmsSet(pillName, 1);
            System.out.println("Alarms set");
        }
        else {
            System.out.println("Alarms already set");
        }
    }

    private void initAll(){
        alarmManager = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);
        myDatabase = new PillDBHelper(myContext);
        dateTimeManager = new DateTimeManager();

        userTimezone = dateTimeManager.getUserTimezone();
        pillTimeCal = getPillTimeCalendar();
        supplyDateCal = getReminderDateCalendar();
    }

    public boolean checkIfAlarmsSet() {
        return myDatabase.getAlarmsSet(pillName);
    }

    private Calendar getReminderDateCalendar() {
        return dateTimeManager.formatDateStringAsCalendar(myContext, userTimezone, myDatabase.getPillDate(pillName));
    }
    private Calendar getPillTimeCalendar() {
        return dateTimeManager.formatTimeStringAsCalendar(myContext, userTimezone, myDatabase.getPillTime(pillName));
    }

    private void setPillReminder() {
        Intent startPillAlarmReceiver = new Intent(myContext, PillAlarmReceiver.class);
        startPillAlarmReceiver.putExtra(myContext.getString(R.string.pill_name), pillName);
        startPillAlarmReceiver.putExtra(myContext.getString(R.string.notification_id), requestCode);

        @SuppressLint("InlinedApi")
        PendingIntent pillAlarmPendingIntent = PendingIntent.getBroadcast(myContext, requestCode, startPillAlarmReceiver, PendingIntent.FLAG_IMMUTABLE);

        if (checkIfAlarmsSet()) {
            System.out.println("Alarm set");
        }
        else {
            System.out.println("Alarm not set");
        }

        long pillReminderTime = pillTimeCal.getTimeInMillis();
        if (pillReminderTime <= System.currentTimeMillis()) {
            pillReminderTime = pillReminderTime + DAY_IN_MS;
        }

        System.out.println("Pill reminder time = " + dateTimeManager.formatLongAsString(myContext, pillReminderTime));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, pillReminderTime, pillAlarmPendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, pillReminderTime, pillAlarmPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, pillReminderTime, pillAlarmPendingIntent);
        }
    }
    private void setAutoReset() {
        Intent startAutoResetReceiver = new Intent(myContext, PillAutoResetReceiver.class);
        startAutoResetReceiver.putExtra(myContext.getString(R.string.pill_name), pillName);
        startAutoResetReceiver.putExtra(myContext.getString(R.string.notification_id), requestCode);

        @SuppressLint("InlinedApi")
        PendingIntent autoResetPendingIntent = PendingIntent.getBroadcast(myContext, requestCode, startAutoResetReceiver, PendingIntent.FLAG_IMMUTABLE);

        long pillReminderTime = pillTimeCal.getTimeInMillis();
        long pillResetTime = pillReminderTime + HALF_DAY_IN_MS;

        if (pillResetTime <= System.currentTimeMillis()) {
            pillResetTime = pillResetTime + DAY_IN_MS;
        }

        System.out.println("Pill reset time = " + dateTimeManager.formatLongAsString(myContext, pillResetTime));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, pillResetTime, autoResetPendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, pillResetTime, autoResetPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, pillResetTime, autoResetPendingIntent);
        }
    }
    private void setSupplyReminder() {
        Intent startPillSupplyReceiver = new Intent (myContext, PillSupplyReceiver.class);
        startPillSupplyReceiver.putExtra(myContext.getString(R.string.pill_name), pillName);

        @SuppressLint("InlinedApi")
        PendingIntent pillSupplyPendingIntent = PendingIntent.getBroadcast(myContext, requestCode, startPillSupplyReceiver, PendingIntent.FLAG_IMMUTABLE);

        long supplyReminderTime = supplyDateCal.getTimeInMillis();

        System.out.println("Supply reminder time = " + dateTimeManager.formatLongAsString(myContext, supplyReminderTime));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, supplyReminderTime, pillSupplyPendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, supplyReminderTime, pillSupplyPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, supplyReminderTime, pillSupplyPendingIntent);
        }
    }
}
