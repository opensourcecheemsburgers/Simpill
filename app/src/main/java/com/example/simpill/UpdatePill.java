package com.example.simpill;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdatePill extends AppCompatActivity implements DialogPillName.ExampleDialogListener, DialogPillAmount.ExampleDialogListener {

    private Simpill simpill;
    private AlarmSetter alarmSetter;

    int isTaken, bottleColor;
    String timeTaken = "null";
    String pillName;

    Button updatePillButton, pillNameButton, pillDateButton, pillClockButton, pillAmountButton;
    TextView pillNameTextView, pillTime, pillStockup, pillSupply;
    ImageButton settingsButton, aboutButton;
    int primaryKeyId, year, month, day, hour, min;
    Typeface truenoReg;

    PillDBHelper myDatabase = new PillDBHelper(this);

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
        SharedPreferences themePref = getSharedPreferences(Simpill.THEME_PREF_BOOLEAN, MODE_PRIVATE);
        Boolean theme = themePref.getBoolean(Simpill.USER_THEME, true);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
    }

    private void setContentViewBasedOnThemeSetting() {
        if (simpill.getCustomTheme())
        {
            setContentView(R.layout.update_pill);
        }
        else {
            setContentView(R.layout.update_pill_light);
        }
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
        pillAmountButton.setOnClickListener(view -> openEnterPillAmountDialog());
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
        DialogPillName dialogPillName = new DialogPillName();
        dialogPillName.show(getSupportFragmentManager(), getString(R.string.pill_name_dialog));
    }
    private void openDatePickerDialog() {
        int theme;

        if(simpill.getCustomTheme()) {
            theme = DatePickerDialog.THEME_DEVICE_DEFAULT_DARK;
        }
        else {
            theme = DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
        }

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

        int theme;
        if(simpill.getCustomTheme()) {
            theme = TimePickerDialog.THEME_DEVICE_DEFAULT_DARK;
        }
        else {
            theme = TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
        }

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
    private void openEnterPillAmountDialog() {
        DialogPillAmount dialogPillAmount = new DialogPillAmount();
        dialogPillAmount.show(getSupportFragmentManager(), "Pill Amount Dialog");
    }


    private void updatePill() {
        if (areTextViewsNonEmpty() && isPillAmountValid() && isFirstCharLetter() && isDateValid()) {
            if (myDatabase.updatePill(getIntent().getStringExtra(getString(R.string.pill_name)), pillNameTextView.getText().toString().trim(),
                    pillTime.getText().toString().trim(),
                    pillStockup.getText().toString().trim(),
                    Integer.parseInt(pillSupply.getText().toString()),
                    isTaken, timeTaken, 0, bottleColor)) {
                showCustomToast(1);
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
            showCustomToast(3);
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
            showCustomToast(4);
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
            showCustomToast(5);
            return false;
        }
    }
    private Boolean isDateValid() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format));
        DateTimeManager dateTimeManager = new DateTimeManager();
        Date currentDate = null;
        try {
            currentDate = simpleDateFormat.parse(dateTimeManager.getCurrentDate(getApplicationContext(), dateTimeManager.getUserTimezone()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date stockupDate;
        try {
            stockupDate = simpleDateFormat.parse(pillStockup.getText().toString().trim());
        } catch (ParseException e) {
            Toast.makeText(this, getString(R.string.set_date), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
        if (currentDate.after(stockupDate)) {
            DialogPastDate dialogPastDate = new DialogPastDate();
            dialogPastDate.show(getSupportFragmentManager(), "Tardis Warning");
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

    private void showCustomToast(int toastNumber) {
        LayoutInflater layoutInflater = getLayoutInflater();

        View toastLayout;
        if (simpill.getCustomTheme()) {
            toastLayout = layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_layout));
        }
        else {
            toastLayout = layoutInflater.inflate(R.layout.custom_toast_light,findViewById(R.id.custom_toast_layout_light));
        }

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setView(toastLayout);

        TextView toastTextView = toastLayout.findViewById(R.id.custom_toast_message);

        switch (toastNumber) {
            case 1:
                toastTextView.setText(pillNameTextView.getText().toString().trim() + " updated :)");
                toast.setDuration(Toast.LENGTH_SHORT);
                break;
            case 2:
                toastTextView.setText(R.string.pill_name_in_use);
                break;
            case 3:
                toastTextView.setText(R.string.amount_must_be_greater_than_zero);
                break;
            case 4:
                toastTextView.setText(getString(R.string.fill_fields_warning));
                break;
            case 5:
                toastTextView.setText(getString(R.string.pill_name_warning));
                break;
        }

        toast.show();
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
}

