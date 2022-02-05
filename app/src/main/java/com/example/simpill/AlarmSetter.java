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
    Context context;
    Simpill simpill;
    DatabaseHelper myDatabase;
    AlarmManager alarmManager;
    DateTimeManager dateTimeManager;

    TimeZone userTimezone;
    String pillName;
    int primaryKey;
    Calendar[] pillTimesCalArray;
    Calendar supplyDateCal;

    AlarmSetter(Context context, String pillName) {
        this.pillName = pillName;
        this.context = context;
        this.myDatabase = new DatabaseHelper(context);
        this.primaryKey = myDatabase.getPrimaryKeyId(pillName);
    }

    private int formatPrimaryKeyAsRequestCode(int requestNumber) {
        return primaryKey * 10 * 10 * 10 + requestNumber;
    }

    public void setAlarms(int alarmCode) {
        System.out.println(pillName);
        initAll();

        switch (alarmCode) {
            case 0:
                setPillReminder();
                setAutoReset();
                setSupplyReminder();
                break;
            case 1:
                setPillReminder();
                break;
            case 2:
                setAutoReset();
                break;
            case 3:
                setSupplyReminder();
                break;
        }
    }

    private void initAll(){
        simpill = new Simpill();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        myDatabase = new DatabaseHelper(context);
        dateTimeManager = new DateTimeManager();

        userTimezone = dateTimeManager.getUserTimezone();
        pillTimesCalArray = getPillTimeCalendar();
        supplyDateCal = getReminderDateCalendar();
    }

    private Calendar getReminderDateCalendar() {
        return dateTimeManager.formatDateStringAsCalendar(context, userTimezone, myDatabase.getPillDate(pillName));
    }
    private Calendar[] getPillTimeCalendar() {
        String[] times =  myDatabase.getPillTime(pillName);
        Calendar[] calendars = new Calendar[times.length];

        for (int currentNumber = 0; currentNumber < times.length; currentNumber++) {
            calendars[currentNumber] = dateTimeManager.formatTimeStringAsCalendar(context, userTimezone, times[currentNumber]);
        }

        return calendars;
    }

    private void setPillReminder() {
        for (int currentNumber = 0; currentNumber < pillTimesCalArray.length; currentNumber++) {
            int requestCode = formatPrimaryKeyAsRequestCode(currentNumber);
            int frequency = myDatabase.getFrequency(pillName);

            @SuppressLint("InlinedApi") PendingIntent pillAlarmPendingIntent = PendingIntent.getBroadcast(context, requestCode,
                    new Intent(context, ReceiverPillAlarm.class)
                            .putExtra(context.getString(R.string.pill_name), pillName)
                            .putExtra(context.getString(R.string.notification_id), requestCode),
                    PendingIntent.FLAG_IMMUTABLE);

            long pillReminderTime =  pillTimesCalArray[currentNumber].getTimeInMillis();
            while (pillReminderTime <= System.currentTimeMillis()) {
                pillReminderTime = pillReminderTime + AlarmManager.INTERVAL_DAY;
            }

            System.out.println(pillName + " reminder time = " + dateTimeManager.formatLongAsDateString(context, pillReminderTime));

            if (frequency <= 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, pillReminderTime, pillAlarmPendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, pillReminderTime, pillAlarmPendingIntent);
                }
            } else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, pillReminderTime, AlarmManager.INTERVAL_DAY * frequency, pillAlarmPendingIntent);
            }
        }
    }
    private void setAutoReset() {
        if (pillTimesCalArray.length == 1) {
            int requestCode = formatPrimaryKeyAsRequestCode(0);

            @SuppressLint("InlinedApi")
            PendingIntent autoResetPendingIntent = PendingIntent.getBroadcast(context, requestCode,
                    new Intent(context, ReceiverPillAutoReset.class)
                            .putExtra(context.getString(R.string.pill_name), pillName)
                            .putExtra(context.getString(R.string.notification_id), requestCode),
                    PendingIntent.FLAG_IMMUTABLE);

            long pillReminderTime = pillTimesCalArray[0].getTimeInMillis();
            long pillResetTime = pillReminderTime - AlarmManager.INTERVAL_HALF_DAY;

            while (pillResetTime <= System.currentTimeMillis()) {
                pillResetTime = pillResetTime + AlarmManager.INTERVAL_DAY;
            }

            System.out.println("Pill reset time = " + dateTimeManager.formatLongAsDateString(context, pillResetTime));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, pillResetTime, autoResetPendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, pillResetTime, autoResetPendingIntent);
            }
        }

        else {
            for (int currentNumber = 0; currentNumber < pillTimesCalArray.length; currentNumber++) {
                int nextNumber;

                if (currentNumber < pillTimesCalArray.length - 1) {
                    nextNumber = currentNumber + 1;
                }
                else {
                    nextNumber = 0;
                }

                int requestCode = formatPrimaryKeyAsRequestCode(currentNumber);

                Intent startAutoResetReceiver = new Intent(context, ReceiverPillAutoReset.class);
                startAutoResetReceiver.putExtra(context.getString(R.string.pill_name), pillName);
                startAutoResetReceiver.putExtra(context.getString(R.string.notification_id), requestCode);

                @SuppressLint("InlinedApi")
                PendingIntent autoResetPendingIntent = PendingIntent.getBroadcast(context, requestCode, startAutoResetReceiver, PendingIntent.FLAG_IMMUTABLE);

                Calendar currentNumberTime = pillTimesCalArray[currentNumber];
                System.out.println("Array [" + currentNumber + "] = " + dateTimeManager.formatLongAsTimeString(context, currentNumberTime.getTimeInMillis()));

                Calendar nextNumberTime = pillTimesCalArray[nextNumber];
                System.out.println("Array [" + nextNumber + "] = " + dateTimeManager.formatLongAsTimeString(context, nextNumberTime.getTimeInMillis()));

                System.out.println(nextNumberTime.getTimeInMillis());

                System.out.println(nextNumber);

                long resetTime = nextNumberTime.getTimeInMillis() - AlarmManager.INTERVAL_FIFTEEN_MINUTES;

                while (resetTime <= System.currentTimeMillis()) {
                    resetTime = resetTime + AlarmManager.INTERVAL_DAY;
                }

                if (currentNumber == pillTimesCalArray.length - 1 && (System.currentTimeMillis() < (resetTime + AlarmManager.INTERVAL_DAY - AlarmManager.INTERVAL_FIFTEEN_MINUTES))) {
                    resetTime = resetTime + AlarmManager.INTERVAL_DAY;
                }

                Calendar pillResetCal = Calendar.getInstance();
                pillResetCal.setTimeInMillis(resetTime);
                System.out.println(pillResetCal.getTime());

                System.out.println("Pill reset time = " + dateTimeManager.formatLongAsDateString(context, resetTime));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, resetTime, autoResetPendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, resetTime, autoResetPendingIntent);
                }
            }
        }
    }
    private void setSupplyReminder() {
        Intent startPillSupplyReceiver = new Intent (context, ReceiverPillSupply.class);
        startPillSupplyReceiver.putExtra(context.getString(R.string.pill_name), pillName);

        @SuppressLint("InlinedApi")
        PendingIntent pillSupplyPendingIntent = PendingIntent.getBroadcast(context, primaryKey, startPillSupplyReceiver, PendingIntent.FLAG_IMMUTABLE);

        long supplyReminderTime = supplyDateCal.getTimeInMillis();

        //System.out.println("Supply reminder time = " + dateTimeManager.formatLongAsDateString(myContext, supplyReminderTime));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, supplyReminderTime, pillSupplyPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, supplyReminderTime, pillSupplyPendingIntent);
        }
    }
}
