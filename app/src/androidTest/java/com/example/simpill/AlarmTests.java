/* (C) 2022 */
package com.example.simpill;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AlarmTests {

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    final Pill[] pills = new DatabaseTest().getTestPills();

    @Test
    public void setAlarms() {
        for (Pill pill : pills) {
            pill.setAlarm(context);
            pill.setStockupAlarm(context);
        }
    }
}
