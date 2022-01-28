package com.example.simpill;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;

public class DialogDatabaseReset extends AppCompatDialogFragment {

    Simpill simpill;
    PillDBHelper myDatabase;
    Typeface truenoReg, truenoLight;
    AlertDialog.Builder dialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    TextView titleTextView, titleMessageView;
    Button resetDbBtn, cancelBtn;
    Dialog dbResetDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        initAll();
        createDialogWithFormatting();
        createOnClickListeners();
        return dbResetDialog;
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
        resetDbBtn = dialogView.findViewById(R.id.btnYes);
        cancelBtn = dialogView.findViewById(R.id.btnNo);
    }

    private void initView() {
        loadSharedPrefs();
        inflater = getActivity().getLayoutInflater();
        setViewBasedOnTheme();
    }

    private void loadSharedPrefs() {
        SharedPreferences themePref = getContext().getSharedPreferences(Simpill.SELECTED_THEME, Context.MODE_PRIVATE);
        int theme = themePref.getInt(Simpill.USER_THEME, simpill.BLUE_THEME);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getContext().getSharedPreferences(Simpill.IS_24HR_BOOLEAN, Context.MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
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
        truenoLight = ResourcesCompat.getFont(getContext(), R.font.truenolight);
    }

    private void setViewBasedOnTheme() {
        dialogView = inflater.inflate(R.layout.dialog_db_reset, null);

        dialogBuilder.setView(dialogView);
    }



    private void createOnClickListeners() {
        resetDbBtn.setOnClickListener(view -> {
            myDatabase.deleteDatabase();
            dbResetDialog.dismiss();

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());

            View toastLayout = layoutInflater.inflate(R.layout.toast, view.findViewById(R.id.custom_toast_layout_light));

            Toast toast = new Toast(getContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 250);
            toast.setView(toastLayout);

            TextView toastTextView = toastLayout.findViewById(R.id.custom_toast_message);
            toastTextView.setText(getString(R.string.database_reset_toast));

            toast.show();
        });

        cancelBtn.setOnClickListener(view -> dbResetDialog.dismiss());
    }


    private void createDialogWithFormatting() {
        dbResetDialog = dialogBuilder.create();
        dbResetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        titleTextView.setText(getString(R.string.pill_db_reset));
        titleMessageView.setText(getString(R.string.pill_db_reset_warning));

        titleTextView.setTypeface(truenoReg);
        titleMessageView.setTypeface(truenoLight);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleTextView.setLetterSpacing(0.025f);
            titleMessageView.setLetterSpacing(0.025f);
        }

        titleTextView.setTextSize(35.0f);
        titleMessageView.setTextSize(15.0f);
    }
}
