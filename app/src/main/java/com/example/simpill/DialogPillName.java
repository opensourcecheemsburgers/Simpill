package com.example.simpill;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;

public class DialogPillName extends AppCompatDialogFragment {

    private EditText pillName;
    private ExampleDialogListener pillNameDialog;
    Simpill simpill;


    AlertDialog.Builder dialogBuilder;
    Typeface truenoReg, truenoLight;
    LayoutInflater inflater;
    View dialogView;
    TextView titleTextView, titleMessageView;
    EditText enterNameEditText;
    Button doneBtn;
    Dialog enterNameDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        initAll();
        createDialogWithFormatting();
        createOnClickListeners();

        return enterNameDialog;
    }

    
    private void initAll() {
        initClasses();
        loadSharedPrefs();
        initFonts();
        initDialogBuilder();
        initView();
        initTextViewsAndButtons();
    }
    private void initClasses() {
        simpill = new Simpill();
    }
    private void initFonts() {
        truenoReg = ResourcesCompat.getFont(getContext(), R.font.truenoreg);
        truenoLight = ResourcesCompat.getFont(getContext(), R.font.truenolight);
    }
    private void initDialogBuilder() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
    }
    private void initView() {
        inflater = getActivity().getLayoutInflater();
        setViewBasedOnTheme();
    }
    private void setViewBasedOnTheme() {
        if (simpill.getCustomTheme())
        {
            dialogView = inflater.inflate(R.layout.enter_name_dialog, null);
        }
        else {
            dialogView = inflater.inflate(R.layout.enter_name_dialog_light, null);
        }
        dialogBuilder.setView(dialogView);
    }
    private void initTextViewsAndButtons() {
        titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        titleMessageView = dialogView.findViewById(R.id.dialogMessageTextView);
        doneBtn = dialogView.findViewById(R.id.btnWelcome);
        enterNameEditText = dialogView.findViewById(R.id.editTextTextPersonName2);
    }

    private void createDialogWithFormatting() {
        enterNameDialog = dialogBuilder.create();
        enterNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        titleTextView.setTypeface(truenoReg);
        titleMessageView.setTypeface(truenoReg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleTextView.setLetterSpacing(0.025f);
            titleMessageView.setLetterSpacing(0.025f);
        }

        titleTextView.setTextSize(35.0f);
        titleMessageView.setTextSize(15.0f);
    }

    private void createOnClickListeners () {
        doneBtn.setOnClickListener(view -> {
            pillNameDialog.applyPillName(enterNameEditText.getText().toString());
            enterNameDialog.dismiss();
        });
    }










    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            pillNameDialog = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    private void loadSharedPrefs() {
        SharedPreferences themePref = getContext().getSharedPreferences(Simpill.THEME_PREF_BOOLEAN, Context.MODE_PRIVATE);
        Boolean theme = themePref.getBoolean(Simpill.USER_THEME, true);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getContext().getSharedPreferences(Simpill.IS_24HR_BOOLEAN, Context.MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
    }

    public interface ExampleDialogListener {
        void applyPillName(String userPillName);
    }
}
