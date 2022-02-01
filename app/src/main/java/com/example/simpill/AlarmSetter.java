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
    Simpill simpill;
    AlarmManager alarmManager;
    DatabaseHelper myDatabase;
    DateTimeManager dateTimeManager;

    TimeZone userTimezone;
    String pillName;
    int primaryKey;
    Calendar[] pillTimesCalArray;
    Calendar supplyDateCal;

    AlarmSetter(Context myContext, String pillName, int primaryKey) {
        this.pillName = pillName;
        this.primaryKey = primaryKey;
        this.myContext = myContext;
    }

    private int formatPrimaryKeyAsRequestCode(int requestNumber) {
        return (int) (primaryKey * (Math.pow(10, 4))) + requestNumber;
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
        alarmManager = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);
        myDatabase = new DatabaseHelper(myContext);
        dateTimeManager = new DateTimeManager();

        userTimezone = dateTimeManager.getUserTimezone();
        pillTimesCalArray = getPillTimeCalendar();
        supplyDateCal = getReminderDateCalendar();
    }

    public boolean checkIfReminderSet() {
        return myDatabase.getIsReminderSet(pillName);
    }

    private Calendar getReminderDateCalendar() {
        return dateTimeManager.formatDateStringAsCalendar(myContext, userTimezone, myDatabase.getPillDate(pillName));
    }
    private Calendar[] getPillTimeCalendar() {

        String[] times =  myDatabase.getPillTime(pillName);
        Calendar[] calendars = new Calendar[times.length];

        for (int currentNumber = 0; currentNumber < times.length; currentNumber++) {
            calendars[currentNumber] = dateTimeManager.formatTimeStringAsCalendar(myContext, userTimezone, times[currentNumber]);
        }

        return calendars;
    }

    private void setPillReminder() {
        for (int currentNumber = 0; currentNumber < pillTimesCalArray.length; currentNumber++) {
            int requestCode = formatPrimaryKeyAsRequestCode(currentNumber);
            int frequency = myDatabase.getFrequency(pillName);


            Intent startPillAlarmReceiver = new Intent(myContext, ReceiverPillAlarm.class);
            startPillAlarmReceiver.putExtra(myContext.getString(R.string.pill_name), pillName);
            startPillAlarmReceiver.putExtra(myContext.getString(R.string.notification_id), requestCode);
            PendingIntent pillAlarmPendingIntent = PendingIntent.getBroadcast(myContext, requestCode, startPillAlarmReceiver, PendingIntent.FLAG_IMMUTABLE);

            /*
            if (checkIfReminderSet()) {
                System.out.println("Alarm set");
            }
            else {
                System.out.println("Alarm not set");
            }
            */

            long pillReminderTime =  pillTimesCalArray[currentNumber].getTimeInMillis();
            while (pillReminderTime <= System.currentTimeMillis()) {
                pillReminderTime = pillReminderTime + DAY_IN_MS;
            }

            //System.out.println("Pill reminder time = " + dateTimeManager.formatLongAsDateString(myContext, pillReminderTime));

            if (frequency == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, pillReminderTime, pillAlarmPendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, pillReminderTime, pillAlarmPendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, pillReminderTime, pillAlarmPendingIntent);
                }
            }
            else if (frequency > 1) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, pillReminderTime, DAY_IN_MS * frequency, pillAlarmPendingIntent);
            }
        }
    }
    private void setAutoReset() {
        if (pillTimesCalArray.length == 1) {
            int requestCode = formatPrimaryKeyAsRequestCode(0);

            Intent startAutoResetReceiver = new Intent(myContext, ReceiverPillAutoReset.class);
            startAutoResetReceiver.putExtra(myContext.getString(R.string.pill_name), pillName);
            startAutoResetReceiver.putExtra(myContext.getString(R.string.notification_id), requestCode);

            @SuppressLint("InlinedApi")
            PendingIntent autoResetPendingIntent = PendingIntent.getBroadcast(myContext, requestCode, startAutoResetReceiver, PendingIntent.FLAG_IMMUTABLE);

            long pillReminderTime = pillTimesCalArray[0].getTimeInMillis();
            long pillResetTime = pillReminderTime - HALF_DAY_IN_MS;

            while (pillResetTime <= System.currentTimeMillis()) {
                pillResetTime = pillResetTime + DAY_IN_MS;
            }

            System.out.println("Pill reset time = " + dateTimeManager.formatLongAsDateString(myContext, pillResetTime));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, pillResetTime, autoResetPendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

                Intent startAutoResetReceiver = new Intent(myContext, ReceiverPillAutoReset.class);
                startAutoResetReceiver.putExtra(myContext.getString(R.string.pill_name), pillName);
                startAutoResetReceiver.putExtra(myContext.getString(R.string.notification_id), requestCode);

                @SuppressLint("InlinedApi")
                PendingIntent autoResetPendingIntent = PendingIntent.getBroadcast(myContext, requestCode, startAutoResetReceiver, PendingIntent.FLAG_IMMUTABLE);



                Calendar currentNumberTime = pillTimesCalArray[currentNumber];
                System.out.println("Array [" + currentNumber + "] = " + dateTimeManager.formatLongAsTimeString(myContext, currentNumberTime.getTimeInMillis()));

                Calendar nextNumberTime = pillTimesCalArray[nextNumber];
                System.out.println("Array [" + nextNumber + "] = " + dateTimeManager.formatLongAsTimeString(myContext, nextNumberTime.getTimeInMillis()));

                System.out.println(nextNumberTime.getTimeInMillis());

                System.out.println(nextNumber);

                long resetTime = nextNumberTime.getTimeInMillis() - 600000L;

                while (resetTime <= System.currentTimeMillis()) {
                    resetTime = resetTime + DAY_IN_MS;
                }

                if (currentNumber == pillTimesCalArray.length - 1) {
                    resetTime = resetTime + DAY_IN_MS;
                }

                Calendar pillResetCal = Calendar.getInstance();
                pillResetCal.setTimeInMillis(resetTime);
                System.out.println(pillResetCal.getTime());

                //System.out.println("Pill reset time = " + dateTimeManager.formatLongAsDateString(myContext, pillResetTime));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, resetTime, autoResetPendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, resetTime, autoResetPendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, resetTime, autoResetPendingIntent);
                }
            }
        }
    }
    private void setSupplyReminder() {
        Intent startPillSupplyReceiver = new Intent (myContext, ReceiverPillSupply.class);
        startPillSupplyReceiver.putExtra(myContext.getString(R.string.pill_name), pillName);

        @SuppressLint("InlinedApi")
        PendingIntent pillSupplyPendingIntent = PendingIntent.getBroadcast(myContext, primaryKey, startPillSupplyReceiver, PendingIntent.FLAG_IMMUTABLE);

        long supplyReminderTime = supplyDateCal.getTimeInMillis();

        //System.out.println("Supply reminder time = " + dateTimeManager.formatLongAsDateString(myContext, supplyReminderTime));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, supplyReminderTime, pillSupplyPendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, supplyReminderTime, pillSupplyPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, supplyReminderTime, pillSupplyPendingIntent);
        }
    }
}
