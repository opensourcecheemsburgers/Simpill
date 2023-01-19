/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.Pill.PRIMARY_KEY_INTENT_KEY_STRING;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverPillAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Pill pill = databaseHelper.getPill(intent.getIntExtra(PRIMARY_KEY_INTENT_KEY_STRING, -1));
        pill.sendPillNotification(context);
        pill.setAlarm(context);
    }
}
