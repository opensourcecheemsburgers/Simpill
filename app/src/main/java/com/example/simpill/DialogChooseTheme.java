package com.example.simpill;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;

public class DialogChooseTheme extends AppCompatDialogFragment {

        Simpill simpill;
        PillDBHelper myDatabase;
        Typeface truenoReg, truenoLight;
        AlertDialog.Builder dialogBuilder;
        LayoutInflater inflater;
        View dialogView;
        TextView titleTextView, titleMessageView;
        ImageButton blueThemeBtn, greyThemeBtn, purpleThemeBtn;
        Button okBtn;
        Dialog dbChooseThemeDialog;

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            initAll();
            createDialogWithFormatting();
            createOnClickListeners();
            return dbChooseThemeDialog;
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
            okBtn = dialogView.findViewById(R.id.btnOk);

            blueThemeBtn = dialogView.findViewById(R.id.blue_theme_btn);
            greyThemeBtn = dialogView.findViewById(R.id.grey_theme_btn);
            purpleThemeBtn = dialogView.findViewById(R.id.purple_theme_btn);
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
            dialogView = inflater.inflate(R.layout.dialog_choose_theme, null);
            dialogBuilder.setView(dialogView);
        }



        private void createOnClickListeners() {
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

                dbChooseThemeDialog.dismiss();

                LayoutInflater layoutInflater = LayoutInflater.from(getContext());

                View toastLayout = layoutInflater.inflate(R.layout.toast, view.findViewById(R.id.custom_toast_layout_light));

                Toast toast = new Toast(getContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 250);
                toast.setView(toastLayout);

                TextView toastTextView = toastLayout.findViewById(R.id.custom_toast_message);
                toastTextView.setText(getString(R.string.theme_applied));

                toast.show();

                getActivity().recreate();
            });
        }


        private void createDialogWithFormatting() {
            dbChooseThemeDialog = dialogBuilder.create();
            dbChooseThemeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            titleTextView.setText(getString(R.string.select_theme_dialog_title));

            titleTextView.setTypeface(truenoReg);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                titleTextView.setLetterSpacing(0.025f);
            }

            titleTextView.setTextSize(35.0f);
        }
}
