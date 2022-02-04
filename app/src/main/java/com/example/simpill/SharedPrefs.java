package com.example.simpill;

import android.content.Context;
import android.content.res.Configuration;

public class SharedPrefs {


    public void setDarkDialogsPref(Context context, boolean darkDialogs) {
        context.getSharedPreferences(Simpill.DARK_DIALOGS_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Simpill.DARK_DIALOGS_TAG, darkDialogs)
                .apply();
    }
    public boolean getDarkDialogsPref(Context context) {
        return context.getSharedPreferences(Simpill.DARK_DIALOGS_FILENAME, Context.MODE_PRIVATE)
                .getBoolean(Simpill.DARK_DIALOGS_TAG, (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
    }

    public void setThemesPref(Context context, int theme) {
        context.getSharedPreferences(Simpill.SELECTED_THEME_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(Simpill.USER_THEME_TAG, theme)
                .apply();
    }
    public int getThemesPref(Context context) {
        return context.getSharedPreferences(Simpill.SELECTED_THEME_FILENAME, Context.MODE_PRIVATE).getInt(Simpill.USER_THEME_TAG, Simpill.BLUE_THEME);
    }

    public void setOpenCountPref(Context context, int openCount) {
        context.getSharedPreferences(Simpill.OPEN_COUNT_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(Simpill.OPEN_COUNT_TAG, openCount)
                .apply();
    }
    public int getOpenCountPref(Context context) {
        return context.getSharedPreferences(Simpill.OPEN_COUNT_FILENAME, Context.MODE_PRIVATE).getInt(Simpill.OPEN_COUNT_TAG, 0);
    }

    public void setStickyNotificationsPref(Context context, boolean stickyNotifications) {
        context.getSharedPreferences(Simpill.PERMANENT_NOTIFICATIONS_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Simpill.USER_PERMANENT_NOTIFICATIONS_TAG, stickyNotifications)
                .apply();
    }
    public boolean getStickyNotificationsPref(Context context)  {
        return context.getSharedPreferences(Simpill.PERMANENT_NOTIFICATIONS_FILENAME, Context.MODE_PRIVATE).getBoolean(Simpill.USER_PERMANENT_NOTIFICATIONS_TAG, false);
    }

    public void set24HourTimeFormatPref(Context context, boolean is24HourFormat) {
        context.getSharedPreferences(Simpill.IS_24HR_BOOLEAN_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Simpill.USER_IS_24HR_TAG, is24HourFormat)
                .apply();
    }
    public boolean get24HourFormatPref(Context context)  {
        return context.getSharedPreferences(Simpill.IS_24HR_BOOLEAN_FILENAME, Context.MODE_PRIVATE).getBoolean(Simpill.USER_IS_24HR_TAG, true);
    }

}
