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

public class UpdatePill extends AppCompatActivity implements Dialogs.PillNameDialogListener, Dialogs.PillAmountDialogListener {

    private Simpill simpill;
    Dialogs dialogs;
    private final Toasts toasts = new Toasts();
    private AlarmSetter alarmSetter;

    int isTaken, bottleColor;
    String timeTaken = "null";
    String pillName;

    Button updatePillButton, pillNameButton, pillDateButton, pillClockButton, pillAmountButton;
    TextView pillNameTextView, pillTime, pillStockup, pillSupply;
    Button settingsButton, aboutButton;
    int primaryKeyId, year, month, day, hour, min;
    Typeface truenoReg;

    DatabaseHelper myDatabase = new DatabaseHelper(this);

    int timesPerDay = 1;
    int intervalInDays = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpill = (Simpill) getApplicationContext();

        loadSharedPrefs();

        setContentViewBasedOnThemeSetting();

        findViewsByIds();
        getAndSetIntentData();
        initiateTexts();
        initiateCalendar();
        initiateButtons();
        createAlarmSetter();
    }

    private void createAlarmSetter() {
        alarmSetter = new AlarmSetter(getApplicationContext(), pillName, primaryKeyId);
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
        } else if (theme == simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        }
        else if (theme == simpill.PURPLE_THEME) {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }

        setContentView(R.layout.app_update_pill);
    }

    private void findViewsByIds(){
        pillNameTextView = findViewById(R.id.enterPillName);
        pillTime = findViewById(R.id.enterPillTime);
        pillStockup = findViewById(R.id.enterPillDateReminder);
        pillSupply = findViewById(R.id.enterPillSupplyNumber);
        pillNameButton = findViewById(R.id.pillNameButton);
        pillDateButton = findViewById(R.id.pillDateButton);
        pillClockButton = findViewById(R.id.pillClockButton);
        pillAmountButton = findViewById(R.id.pillAmountButton);
        updatePillButton = findViewById(R.id.update_pill);
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
    }
    private void initiateTexts(){
        truenoReg = ResourcesCompat.getFont(this, R.font.truenoreg);

        pillNameTextView.setTypeface(truenoReg);
        pillTime.setTypeface(truenoReg);
        pillStockup.setTypeface(truenoReg);
        pillSupply.setTypeface(truenoReg);
        updatePillButton.setTypeface(truenoReg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pillNameTextView.setLetterSpacing(0.05f);
            pillTime.setLetterSpacing(0.05f);
            pillStockup.setLetterSpacing(0.05f);
            pillSupply.setLetterSpacing(0.05f);
            updatePillButton.setLetterSpacing(0.05f);
        }
    }
    private void initiateButtons() {
        pillNameButton.setOnClickListener(view -> openEnterPillNameDialog());
        pillAmountButton.setOnClickListener(view -> openEnterPillNameDialog());
        pillClockButton.setOnClickListener(v -> openTimePickerDialog());
        pillDateButton.setOnClickListener(v -> openDatePickerDialog());
        updatePillButton.setOnClickListener(v -> updatePill());
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

    private void getAndSetIntentData(){
        primaryKeyId = getIntent().getIntExtra(getString(R.string.primary_key_id), -1);
        pillName = getIntent().getStringExtra(getString(R.string.pill_name));
        pillNameTextView.setText(pillName);
        pillTime.setText(getIntent().getStringExtra(getString(R.string.pill_time)));
        pillStockup.setText(getIntent().getStringExtra(getString(R.string.pill_date)));
        System.out.println(getIntent().getStringExtra(getString(R.string.pill_date)));
        pillSupply.setText(String.valueOf(getIntent().getIntExtra(getString(R.string.pill_amount), 1)));
        isTaken = getIntent().getIntExtra(getString(R.string.is_pill_taken), 0);
        bottleColor = getIntent().getIntExtra(getString(R.string.bottle_color), 2);
        timeTaken = getIntent().getStringExtra(getString(R.string.time_taken));
    }

    private void openEnterPillNameDialog() {
        new Dialogs().getChooseNameDialog(this).show();
    }
    private void openDatePickerDialog() {
        int theme = DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;

        DatePickerDialog datePickerDialog = new DatePickerDialog(UpdatePill.this, theme, (view, year, month, day) -> {
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
        }, year, month, day);
        datePickerDialog.show();
    }
    private void openTimePickerDialog() {
        DateTimeManager dateTimeManager = new DateTimeManager();

        int theme = TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, theme, (timePicker, selectedHour, selectedMinute) -> {

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
                time = dateTimeManager.convert12HrTimeTo24HrTime(getApplicationContext(), time);
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
                ,12, 0, simpill.getUserIs24Hr());
        timePickerDialog.show();
    }



    private void updatePill() {

        if (areTextViewsNonEmpty() && isPillAmountValid() && isFirstCharLetter() && isDateValid()) {
            if (myDatabase.updatePill(getIntent().getStringExtra(getString(R.string.pill_name)), pillNameTextView.getText().toString().trim(), myDatabase.convertStringToArray(pillTime.getText().toString()), getIntervalInDays(),
                    pillStockup.getText().toString().trim(),
                    Integer.parseInt(pillSupply.getText().toString()),
                    isTaken, timeTaken, 0, bottleColor)) {
                toasts.showCustomToast(this, pillNameTextView.getText().toString().trim() + getString(R.string.append_updated_toast));
                openMainActivity();
                alarmSetter.setAlarms(0);
            }
        }
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
        if(pillNameTextView.getText().toString().trim().length() == 0 ||
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
        if (pillNameTextView.getText().toString().trim().length() != 0 && Character.isLetter(pillNameTextView.getText().toString().trim().charAt(0))) {
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


    @Override
    public void applyPillName(String userPillName) {
        pillNameTextView.setText(userPillName);
        pillNameTextView.setTypeface(truenoReg);
    }

    @Override
    public void applyPillSupply(String userPillSupply) {
        pillSupply.setText(userPillSupply);
        pillSupply.setTypeface(truenoReg);
    }

    public void setIntervalInDays(int intervalInDays) {
        this.intervalInDays = intervalInDays;
    }
    public int getIntervalInDays() {
        return intervalInDays;
    }

    public void setTimesPerDay(int timesPerDay) {
        this.timesPerDay = timesPerDay;
    }
    public int getTimesPerDay() {
        return timesPerDay;
    }
}

