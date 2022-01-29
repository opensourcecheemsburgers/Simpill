package com.example.simpill;

import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;

public class DialogChooseFrequency extends AppCompatDialogFragment {
    Simpill simpill;
    PillDBHelper myDatabase;
    Typeface truenoReg, truenoLight;
    AlertDialog.Builder dialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    TextView titleTextView, titleMessageView, multipleDailyTextView, dailyTextView, everyOtherDayTextView, weeklyTextView, customIntervalTextView;
    ImageButton blueThemeBtn, greyThemeBtn, purpleThemeBtn;
    Button okBtn;
    Dialog dbChooseFrequencyDialog;

    int frequency = 1;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        initAll();
        createDialogWithFormatting();
        createOnClickListeners();
        return dbChooseFrequencyDialog;
    }

    private void initAll() {
        initClasses();
        initFonts();
        initDialogBuilder();
        initView();
        initTextViewsAndButtons();
    }

    private void initTextViewsAndButtons() {
        titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        titleMessageView = dialogView.findViewById(R.id.dialogMessageTextView);

        multipleDailyTextView = dialogView.findViewById(R.id.multiple_daily);
        dailyTextView = dialogView.findViewById(R.id.daily);
        everyOtherDayTextView = dialogView.findViewById(R.id.every_other_day);
        weeklyTextView = dialogView.findViewById(R.id.weekly);
        customIntervalTextView = dialogView.findViewById(R.id.custom_interval);
    }

    private void initView() {
        inflater = getActivity().getLayoutInflater();
        setDialogView();
    }

    private void initClasses() {
        myDatabase = new PillDBHelper(getContext());
        simpill = new Simpill();
    }

    private void initDialogBuilder() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
    }

    private void initFonts() {
        truenoReg = ResourcesCompat.getFont(getContext(), R.font.truenoreg);
    }

    private void setDialogView() {
        dialogView = inflater.inflate(R.layout.dialog_choose_frequency, null);
        dialogBuilder.setView(dialogView);
    }



    private void createOnClickListeners() {
        multipleDailyTextView.setOnClickListener(view -> frequency = 0);
        dailyTextView.setOnClickListener(view -> frequency = 1);
        everyOtherDayTextView.setOnClickListener(view -> frequency = 2);
        weeklyTextView.setOnClickListener(view -> frequency = 7);
    }



    public int getFrequency() {

    }


    private void createDialogWithFormatting() {
        dbChooseFrequencyDialog = dialogBuilder.create();
        dbChooseFrequencyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        titleTextView.setTypeface(truenoReg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleTextView.setLetterSpacing(0.025f);
        }

        titleTextView.setTextSize(35.0f);
    }
}

