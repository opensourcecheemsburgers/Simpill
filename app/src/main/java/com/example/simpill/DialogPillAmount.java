package com.example.simpill;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;

public class DialogPillAmount extends AppCompatDialogFragment {

    private EditText pillName;
    private ExampleDialogListener pillAmountDialogListener;
    Simpill simpill;


    AlertDialog.Builder dialogBuilder;
    Typeface truenoReg, truenoLight;
    LayoutInflater inflater;
    View dialogView;
    TextView titleTextView;
    EditText enterAmountEditText;
    ImageView pillIcon, addBtn, minusBtn;
    Button doneBtn;
    Dialog enterAmountDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        initAll();
        createDialogWithFormatting();
        createOnClickListeners();

        return enterAmountDialog;
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
        dialogView = inflater.inflate(R.layout.dialog_pill_amount, null);

        dialogBuilder.setView(dialogView);
    }
    private void initTextViewsAndButtons() {
        titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        pillIcon = dialogView.findViewById(R.id.imageView13);
        doneBtn = dialogView.findViewById(R.id.btnWelcome);
        addBtn = dialogView.findViewById(R.id.addBtn);
        minusBtn = dialogView.findViewById(R.id.minusBtn);
        enterAmountEditText = dialogView.findViewById(R.id.amountTextView);
        enterAmountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void createDialogWithFormatting() {
        enterAmountDialog = dialogBuilder.create();
        enterAmountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        titleTextView.setTypeface(truenoReg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleTextView.setLetterSpacing(0.025f);
        }

        titleTextView.setTextSize(35.0f);
    }

    private void createOnClickListeners () {
        doneBtn.setOnClickListener(view -> {
            pillAmountDialogListener.applyPillSupply(enterAmountEditText.getText().toString());
            enterAmountDialog.dismiss();
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
            enterAmountEditText.setText(String.valueOf(pillAmount - 1));
        });
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            pillAmountDialogListener = (ExampleDialogListener) context;
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
        void applyPillSupply(String pillSupply);
    }
}
