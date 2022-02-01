package com.example.simpill;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Dialogs extends AppCompatDialogFragment {

    Simpill simpill = new Simpill();
    Toasts toasts = new Toasts();
    DatabaseHelper myDatabase;
    AlertDialog.Builder dialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    Dialog dialog;

    public Dialog getPillResetDialog(Context context, String pillName, MainRecyclerViewAdapter.MyViewHolder holder, int position, MediaPlayer resetSoundPlayer) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_reset_warning);

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        TextView titleMessageView = dialogView.findViewById(R.id.dialogMessageTextView);
        Button yesBtn = dialogView.findViewById(R.id.btnYes);
        Button cancelBtn = dialogView.findViewById(R.id.btnNo);

        yesBtn.setOnClickListener(view -> {
            super.onDestroy();
            PillResetDialogListener pillResetDialogListener = (PillResetDialogListener) context;
            pillResetDialogListener.notifyAdapterOfResetPill(pillName, holder, position, resetSoundPlayer);
            toasts.showCustomToast(context, pillName + " " + context.getString(R.string.pill_reset_toast));
            dialog.dismiss();
        });
        cancelBtn.setOnClickListener(view -> dialog.dismiss());
        return dialog;
    }
    public Dialog getPillDeletionDialog(Context context, String pillName, int position) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_delete_pill);

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        TextView titleMessageView = dialogView.findViewById(R.id.dialogMessageTextView);
        Button yesBtn = dialogView.findViewById(R.id.btnYes);
        Button cancelBtn = dialogView.findViewById(R.id.btnNo);

        yesBtn.setOnClickListener(view -> {
            if (myDatabase.deletePill(pillName)) {
                super.onDestroy();
                toasts.showCustomToast(context, pillName + " " + context.getString(R.string.append_pill_deleted_toast));
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
        setViewAndCreateDialog(R.layout.dialog_db_reset);

        Button resetDbBtn = dialogView.findViewById(R.id.btnYes);
        Button cancelBtn = dialogView.findViewById(R.id.btnNo);

        resetDbBtn.setOnClickListener(view -> {
            myDatabase.deleteDatabase();
            dialog.dismiss();
            new Toasts().showCustomToast(context, context.getString(R.string.pill_db_deleted_toast));
        });
        cancelBtn.setOnClickListener(view -> dialog.dismiss());

        return dialog;
    }

    public Dialog getFrequencyDialog(Context context, int classNumber) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_choose_frequency);

        TextView multipleDailyTextView = dialogView.findViewById(R.id.multiple_daily);
        TextView dailyTextView = dialogView.findViewById(R.id.daily);
        TextView everyOtherDayTextView = dialogView.findViewById(R.id.every_other_day);
        TextView weeklyTextView = dialogView.findViewById(R.id.weekly);
        TextView customIntervalTextView = dialogView.findViewById(R.id.custom_interval);

        ChooseFrequencyDialogListener chooseFrequencyDialogListener = (ChooseFrequencyDialogListener) context;

        if (classNumber == 0) {
            CreatePill createPill = new CreatePill();

            multipleDailyTextView.setOnClickListener(view -> {
                createPill.setIntervalInDays(0);
                dialog.dismiss();
                getChooseReminderAmountDialog(context, classNumber).show();
            });
            dailyTextView.setOnClickListener(view -> {
                createPill.setIntervalInDays(1);
                chooseFrequencyDialogListener.openTimePicker(1);
                dialog.dismiss();
                toasts.showCustomToast(context, String.valueOf(createPill.getIntervalInDays()));
            });
            everyOtherDayTextView.setOnClickListener(view ->{
                createPill.setIntervalInDays(2);
                chooseFrequencyDialogListener.openTimePicker(2);
                dialog.dismiss();
                toasts.showCustomToast(context, String.valueOf(createPill.getIntervalInDays()));
            });
            weeklyTextView.setOnClickListener(view -> {
                createPill.setIntervalInDays(7);
                chooseFrequencyDialogListener.openTimePicker(7);
                dialog.dismiss();
                toasts.showCustomToast(context, String.valueOf(createPill.getIntervalInDays()));
            });
            customIntervalTextView.setOnClickListener(view -> {
                dialog.dismiss();
                getCustomDialog(context, classNumber).show();
            });
        }
        else if (classNumber == 1) {
            UpdatePill updatePill = new UpdatePill();

            multipleDailyTextView.setOnClickListener(view -> {
                updatePill.setIntervalInDays(0);
                getChooseReminderAmountDialog(context, classNumber).show();
            });
            dailyTextView.setOnClickListener(view -> {
                updatePill.setIntervalInDays(1);
                chooseFrequencyDialogListener.openTimePicker(1);
            });
            everyOtherDayTextView.setOnClickListener(view ->{
                updatePill.setIntervalInDays(2);
                chooseFrequencyDialogListener.openTimePicker(2);
            });
            weeklyTextView.setOnClickListener(view -> {
                updatePill.setIntervalInDays(7);
                chooseFrequencyDialogListener.openTimePicker(7);
            });
            customIntervalTextView.setOnClickListener(view -> getCustomDialog(context, classNumber).show());
        }
        else {
            throw new IllegalArgumentException();
        }

        return dialog;
    }
    public Dialog getCustomDialog(Context context, int classNumber) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_choose_interval);

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        ImageView pillIcon = dialogView.findViewById(R.id.imageView13);
        Button doneBtn = dialogView.findViewById(R.id.btnWelcome);
        Button addBtn = dialogView.findViewById(R.id.addBtn);
        Button minusBtn = dialogView.findViewById(R.id.minusBtn);
        EditText enterAmountEditText = dialogView.findViewById(R.id.amountTextView);

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
            if (classNumber == 0) {
                CreatePill createPill = new CreatePill();
                createPill.setIntervalInDays(Integer.parseInt(enterAmountEditText.getText().toString()));
                dialog.dismiss();
                toasts.showCustomToast(context, String.valueOf(createPill.getIntervalInDays()));
            }
            else if (classNumber == 1) {
                UpdatePill updatePill = new UpdatePill();
                updatePill.setIntervalInDays(Integer.parseInt(enterAmountEditText.getText().toString()));
                dialog.dismiss();
                toasts.showCustomToast(context, String.valueOf(updatePill.getIntervalInDays()));
            }
            chooseFrequencyDialogListener.openTimePicker(Integer.parseInt(enterAmountEditText.getText().toString()));
        });
        return dialog;
    }
    public Dialog getExtraReminderDialog(Context context, int frequency) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_extra_reminder);

        Button yesBtn = dialogView.findViewById(R.id.btnYes);
        Button noBtn = dialogView.findViewById(R.id.btnNo);

        ChooseFrequencyDialogListener chooseFrequencyDialogListener = (ChooseFrequencyDialogListener) context;

        yesBtn.setOnClickListener(view -> {
            chooseFrequencyDialogListener.openTimePicker(frequency);
        });

        noBtn.setOnClickListener(view -> dialog.dismiss());

        return dialog;
    }


    public Dialog getPastDateDialog(Context context) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_past_date);

        Button okBtn = dialogView.findViewById(R.id.btnOk);
        okBtn.setOnClickListener(view -> dialog.dismiss());

        return dialog;
    }

    public Dialog getWelcomeDialog(Context context) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_welcome);

        Button welcomeBtn = dialogView.findViewById(R.id.btnWelcome);
        welcomeBtn.setOnClickListener(view -> dialog.dismiss());

        return dialog;
    }

    public Dialog getChooseThemeDialog(Context context) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_choose_theme);

        super.onDestroy();
        SettingsDialogListener settingsDialogListener = (SettingsDialogListener) context;

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);

        ImageButton blueThemeBtn = dialogView.findViewById(R.id.blue_theme_btn);
        ImageButton greyThemeBtn = dialogView.findViewById(R.id.grey_theme_btn);
        ImageButton purpleThemeBtn = dialogView.findViewById(R.id.purple_theme_btn);

        blueThemeBtn.setOnClickListener(view -> {
            simpill.setCustomTheme(simpill.BLUE_THEME);
            SharedPreferences.Editor editor = context.getSharedPreferences(Simpill.SELECTED_THEME, Context.MODE_PRIVATE).edit();
            editor.putInt(Simpill.USER_THEME, simpill.getCustomTheme());
            editor.apply();
            toasts.showCustomToast(context, context.getString(R.string.theme_applied));
            settingsDialogListener.recreateScreen();
            dialog.dismiss();
        });
        greyThemeBtn.setOnClickListener(view -> {
            simpill.setCustomTheme(simpill.GREY_THEME);
            SharedPreferences.Editor editor = context.getSharedPreferences(Simpill.SELECTED_THEME, Context.MODE_PRIVATE).edit();
            editor.putInt(Simpill.USER_THEME, simpill.getCustomTheme());
            editor.apply();
            toasts.showCustomToast(context, context.getString(R.string.theme_applied));
            settingsDialogListener.recreateScreen();
            dialog.dismiss();
        });
        purpleThemeBtn.setOnClickListener(view -> {
            simpill.setCustomTheme(simpill.PURPLE_THEME);
            SharedPreferences.Editor editor = context.getSharedPreferences(Simpill.SELECTED_THEME, Context.MODE_PRIVATE).edit();
            editor.putInt(Simpill.USER_THEME, simpill.getCustomTheme());
            editor.apply();
            toasts.showCustomToast(context, context.getString(R.string.theme_applied));
            settingsDialogListener.recreateScreen();
            dialog.dismiss();
        });
        return dialog;
    }

    public Dialog getChooseNameDialog(Context context) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_pill_name);

        super.onAttach(context);
        PillNameDialogListener pillNameDialogListener = (PillNameDialogListener) context;

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        TextView titleMessageView = dialogView.findViewById(R.id.dialogMessageTextView);
        Button doneBtn = dialogView.findViewById(R.id.btnWelcome);
        EditText enterNameEditText = dialogView.findViewById(R.id.editTextTextPersonName2);

        doneBtn.setOnClickListener(view -> {
            pillNameDialogListener.applyPillName(enterNameEditText.getText().toString());
            dialog.dismiss();
        });

        return dialog;
    }

    public Dialog getChooseSupplyAmountDialog(Context context) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_pill_amount);

        super.onAttach(context);
        PillAmountDialogListener pillAmountDialogListener = (PillAmountDialogListener) context;

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        ImageView pillIcon = dialogView.findViewById(R.id.imageView13);
        Button doneBtn = dialogView.findViewById(R.id.btnWelcome);
        Button addBtn = dialogView.findViewById(R.id.addBtn);
        Button minusBtn = dialogView.findViewById(R.id.minusBtn);
        EditText enterAmountEditText = dialogView.findViewById(R.id.amountTextView);

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

    public Dialog getChooseReminderAmountDialog(Context context, int classNumber) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_choose_reminder_amount);

        Button doneBtn = dialogView.findViewById(R.id.btnWelcome);
        Button addBtn = dialogView.findViewById(R.id.addBtn);
        Button minusBtn = dialogView.findViewById(R.id.minusBtn);
        EditText enterAmountEditText = dialogView.findViewById(R.id.amountTextView);

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
        setViewAndCreateDialog(R.layout.dialog_choose_times);

        Button doneBtn = dialogView.findViewById(R.id.btnDone);

        TimesRecyclerViewAdapter timesRecyclerViewAdapter = new TimesRecyclerViewAdapter(context, clocks);
        RecyclerView recyclerView = dialogView.findViewById(R.id.times_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(clocks+1);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(timesRecyclerViewAdapter);

        ChooseTimesDialogListener chooseTimesDialogListener = (ChooseTimesDialogListener) context;

        doneBtn.setOnClickListener(view -> {
            dialog.dismiss();
            chooseTimesDialogListener.returnTimesStringArray(timesRecyclerViewAdapter.returnTimeStringsArrayFromRecyclerViewClass());
        });

        return dialog;
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


    public TimePickerDialog getTimePickerDialog(Context context, int position) {
        TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, selectedHour, selectedMinute) -> {
            DateTimeManager dateTimeManager = new DateTimeManager();
            String amOrPm;
            String time;

            if (!simpill.getUserIs24Hr()) {
                if (selectedHour > 12) {
                    amOrPm = "pm";
                    selectedHour = selectedHour - 12;
                } else if (selectedHour == 12) {
                    amOrPm = "pm";
                } else if (selectedHour == 0) {
                    selectedHour = selectedHour + 12;
                    amOrPm = "am";
                } else {
                    amOrPm = "am";
                }
                if (selectedMinute < 10) {
                    time = selectedHour + ":0" + selectedMinute + " " + amOrPm;
                } else {
                    time = selectedHour + ":" + selectedMinute + " " + amOrPm;
                }
                time = dateTimeManager.convert12HrTimeTo24HrTime(context, time);
            } else {
                if (selectedMinute < 10) {
                    time = selectedHour + ":0" + selectedMinute;
                } else {
                    time = selectedHour + ":" + selectedMinute;
                }
                if (selectedHour < 10) {
                    time = "0" + selectedHour + ":" + selectedMinute;
                }
                if (selectedHour < 10 && selectedMinute < 10) {
                    time = "0" + selectedHour + ":0" + selectedMinute;
                }
            }
        };



        TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.MyTimePickerDialogStyle, timeSetListener, 12, 0, simpill.getUserIs24Hr());

        return timePickerDialog;
    }

    public interface TimePickerDialogListener {
        void applySelectedTimeToArray(String time, int position);
        void applySelectedTimeToTextView(TimesRecyclerViewAdapter.MyViewHolder holder, String time, int position);
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
    }
    public interface PillReminderAmountDialogListener {
        void applyNumberOfReminders(int reminders);
    }
    public interface ChooseTimesDialogListener {
        void returnTimesStringArray(String[] times);
    }
}
