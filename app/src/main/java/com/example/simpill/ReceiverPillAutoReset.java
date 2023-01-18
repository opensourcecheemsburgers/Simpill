/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.Pill.PRIMARY_KEY_INTENT_KEY_STRING;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverPillAutoReset extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Pill pill =
                new DatabaseHelper(context)
                        .getPill(intent.getIntExtra(PRIMARY_KEY_INTENT_KEY_STRING, -1));
        pill.autoResetPill(context);
    }
}
