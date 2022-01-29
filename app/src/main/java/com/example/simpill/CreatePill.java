package com.example.simpill;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreatePill extends AppCompatActivity implements Dialogs.ExampleDialogListener, DialogPillAmount.ExampleDialogListener {

    private Simpill simpill;
    private final Toasts toasts = new Toasts();

    private static final int defaultIsTaken = 0;
    private static final int defaultBottleColor = 2;

    Button createNewPillButton, pillNameButton, pillDateButton, pillClockButton, pillAmountButton;
    TextView pillName, pillTime, pillStockup, pillSupply;
    Button settingsButton, aboutButton;
    int year, month, day, hour, min;
    Typeface truenoReg;

    int intervalInDays = 1;


    PillDBHelper myDatabase = new PillDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpill = (Simpill) getApplicationContext();

        loadSharedPrefs();

        setContentViewBasedOnThemeSetting();

        findViewsByIds();
        initiateTexts();
        initiateCalendar();
        initiateButtons();
    }

    private void loadSharedPrefs() {
        SharedPreferences themePref = getApplicationContext().getSharedPreferences(Simpill.SELECTED_THEME, Context.MODE_PRIVATE);
        int theme = themePref.getInt(Simpill.USER_THEME, simpill.BLUE_THEME);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
    }

    private void setContentViewBasedOnThemeSetting() {
        int theme = simpill.getCustomTheme();

        if (theme == simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if(theme == simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        }
        else {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }

        setContentView(R.layout.app_create_pill);
    }

    private void findViewsByIds(){
        pillName = findViewById(R.id.enterPillName);
        pillTime = findViewById(R.id.enterPillTime);
        pillStockup = findViewById(R.id.enterPillDateReminder);
        pillSupply = findViewById(R.id.enterPillSupplyNumber);
        pillNameButton = findViewById(R.id.pillNameButton);
        pillDateButton = findViewById(R.id.pillDateButton);
        pillClockButton = findViewById(R.id.pillClockButton);
        pillAmountButton = findViewById(R.id.pillAmountButton);
        createNewPillButton = findViewById(R.id.create_new_pill);
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
    }
    private void initiateTexts(){
        truenoReg = ResourcesCompat.getFont(this, R.font.truenoreg);
        pillName.setTypeface(truenoReg);
        pillTime.setTypeface(truenoReg);
        pillStockup.setTypeface(truenoReg);
        pillSupply.setTypeface(truenoReg);
        createNewPillButton.setTypeface(truenoReg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pillName.setLetterSpacing(0.05f);
            pillTime.setLetterSpacing(0.05f);
            pillStockup.setLetterSpacing(0.05f);
            pillSupply.setLetterSpacing(0.05f);
            createNewPillButton.setLetterSpacing(0.05f);
        }
    }
    private void initiateButtons() {
        pillNameButton.setOnClickListener(view -> openEnterPillNameDialog());
        pillAmountButton.setOnClickListener(view -> openEnterPillAmountDialog());
        pillClockButton.setOnClickListener(v -> openFrequencyDialog());
        pillDateButton.setOnClickListener(v -> openDatePickerDialog());
        createNewPillButton.setOnClickListener(v -> createPill());
        settingsButton.setOnClickListener(v -> openSettingsActivity());
        aboutButton.setOnClickListener(v -> openAboutActivity());
    }
    private void initiateCalendar(){
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
    }

    private void openEnterPillNameDialog() {
        new Dialogs().getChooseNameDialog(this).show();
    }
    private void openDatePickerDialog() {
        new DatePickerDialog(this, R.style.DateTimePickerTheme, (view, year, month, day) -> {
            month = month + 1;
            String selectedDate = year + "-" + month + "-" + day;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());

            Date date = Calendar.getInstance().getTime();

            try {
                simpleDateFormat.parse(selectedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            simpleDateFormat.format(date);

            pillStockup.setText(selectedDate);
        }, year, month, day).show();
    }
    private void openTimePickerDialog() {
        new TimePickerDialog(this, R.style.DateTimePickerTheme, (timePicker, selectedHour, selectedMinute) -> {
            String amOrPm;
            String time;

            if (!simpill.getUserIs24Hr()){
                if (selectedHour > 12) {
                    amOrPm = "pm";
                    selectedHour = selectedHour - 12;
                }
                else if (selectedHour == 12){
                    amOrPm = "pm";
                }
                else if (selectedHour == 0){
                    selectedHour = selectedHour + 12;
                    amOrPm = "am";
                }
                else {
                    amOrPm = "am";
                }
                if  (selectedMinute < 10) {
                    time = selectedHour + ":0" + selectedMinute + " " + amOrPm;
                }
                else {
                    time = selectedHour + ":" + selectedMinute + " " + amOrPm;
                }
                time =  new DateTimeManager().convert12HrTimeTo24HrTime(getApplicationContext(), time);
            }
            else {
                if  (selectedMinute < 10) {
                    time = selectedHour + ":0" + selectedMinute;
                }
                else {
                    time = selectedHour + ":" + selectedMinute;
                }
                if (selectedHour < 10) {
                    time = "0" + selectedHour + ":" + selectedMinute;
                }
                if (selectedHour < 10 && selectedMinute < 10) {
                    time = "0" + selectedHour + ":0" + selectedMinute;
                }
            }
            pillTime.setText(time);
        }
                ,12, 0, simpill.getUserIs24Hr()).show();
    }
    private void openEnterPillAmountDialog() {
        DialogPillAmount dialogPillAmount = new DialogPillAmount();
        dialogPillAmount.show(getSupportFragmentManager(), "Pill Amount Dialog");
    }
    private void openFrequencyDialog() {
        new Dialogs().getFrequencyDialog(this, 0).show();
    }


    private void createPill() {
        if (!isNameUnique()) {
            toasts.showCustomToast(this, getString(R.string.non_unique_pill_name_warning));
        } else {
            if (areTextViewsNonEmpty() && isPillAmountValid() && isFirstCharLetter() && isDateValid()) {
                if (myDatabase.addNewPill(
                        getNewPillId(),
                        pillName.getText().toString().trim(),
                        myDatabase.convertStringToArray(pillTime.getText().toString()),
                        getIntervalInDays(),
                        pillStockup.getText().toString().trim(),
                        Integer.parseInt(pillSupply.getText().toString()),
                        defaultIsTaken, getString(R.string.nullString), 0, defaultBottleColor)) {
                    toasts.showCustomToast(this, pillName.getText().toString().trim() + getString(R.string.pill_created_toast));
                    Intent intent = new Intent(this, ChooseColor.class);
                    intent.putExtra(getString(R.string.pill_name), pillName.getText().toString().trim());
                    startActivity(intent);
                }
            }
        }
    }

    private Boolean isNameUnique() {
        return !myDatabase.checkIfPillNameExists(pillName.getText().toString().trim());
    }

    private Boolean isPillAmountValid() {
        int supplyAmount = -1;
        try {
            supplyAmount = Integer.parseInt(pillSupply.getText().toString().trim());
        }
        catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
        }
        if(supplyAmount <= 0) {
            toasts.showCustomToast(this, getString(R.string.pill_supply_warning));
            return false;
        }
        else {
            return true;
        }
    }
    private Boolean areTextViewsNonEmpty() {
        if(pillName.getText().toString().trim().length() == 0 ||
                pillTime.getText().toString().trim().length() == 0 ||
                pillStockup.getText().toString().trim().length() == 0 ||
                pillSupply.getText().toString().trim().length() == 0) {
            toasts.showCustomToast(this, getString(R.string.fill_fields_warning));
            return false;
        }
        else {
            return true;
        }
    }
    private Boolean isFirstCharLetter() {
        if (pillName.getText().toString().trim().length() != 0 && Character.isLetter(pillName.getText().toString().trim().charAt(0))) {
            return true;
        }
        else {
            toasts.showCustomToast(this, getString(R.string.pill_name_warning));
            return false;
        }
    }

    private Boolean isDateValid() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format));
        DateTimeManager dateTimeManager = new DateTimeManager();
        Date currentDate = null;
        try {
            currentDate = simpleDateFormat.parse(dateTimeManager.getCurrentDate(getApplicationContext(), dateTimeManager.getUserTimezone()));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        Date stockupDate;
        try {
            stockupDate = simpleDateFormat.parse(pillStockup.getText().toString().trim());
        }
        catch (ParseException e) {
            toasts.showCustomToast(this, getString(R.string.set_date));
            e.printStackTrace();
            return false;
        }
        if (currentDate.after(stockupDate)) {
            new Dialogs().getPastDateDialog(this).show();
            return false;
        }
        else {
            return true;
        }
    }

    private int getNewPillId() {
        return myDatabase.getRowCount() + 1;
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }
    private void openSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void setIntervalInDays(int intervalInDays) {
        this.intervalInDays = intervalInDays;
    }

    public int getIntervalInDays() {
        return intervalInDays;
    }

    @Override
    public void applyPillName(String userPillName) {
        pillName.setText(userPillName);
        pillName.setTypeface(truenoReg);
    }

    @Override
    public void applyPillSupply(String userPillSupply) {
        pillSupply.setText(userPillSupply);
        pillSupply.setTypeface(truenoReg);
    }
}