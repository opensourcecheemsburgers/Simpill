package com.example.simpill;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Dialogs extends AppCompatDialogFragment {


    Toasts toasts = new Toasts();
    DatabaseHelper myDatabase;
    AlertDialog.Builder dialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    Dialog dialog;

    public Dialog getPillResetDialog(Context context, String pillName, MainRecyclerViewAdapter.MyViewHolder holder, int position, MediaPlayer resetSoundPlayer) {
        init(context);

        if(isDarkDialogTheme(context)) {
            setViewAndCreateDialog(R.layout.dialog_reset_warning_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_reset_warning);
        }
        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        TextView titleMessageView = dialogView.findViewById(R.id.dialogMessageTextView);
        Button yesBtn = dialogView.findViewById(R.id.btnYes);
        Button cancelBtn = dialogView.findViewById(R.id.btnNo);

        titleTextView.setText(context.getString(R.string.reset_pill_dialog_title));
        titleMessageView.setText(context.getString(R.string.reset_pill_dialog_message, pillName));

        yesBtn.setOnClickListener(view -> {
            super.onDestroy();
            PillResetDialogListener pillResetDialogListener = (PillResetDialogListener) context;
            pillResetDialogListener.notifyAdapterOfResetPill(pillName, holder, position, resetSoundPlayer);
            dialog.dismiss();
        });
        cancelBtn.setOnClickListener(view -> dialog.dismiss());
        return dialog;
    }
    public Dialog getPillDeletionDialog(Context context, String pillName, int position) {
        init(context);

        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_delete_pill_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_delete_pill);
        }

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        TextView titleMessageView = dialogView.findViewById(R.id.dialogMessageTextView);
        titleMessageView.setText(context.getString(R.string.pill_deletion_dialog_message, pillName));

        Button yesBtn = dialogView.findViewById(R.id.btnYes);
        Button cancelBtn = dialogView.findViewById(R.id.btnNo);

        yesBtn.setOnClickListener(view -> {
            if (myDatabase.deletePill(pillName)) {
                super.onDestroy();
                toasts.showCustomToast(context, context.getString(R.string.append_pill_deleted_toast, pillName));
                PillDeleteDialogListener pillDeleteDialogListener = (PillDeleteDialogListener) context;
                pillDeleteDialogListener.notifyAdapterOfDeletedPill(position);
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(view -> dialog.dismiss());

        return dialog;
    }
    public Dialog getDatabaseDeletionDialog(Context context) {
        init(context);

        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_db_reset_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_db_reset);
        }

        Button resetDbBtn = dialogView.findViewById(R.id.btnYes);
        Button cancelBtn = dialogView.findViewById(R.id.btnNo);

        resetDbBtn.setOnClickListener(view -> {
            myDatabase.deleteDatabase();
            dialog.dismiss();
            toasts.showCustomToast(context, context.getString(R.string.pill_db_deleted_toast));
        });
        cancelBtn.setOnClickListener(view -> dialog.dismiss());

        return dialog;
    }



    public Dialog getPastDateDialog(Context context) {
        init(context);

        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_past_date_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_past_date);
        }

        Button okBtn = dialogView.findViewById(R.id.btnOk);
        okBtn.setOnClickListener(view -> dialog.dismiss());

        return dialog;
    }
    public Dialog getWelcomeDialog(Context context) {
        init(context);

        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_welcome_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_welcome);
        }

        Button welcomeBtn = dialogView.findViewById(R.id.done_btn);
        welcomeBtn.setOnClickListener(view -> dialog.dismiss());

        return dialog;
    }

    public Dialog getChooseThemeDialog(Context context) {
        SharedPrefs sharedPrefs = new SharedPrefs();

        init(context);

        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_choose_theme_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_choose_theme);
        }

        super.onDestroy();
        SettingsDialogListener settingsDialogListener = (SettingsDialogListener) context;

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);

        ImageButton blueThemeBtn = dialogView.findViewById(R.id.blue_theme_btn);
        ImageButton greyThemeBtn = dialogView.findViewById(R.id.grey_theme_btn);
        ImageButton purpleThemeBtn = dialogView.findViewById(R.id.purple_theme_btn);
        ImageButton blackThemeBtn = dialogView.findViewById(R.id.black_theme_btn);

        blueThemeBtn.setOnClickListener(view -> {
            sharedPrefs.setThemesPref(context, Simpill.BLUE_THEME);
            toasts.showCustomToast(context, context.getString(R.string.theme_applied, context.getString(R.string.blue)));
            ((SettingsDialogListener) context).recreateScreen();
            dialog.dismiss();
        });
        greyThemeBtn.setOnClickListener(view -> {
            sharedPrefs.setThemesPref(context, Simpill.GREY_THEME);
            toasts.showCustomToast(context, context.getString(R.string.theme_applied, context.getString(R.string.grey)));
            settingsDialogListener.recreateScreen();
            dialog.dismiss();
        });
        purpleThemeBtn.setOnClickListener(view -> {
            sharedPrefs.setThemesPref(context, Simpill.PURPLE_THEME);
            toasts.showCustomToast(context, context.getString(R.string.theme_applied, context.getString(R.string.purple)));
            settingsDialogListener.recreateScreen();
            dialog.dismiss();
        });
        blackThemeBtn.setOnClickListener(view -> {
            sharedPrefs.setThemesPref(context, Simpill.BLACK_THEME);
            toasts.showCustomToast(context, context.getString(R.string.theme_applied, context.getString(R.string.dark)));
            settingsDialogListener.recreateScreen();
            dialog.dismiss();
        });
        return dialog;
    }

    public Dialog getChooseNameDialog(Context context) {
        init(context);

        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_pill_name_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_pill_name);
        }

        super.onAttach(context);
        PillNameDialogListener pillNameDialogListener = (PillNameDialogListener) context;

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        TextView titleMessageView = dialogView.findViewById(R.id.dialogMessageTextView);
        Button doneBtn = dialogView.findViewById(R.id.done_btn);
        EditText enterNameEditText = dialogView.findViewById(R.id.editTextTextPersonName2);

        doneBtn.setOnClickListener(view -> {
            pillNameDialogListener.applyPillName(enterNameEditText.getText().toString());
            dialog.dismiss();
        });

        return dialog;
    }
    public Dialog getChooseSupplyAmountDialog(Context context) {
        init(context);
        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_pill_amount_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_pill_amount);
        }

        super.onAttach(context);
        PillAmountDialogListener pillAmountDialogListener = (PillAmountDialogListener) context;

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        ImageView pillIcon = dialogView.findViewById(R.id.imageView13);
        Button doneBtn = dialogView.findViewById(R.id.done_btn);
        Button addBtn = dialogView.findViewById(R.id.addBtn);
        Button minusBtn = dialogView.findViewById(R.id.minusBtn);
        EditText enterAmountEditText = dialogView.findViewById(R.id.calendar_btn);

        enterAmountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        doneBtn.setOnClickListener(view -> {
            pillAmountDialogListener.applyPillSupply(enterAmountEditText.getText().toString());
            dialog.dismiss();
        });
        addBtn.setOnClickListener(view -> {
            int pillAmount;
            if (enterAmountEditText.getText().toString().equals("")) {
                pillAmount = 30;
            }
            else {
                pillAmount = Integer.parseInt(enterAmountEditText.getText().toString());
            }
            enterAmountEditText.setText(String.valueOf(pillAmount + 1));
        });
        minusBtn.setOnClickListener(view -> {
            int pillAmount;
            if (enterAmountEditText.getText().toString().equals("")) {
                pillAmount = 30;
            }
            else {
                pillAmount = Integer.parseInt(enterAmountEditText.getText().toString());
            }
            if(!(pillAmount - 1 <= 0)) {
                enterAmountEditText.setText(String.valueOf(pillAmount - 1));
            }
        });

        pillAmountDialogListener.applyPillSupply(enterAmountEditText.getText().toString());

        return dialog;
    }
    public Dialog getChooseReminderAmountDialog(Context context) {
        init(context);
        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_choose_reminder_amount_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_choose_reminder_amount);
        }

        Button doneBtn = dialogView.findViewById(R.id.done_btn);
        Button addBtn = dialogView.findViewById(R.id.addBtn);
        Button minusBtn = dialogView.findViewById(R.id.minusBtn);
        EditText enterAmountEditText = dialogView.findViewById(R.id.calendar_btn);

        enterAmountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        PillReminderAmountDialogListener pillReminderAmountDialogListener = (PillReminderAmountDialogListener) context;

        doneBtn.setOnClickListener(view -> {
            dialog.dismiss();
            pillReminderAmountDialogListener.applyNumberOfReminders(Integer.parseInt(enterAmountEditText.getText().toString()));
            getChooseTimesDialog(context, Integer.parseInt(enterAmountEditText.getText().toString())).show();
        });

        addBtn.setOnClickListener(view -> {
            int reminderAmount;

            if (enterAmountEditText.getText().toString().equals("")) {
                reminderAmount = 30;
            } else {
                reminderAmount = Integer.parseInt(enterAmountEditText.getText().toString());

            enterAmountEditText.setText(String.valueOf(reminderAmount + 1));
            }
        });
        minusBtn.setOnClickListener(view -> {
            int pillAmount;
            if (enterAmountEditText.getText().toString().equals("")) {
                pillAmount = 30;
            }
            else {
                pillAmount = Integer.parseInt(enterAmountEditText.getText().toString());
            }
            if(!(pillAmount - 1 <= 0)) {
                enterAmountEditText.setText(String.valueOf(pillAmount - 1));
            }
        });

        return dialog;
    }
    public Dialog getChooseTimesDialog(Context context, int clocks) {
        init(context);
        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_choose_times_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_choose_times);
        }

        Button doneBtn = dialogView.findViewById(R.id.btnDone);

        TimesRecyclerViewAdapter timesRecyclerViewAdapter = new TimesRecyclerViewAdapter(context, clocks);
        RecyclerView recyclerView = dialogView.findViewById(R.id.times_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(clocks+1);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(timesRecyclerViewAdapter);

        ChooseTimesDialogListener chooseTimesDialogListener = (ChooseTimesDialogListener) context;

        doneBtn.setOnClickListener(view -> {
            if (timesRecyclerViewAdapter.checkForEmptyTimes()) {
                toasts.showCustomToast(context, context.getString(R.string.time_enter));
            }
            else if(timesRecyclerViewAdapter.checkForAdjacentTimes()){
                toasts.showCustomToast(context, context.getString(R.string.time_warning_toast));
            }
            else {
                dialog.dismiss();
                chooseTimesDialogListener.returnTimesStringArray(timesRecyclerViewAdapter.returnTimeStringsArrayFromRecyclerViewClass());
            }
        });

        return dialog;
    }
    public Dialog getFrequencyDialog(Context context) {
        init(context);
        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_choose_frequency_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_choose_frequency);
        }

        TextView multipleDailyTextView = dialogView.findViewById(R.id.multiple_daily);
        TextView dailyTextView = dialogView.findViewById(R.id.daily);
        TextView everyOtherDayTextView = dialogView.findViewById(R.id.every_other_day);
        TextView weeklyTextView = dialogView.findViewById(R.id.weekly);
        TextView customIntervalTextView = dialogView.findViewById(R.id.custom_interval);

        multipleDailyTextView.setOnClickListener(view -> onClickFrequency(context, DatabaseHelper.MULTIPLE_DAILY));
        dailyTextView.setOnClickListener(view -> onClickFrequency(context, DatabaseHelper.DAILY));
        everyOtherDayTextView.setOnClickListener(view -> onClickFrequency(context, DatabaseHelper.EVERY_OTHER_DAY));
        weeklyTextView.setOnClickListener(view -> onClickFrequency(context, DatabaseHelper.WEEKLY));
        customIntervalTextView.setOnClickListener(view -> getCustomIntervalDialog(context));

        return dialog;
    }
    private void onClickFrequency(Context context, int frequency) {
        dialog.dismiss();
        ChooseFrequencyDialogListener chooseFrequencyDialogListener = (ChooseFrequencyDialogListener) context;
        chooseFrequencyDialogListener.setInterval(frequency);

        if(frequency == 0) {
            getChooseReminderAmountDialog(context).show();
        }
        else {
            chooseFrequencyDialogListener.openTimePicker(frequency);
        }
    }
    public Dialog getCustomIntervalDialog(Context context) {
        init(context);
        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_choose_interval_dark);
        } else {
            setViewAndCreateDialog(R.layout.dialog_choose_interval);
        }

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        ImageView pillIcon = dialogView.findViewById(R.id.imageView13);
        Button doneBtn = dialogView.findViewById(R.id.done_btn);
        Button addBtn = dialogView.findViewById(R.id.addBtn);
        Button minusBtn = dialogView.findViewById(R.id.minusBtn);
        EditText enterAmountEditText = dialogView.findViewById(R.id.calendar_btn);

        enterAmountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        ChooseFrequencyDialogListener chooseFrequencyDialogListener = (ChooseFrequencyDialogListener) context;

        addBtn.setOnClickListener(view -> {
            int days;
            if (enterAmountEditText.getText().toString().equals("")) {
                days = 2;
            }
            else {
                days = Integer.parseInt(enterAmountEditText.getText().toString()) + 1;
            }
            enterAmountEditText.setText(String.valueOf(days));
        });
        minusBtn.setOnClickListener(view -> {
            int days;
            if (enterAmountEditText.getText().toString().equals("")) {
                days = 2;
            }
            else if (Integer.parseInt(enterAmountEditText.getText().toString()) > 1) {
                days = Integer.parseInt(enterAmountEditText.getText().toString()) - 1;
            }
            else {
                days = Integer.parseInt(enterAmountEditText.getText().toString());
            }
            enterAmountEditText.setText(String.valueOf(days));
        });

        doneBtn.setOnClickListener(view -> {
            chooseFrequencyDialogListener.setInterval(Integer.parseInt(enterAmountEditText.getText().toString()));
            chooseFrequencyDialogListener.openTimePicker(Integer.parseInt(enterAmountEditText.getText().toString()));
        });
        return dialog;
    }

    public Dialog getStartDateDialog(Context context) {
        init(context);
        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_choose_start_date_dark);
        } else {
            setViewAndCreateDialog(R.layout.dialog_choose_start_date);
        }

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        ImageButton calendarBtn = dialogView.findViewById(R.id.calendar_btn);
        TextView dateTextView = dialogView.findViewById(R.id.user_date_textview);
        Button doneBtn = dialogView.findViewById(R.id.done_btn);

        GetStartDateDialogListener getStartDateDialogListener = (GetStartDateDialogListener) context;

        int defaultYear, defaultMonth, defaultDay;

        Calendar calendar = Calendar.getInstance();

        defaultYear = calendar.get(Calendar.YEAR);
        defaultMonth = calendar.get(Calendar.MONTH);
        defaultDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, DatePickerDialog.THEME_HOLO_LIGHT, (view, year, month, day) -> {
            month = month + 1;
            String selectedDate = year + "-" + month + "-" + day;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_format));

            try {
                simpleDateFormat.parse(selectedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            getStartDateDialogListener.applyStartDate(selectedDate);

            dateTextView.setText(new DateTimeManager().convertISODateStringToLocallyFormattedString(context, selectedDate));
        }, defaultYear, defaultMonth, defaultDay);

        calendarBtn.setOnClickListener(view -> datePickerDialog.show());

        doneBtn.setOnClickListener(view -> {
            dialog.dismiss();
        });
        return dialog;
    }


    public Dialog getDonationDialog(Context context) {
        init(context);
        if (isDarkDialogTheme(context)){
            setViewAndCreateDialog(R.layout.dialog_donate_dark);
        }
        else {
            setViewAndCreateDialog(R.layout.dialog_donate);
        }

        ClipboardHelper clipboardHelper = new ClipboardHelper();

        ImageButton paypalDonation = dialogView.findViewById(R.id.imageButton);
        TextView paypalDonationTextView = dialogView.findViewById(R.id.textView5);
        ImageButton playStoreBtn = dialogView.findViewById(R.id.imageButton3);
        TextView playStoreTextView = dialogView.findViewById(R.id.textView6);
        ImageButton moneroBtn = dialogView.findViewById(R.id.imageButton4);
        TextView moneroTextView = dialogView.findViewById(R.id.textView7);
        Button dismissBtn = dialogView.findViewById(R.id.dismiss_btn);

        paypalDonation.setOnClickListener(view -> openPaypalDonation(context));
        paypalDonationTextView.setOnClickListener(view -> openPaypalDonation(context));
        playStoreBtn.setOnClickListener(view -> openPaidSimpillLink(context));
        playStoreTextView.setOnClickListener(view -> openPaidSimpillLink(context));
        dismissBtn.setOnClickListener(view -> dialog.dismiss());
        moneroBtn.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(context, 2));
        moneroTextView.setOnClickListener(view -> clipboardHelper.copyAddressToClipboard(context, 2));
        return dialog;
    }
    private void openPaypalDonation(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.paypal_donation_link))));
    }
    private void openPaidSimpillLink(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.simpill_paid_version_link))));
    }



    private void init(Context context) {
        myDatabase = new DatabaseHelper(context);
        dialogBuilder = new AlertDialog.Builder(context);
        inflater = LayoutInflater.from(context);
    }
    private void setViewAndCreateDialog(int layout) {
        dialogView = inflater.inflate(layout, null);
        dialogBuilder.setView(dialogView);

        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }

    private boolean isDarkDialogTheme(Context context) {
        return new SharedPrefs().getDarkDialogsPref(context);
    }

    public interface PillNameDialogListener {
        void applyPillName(String userPillName);
    }
    public interface PillAmountDialogListener {
        void applyPillSupply(String pillSupply);
    }
    public interface SettingsDialogListener {
        void recreateScreen();
    }
    public interface PillDeleteDialogListener {
        void notifyAdapterOfDeletedPill(int position);
    }
    public interface PillResetDialogListener {
        void notifyAdapterOfResetPill(String pillName, MainRecyclerViewAdapter.MyViewHolder holder, int position, MediaPlayer resetSoundPlayer);
    }
    public interface ChooseFrequencyDialogListener {
        void openTimePicker(int frequency);
        void setInterval(int intervalInDays);
    }
    public interface PillReminderAmountDialogListener {
        void applyNumberOfReminders(int reminders);
    }
    public interface ChooseTimesDialogListener {
        void returnTimesStringArray(String[] times);
    }
    public interface GetStartDateDialogListener {
        void applyStartDate(String startDate);
    }
}
