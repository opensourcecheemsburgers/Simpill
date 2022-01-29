package com.example.simpill;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialogs extends AppCompatDialogFragment {

    Simpill simpill = new Simpill();
    Toasts toasts = new Toasts();
    PillDBHelper myDatabase;
    AlertDialog.Builder dialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    Dialog dialog;

    public Dialog getFrequencyDialog(Context context, int classNumber) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_choose_frequency);

        TextView multipleDailyTextView = dialogView.findViewById(R.id.multiple_daily);
        TextView dailyTextView = dialogView.findViewById(R.id.daily);
        TextView everyOtherDayTextView = dialogView.findViewById(R.id.every_other_day);
        TextView weeklyTextView = dialogView.findViewById(R.id.weekly);
        TextView customIntervalTextView = dialogView.findViewById(R.id.custom_interval);

        if (classNumber == 0) {
            CreatePill createPill = new CreatePill();

            multipleDailyTextView.setOnClickListener(view -> createPill.setIntervalInDays(0));
            dailyTextView.setOnClickListener(view -> createPill.setIntervalInDays(1));
            everyOtherDayTextView.setOnClickListener(view -> createPill.setIntervalInDays(2));
            weeklyTextView.setOnClickListener(view -> createPill.setIntervalInDays(7));
        }
        else if (classNumber == 1) {
            UpdatePill updatePill = new UpdatePill();

            multipleDailyTextView.setOnClickListener(view -> updatePill.setIntervalInDays(0));
            dailyTextView.setOnClickListener(view -> updatePill.setIntervalInDays(1));
            everyOtherDayTextView.setOnClickListener(view -> updatePill.setIntervalInDays(2));
            weeklyTextView.setOnClickListener(view -> updatePill.setIntervalInDays(7));
        }
        else {
            throw new IllegalArgumentException();
        }

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

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        Button okBtn = dialogView.findViewById(R.id.btnOk);

        Button blueThemeBtn = dialogView.findViewById(R.id.blue_theme_btn);
        Button greyThemeBtn = dialogView.findViewById(R.id.grey_theme_btn);
        Button purpleThemeBtn = dialogView.findViewById(R.id.purple_theme_btn);

        blueThemeBtn.setOnClickListener(view -> {
            simpill.setCustomTheme(simpill.BLUE_THEME);
            SharedPreferences.Editor editor = getContext().getSharedPreferences(Simpill.SELECTED_THEME, getContext().MODE_PRIVATE).edit();
            editor.putInt(Simpill.USER_THEME, simpill.getCustomTheme());
            editor.apply();
        });
        greyThemeBtn.setOnClickListener(view -> {
            simpill.setCustomTheme(simpill.GREY_THEME);
            SharedPreferences.Editor editor = getContext().getSharedPreferences(Simpill.SELECTED_THEME, getContext().MODE_PRIVATE).edit();
            editor.putInt(Simpill.USER_THEME, simpill.getCustomTheme());
            editor.apply();
        });
        purpleThemeBtn.setOnClickListener(view -> {
            simpill.setCustomTheme(simpill.PURPLE_THEME);
            SharedPreferences.Editor editor = getContext().getSharedPreferences(Simpill.SELECTED_THEME, getContext().MODE_PRIVATE).edit();
            editor.putInt(Simpill.USER_THEME, simpill.getCustomTheme());
            editor.apply();
        });
        okBtn.setOnClickListener(view -> {
            toasts.showCustomToast(context, getString(R.string.theme_applied));
        });

        return dialog;
    }

    public Dialog getChooseNameDialog(Context context) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_pill_name);

        super.onAttach(context);
        ExampleDialogListener exampleDialogListener = (ExampleDialogListener) context;

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        TextView titleMessageView = dialogView.findViewById(R.id.dialogMessageTextView);
        Button doneBtn = dialogView.findViewById(R.id.btnWelcome);
        EditText enterNameEditText = dialogView.findViewById(R.id.editTextTextPersonName2);

        doneBtn.setOnClickListener(view -> {
            exampleDialogListener.applyPillName(enterNameEditText.getText().toString());
            dialog.dismiss();
        });

        return dialog;
    }

    public Dialog getChooseAmountDialog(Context context) {
        init(context);
        setViewAndCreateDialog(R.layout.dialog_pill_name);

        super.onAttach(context);
        ExampleDialogListener exampleDialogListener = (ExampleDialogListener) context;

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        ImageView pillIcon = dialogView.findViewById(R.id.imageView13);
        Button doneBtn = dialogView.findViewById(R.id.btnWelcome);
        Button addBtn = dialogView.findViewById(R.id.addBtn);
        Button minusBtn = dialogView.findViewById(R.id.minusBtn);
        EditText enterAmountEditText = dialogView.findViewById(R.id.amountTextView);

        enterAmountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        doneBtn.setOnClickListener(view -> {
            exampleDialogListener.applyPillSupply(enterAmountEditText.getText().toString());
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

        exampleDialogListener.applyPillSupply(enterAmountEditText.getText().toString());

        return dialog;
    }

    private void init(Context context) {
        myDatabase = new PillDBHelper(context);
        dialogBuilder = new AlertDialog.Builder(context);
        inflater = LayoutInflater.from(context);
    }
    private void setViewAndCreateDialog(int layout) {
        dialogView = inflater.inflate(layout, null);
        dialogBuilder.setView(dialogView);

        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }

    public interface ExampleDialogListener {
        void applyPillName(String userPillName);
        void applyPillSupply(String pillSupply);
    }
}
