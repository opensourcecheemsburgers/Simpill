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

public class UpdatePill extends AppCompatActivity implements Dialogs.PillNameDialogListener, Dialogs.PillAmountDialogListener,
        Dialogs.ChooseFrequencyDialogListener,Dialogs.PillReminderAmountDialogListener,  Dialogs.TimePickerDialogListener, Dialogs.ChooseTimesDialogListener {

    private Simpill simpill = new Simpill();
    private AlarmSetter alarmSetter;
    Dialogs dialogs = new Dialogs();
    private final Toasts toasts = new Toasts();

    private static final int defaultIsTaken = 0;
    private static final int defaultBottleColor = 2;

    Button createNewPillButton, pillNameButton, pillDateButton, pillClockButton, pillAmountButton;
    TextView pillNameTextView, pillTimeTextView, pillStockupTextView, pillSupplyTextView;
    Button settingsButton, aboutButton;
    int year, month, day, hour, min;

    Typeface truenoReg;

    int intervalInDays = 1;
    int timesPerDay;
    int currentArrayNumber = 0;

    String[] times;

    String pillName, timeTaken, date;
    int primaryKeyId, supply, isTaken, bottleColor;

    DatabaseHelper myDatabase = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSharedPrefs();

        setContentViewBasedOnThemeSetting();

        findViewsByIds();
        getAndSetIntentData();
        alarmSetter = new AlarmSetter(this, pillName, primaryKeyId);
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
        pillNameTextView = findViewById(R.id.enterPillName);
        pillTimeTextView = findViewById(R.id.enterPillTime);
        pillStockupTextView = findViewById(R.id.enterPillDateReminder);
        pillSupplyTextView = findViewById(R.id.enterPillSupplyNumber);
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
        pillNameTextView.setTypeface(truenoReg);
        pillTimeTextView.setTypeface(truenoReg);
        pillStockupTextView.setTypeface(truenoReg);
        pillSupplyTextView.setTypeface(truenoReg);
        createNewPillButton.setTypeface(truenoReg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pillNameTextView.setLetterSpacing(0.05f);
            pillTimeTextView.setLetterSpacing(0.05f);
            pillStockupTextView.setLetterSpacing(0.05f);
            pillSupplyTextView.setLetterSpacing(0.05f);
            createNewPillButton.setLetterSpacing(0.05f);
        }
    }
    private void initiateButtons() {
        pillNameButton.setOnClickListener(view -> openEnterPillNameDialog());
        pillAmountButton.setOnClickListener(view -> openEnterPillAmountDialog());
        pillClockButton.setOnClickListener(v -> openFrequencyDialog());
        pillDateButton.setOnClickListener(v -> openDatePickerDialog());
        createNewPillButton.setOnClickListener(v -> updatePill());
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
        dialogs.getChooseNameDialog(this).show();
    }
    private void openDatePickerDialog() {
        new DatePickerDialog(this, DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, (view, year, month, day) -> {
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

            pillStockupTextView.setText(selectedDate);
        }, year, month, day).show();
    }
    private void openTimePickerDialog(int timePickerAmount) {
        times = new String[timePickerAmount];

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
                time = dateTimeManager.convert12HrTimeTo24HrTime(UpdatePill.this, time);
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
            pillTimeTextView.setText(time);
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(UpdatePill.this, R.style.MyTimePickerDialogStyle, timeSetListener, 12, 0, simpill.getUserIs24Hr());
        timePickerDialog.show();
    }
    private void openEnterPillAmountDialog() {
        dialogs.getChooseSupplyAmountDialog(this).show();
    }
    private void openFrequencyDialog() {
        dialogs.getFrequencyDialog(this, 1).show();
    }

    private void getAndSetIntentData(){
        primaryKeyId = getIntent().getIntExtra(getString(R.string.primary_key_id), -1);
        pillName = getIntent().getStringExtra(getString(R.string.pill_name));
        times = getIntent().getStringArrayExtra(getString(R.string.pill_time));
        date = getIntent().getStringExtra(getString(R.string.pill_date));
        supply = getIntent().getIntExtra(getString(R.string.pill_amount), 1);
        isTaken = getIntent().getIntExtra(getString(R.string.is_pill_taken), 0);
        bottleColor = getIntent().getIntExtra(getString(R.string.bottle_color), 2);
        timeTaken = getIntent().getStringExtra(getString(R.string.time_taken));


        pillNameTextView.setText(pillName);

        if (simpill.getUserIs24Hr()) {
            pillTimeTextView.setText(myDatabase.convertArrayToString(times));
        }
        else {
            pillTimeTextView.setText(myDatabase.convertArrayToString(myDatabase.convert24HrArrayTo12HrArray(this, times)));
        }
        pillStockupTextView.setText(new DateTimeManager().convertISODateStringToLocallyFormattedString(this, date));
        pillSupplyTextView.setText(String.valueOf(supply));
    }



    private void updatePill() {
        if (areTextViewsNonEmpty() && isPillAmountValid() && isFirstCharLetter() && isDateValid()) {
            if (myDatabase.updatePill(getIntent().getStringExtra(getString(R.string.pill_name)), pillNameTextView.getText().toString().trim(), myDatabase.convertStringToArray(pillTimeTextView.getText().toString()), getIntervalInDays(),
                    pillStockupTextView.getText().toString().trim(),
                    Integer.parseInt(pillSupplyTextView.getText().toString()),
                    isTaken, timeTaken, 0, bottleColor)) {
                toasts.showCustomToast(this, pillNameTextView.getText().toString().trim() + getString(R.string.append_updated_toast));
                openMainActivity();
                alarmSetter.setAlarms(0);
            }
        }
    }


    private Boolean isNameUnique() {
        return !myDatabase.checkIfPillNameExists(pillNameTextView.getText().toString().trim());
    }

    private Boolean isPillAmountValid() {
        int supplyAmount = -1;
        try {
            supplyAmount = Integer.parseInt(pillSupplyTextView.getText().toString().trim());
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
                pillTimeTextView.getText().toString().trim().length() == 0 ||
                pillStockupTextView.getText().toString().trim().length() == 0 ||
                pillSupplyTextView.getText().toString().trim().length() == 0) {
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
            stockupDate = simpleDateFormat.parse(pillStockupTextView.getText().toString().trim());
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
        pillNameTextView.setText(userPillName);
        pillNameTextView.setTypeface(truenoReg);
    }

    @Override
    public void applyPillSupply(String userPillSupply) {
        pillSupplyTextView.setText(userPillSupply);
        pillSupplyTextView.setTypeface(truenoReg);
    }

    @Override
    public void openTimePicker(int frequency) {
        openTimePickerDialog(1);
    }

    @Override
    public void applySelectedTimeToArray(String time, int position) {
        times[position] = time;
    }

    @Override
    public void applySelectedTimeToTextView(TimesRecyclerViewAdapter.MyViewHolder holder, String time, int position) {
        pillTimeTextView.setText(time);
    }

    @Override
    public void applyNumberOfReminders(int reminders) {
        times = new String[reminders];
    }

    @Override
    public void returnTimesStringArray(String[] times) {
        pillTimeTextView.setText(myDatabase.convertArrayToString(times));
    }
}