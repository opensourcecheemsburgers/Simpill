package com.example.simpill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PillAutoResetReceiver extends BroadcastReceiver {

    public static final int taken = 1;
    public static final int notTaken = 0;
    private static final int alarmCodeForAutoReset = 2;
    AlarmSetter alarmSetter;
    PillDBHelper myDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("autoreset begin");

        String pillName = intent.getStringExtra(context.getString(R.string.pill_name));
        int notificationCode = intent.getIntExtra(context.getString(R.string.notification_id), -1);
        myDatabase = new PillDBHelper(context);

        if (myDatabase.getPillName(pillName) != null && myDatabase.getPillName(pillName).equals(pillName))
        {
            myDatabase.setAlarmsSet(pillName, 0);
            alarmSetter = new AlarmSetter(context, pillName, notificationCode);
            alarmSetter.setAlarms(alarmCodeForAutoReset);
            if (myDatabase.getIsTaken(pillName) == taken) {
                    myDatabase.setIsTaken(pillName, notTaken);
                    myDatabase.setTimeTaken(pillName, context.getString(R.string.nullString));
                    System.out.println("autoreset complete");
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
            }
        }
    }
}