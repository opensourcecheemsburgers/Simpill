package com.example.simpill;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import java.util.Objects;

public class DeviceBootReceiver extends BroadcastReceiver {

    int alarmCodeForAllAlarms = 0;

    AlarmSetter alarmSetter;
    AlarmManager alarmManager;
    DateTimeManager dateTimeManager;

    @SuppressLint("ShortAlarm")
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper myDatabase = new DatabaseHelper(context);

        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED") &&
                myDatabase.getRowCount() > 0) {

            Toast.makeText(context, context.getString(R.string.device_restart_toast), Toast.LENGTH_LONG).show();

            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Cursor cursor = myDatabase.readSqlDatabase();

            int lastRow = myDatabase.getRowCount() - 1;
            int currentRow;

            for (currentRow = 0; currentRow <= lastRow; currentRow++) {
                cursor.moveToPosition(currentRow);

                String pillName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                myDatabase.setIsReminderSet(pillName, 0);
                alarmSetter = new AlarmSetter(context, pillName);
                alarmSetter.setAlarms(alarmCodeForAllAlarms);
            }
        }
    }
}


