package com.example.simpill;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public class SharedPrefs {

    Simpill simpill = new Simpill();

    public void loadSharedPrefs(Context context) {
        simpill.setDarkDialogs
                (context.getSharedPreferences(Simpill.DARK_DIALOGS_FILENAME, Context.MODE_PRIVATE)
                        .getBoolean(Simpill.DARK_DIALOGS_TAG, (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES));

        simpill.setCustomTheme(context.getSharedPreferences(Simpill.SELECTED_THEME_FILENAME, Context.MODE_PRIVATE)
                .getInt(Simpill.USER_THEME_TAG, simpill.BLUE_THEME));

        simpill.setUserIs24Hr(context.getSharedPreferences(Simpill.IS_24HR_BOOLEAN_FILENAME, Context.MODE_PRIVATE)
                .getBoolean(Simpill.USER_IS_24HR_TAG, true));

        simpill.setUserPermanentNotifications(context.getSharedPreferences(Simpill.PERMANENT_NOTIFICATIONS_FILENAME, Context.MODE_PRIVATE)
                .getBoolean(Simpill.USER_PERMANENT_NOTIFICATIONS_TAG, false));

        simpill.setOpenCount(context.getSharedPreferences(Simpill.OPEN_COUNT_FILENAME, Context.MODE_PRIVATE).getInt(Simpill.OPEN_COUNT_TAG, 0));
    }

    public void setDarkDialogsPref(Context context, boolean darkDialogs) {
        context.getSharedPreferences(Simpill.DARK_DIALOGS_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Simpill.DARK_DIALOGS_TAG, darkDialogs)
                .apply();
    }

    public void setThemesPref(Context context, int theme) {
        context.getSharedPreferences(Simpill.SELECTED_THEME_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(Simpill.USER_THEME_TAG, theme)
                .apply();
    }

    public void setOpenCountPref(Context context, int openCount) {
        context.getSharedPreferences(Simpill.OPEN_COUNT_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(Simpill.OPEN_COUNT_TAG, openCount)
                .apply();
    }

    public void setStickyNotificationsPref(Context context, boolean stickyNotifications) {
        context.getSharedPreferences(Simpill.PERMANENT_NOTIFICATIONS_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Simpill.USER_PERMANENT_NOTIFICATIONS_TAG, stickyNotifications)
                .apply();
    }

    public void set24HourTimeFormatPref(Context context, boolean is24HourFormat) {
        context.getSharedPreferences(Simpill.IS_24HR_BOOLEAN_FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Simpill.USER_IS_24HR_TAG, is24HourFormat)
                .apply();
    }
}
