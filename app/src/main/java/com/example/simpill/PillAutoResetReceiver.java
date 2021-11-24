package com.example.simpill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PillAutoResetReceiver extends BroadcastReceiver {

    public static final int taken = 1;
    public static final int notTaken = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("autoreset begin");

        if (!intent.hasExtra(context.getString(R.string.pill_name))) {
            System.out.println("Intent is null");
        } else {
            String pillName = intent.getStringExtra(context.getString(R.string.pill_name));

            PillDBHelper myDatabase = new PillDBHelper(context);

            if (!myDatabase.getPillName(pillName).equals("null")) {
                if (myDatabase.getIsTaken(pillName) == taken) {
                    myDatabase.setIsTaken(pillName, notTaken);
                    myDatabase.setTimeTaken(pillName, context.getString(R.string.nullString));
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.notifyRecyclerView();
                    System.out.println("autoreset complete");
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }
        }
    }
}