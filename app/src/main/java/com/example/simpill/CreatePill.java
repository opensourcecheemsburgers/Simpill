/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.Pill.DEFAULT_ALARM_URI;
import static com.example.simpill.Pill.PRIMARY_KEY_INTENT_KEY_STRING;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import com.airbnb.lottie.LottieAnimationView;
import java.util.Calendar;

public class CreatePill extends AppCompatActivity
        implements Dialogs.PillNameDialogListener,
                Dialogs.PillAmountDialogListener,
                Dialogs.ChooseFrequencyDialogListener,
                Dialogs.PillReminderAmountDialogListener,
                Dialogs.ChooseTimesDialogListener,
                Dialogs.GetStartDateDialogListener,
                Dialogs.PillReminderMethodListener,
                Dialogs.OnReminderTimeSetListener,
                Dialogs.GetStockupDateDialogListener {

    public static final String NEW_PILL_INTENT_KEY = "pill_created";
    private final SharedPrefs sharedPrefs = new SharedPrefs(this);
    final Dialogs dialogs = new Dialogs(this);
    private final Toasts toasts = new Toasts(this);

    Button createNewPillButton;
    ImageView pillBottleImage, clockImage, calendarImage, blisterPackImage;
    TextView pillNameTextView,
            pillTimesTextView,
            pillTimeIntervalText,
            pillTimeAlarmTypeText,
            pillStockupTextView,
            pillStockupSubtext,
            pillSupplyTextView,
            pillSupplySubtext;
    Button backButton;
    int year, month, day, hour, min, cachedHour, cachedMin;

    Typeface montserratSemiBold;
    Typeface interMedium;

    boolean is24HrFormat = false;

    Pill userPill = new Pill();

    Dialog timeInfoDialog;
    View timeInfoDialogView;
    Window timeInfoDialogWindow;
    ConstraintLayout dialogLayout;
    ImageButton alarmBellButton, clockButton;
    TextView titleTextView,
            messageTextView,
            timesTextView,
            userTimesTextView,
            userFrequencyTextView,
            reminderMethodTextView,
            userReminderMethodTextView,
            userAlarmSoundTextView,
            frequencyTextView;
    Button doneBtn, playBtn, pauseBtn;
    LottieAnimationView pillNameAnimation,
            pillTimeAnimation,
            pillDateAnimation,
            pillSupplyAnimation;

    MediaPlayer alarmPlayer;

    final DatabaseHelper myDatabase = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        is24HrFormat = new SharedPrefs(this).get24HourFormatPref();
        setContentViewBasedOnThemeSetting();
        initDialog();
        findViewsByIds();
        initiateTexts();
        initiateCalendar();
        initiateButtons();
        getAndSetIntentData();
    }

    private void getAndSetIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra(PRIMARY_KEY_INTENT_KEY_STRING)) {
            userPill = myDatabase.getPill(intent.getIntExtra(PRIMARY_KEY_INTENT_KEY_STRING, -1));
            pillNameTextView.setText(userPill.getName());
            pillTimesTextView.setText(
                    is24HrFormat ? userPill.getTimes24HrFormat() : userPill.getTimes12HrFormat());

            timesTextView.setVisibility(View.GONE);
            userTimesTextView.setVisibility(View.VISIBLE);
            userTimesTextView.setText(
                    this.getString(R.string.reminder_times)
                            .concat(
                                    is24HrFormat
                                            ? userPill.getTimes24HrFormat()
                                            : userPill.getTimes12HrFormat()));
            setInterval(userPill.getFrequency());
            applyReminderMethod(userPill.getAlarmType());

            if (userPill.getStockupDate().equalsIgnoreCase("null")) {
                pillStockupTextView.setText(getString(R.string.enter_pill_refill_date_reminder));
            } else {
                pillStockupTextView.setText(
                        new DateTimeManager()
                                .convertISODateStringToLocallyFormattedString(
                                        userPill.getStockupDate()));
            }
            if (userPill.getSupply() < 0) {
                pillSupplyTextView.setText(getString(R.string.enter_pill_amount));
            } else {
                pillSupplyTextView.setText(String.valueOf(userPill.getSupply()));
            }
            createNewPillButton.setOnClickListener(v -> updatePill());
        }
    }

    private void createPill() {
        if (areTextViewsNonEmpty()) {
            Pill pill = userPill.addToDatabase(this);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(NEW_PILL_INTENT_KEY, pill.getPrimaryKey());
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void updatePill() {
        if (areTextViewsNonEmpty()) {
            userPill.updatePillInDatabase(this);
            userPill.setAlarm(this);
            userPill.setStockupAlarm(this);
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void setContentViewBasedOnThemeSetting() {
        int theme = sharedPrefs.getThemesPref();

        if (theme == Simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if (theme == Simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        } else if (theme == Simpill.BLACK_THEME) {
            setTheme(R.style.SimpillAppTheme_BlackBackground);
        } else {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }

        setContentView(R.layout.app_create_pill);
    }

    @SuppressLint("InflateParams")
    private void initDialog() {
        timeInfoDialogView =
                LayoutInflater.from(this).inflate(R.layout.dialog_pill_reminder_info, null);
        timeInfoDialog = new AlertDialog.Builder(this).setView(timeInfoDialogView).create();
        timeInfoDialogWindow = timeInfoDialog.getWindow();
        timeInfoDialogWindow.setBackgroundDrawable(new ColorDrawable(0));

        timeInfoDialog.setOnDismissListener(
                dialog -> {
                    stopAlarm();
                    if (reminderMethodTextView.getVisibility() == View.VISIBLE) {
                        playBtn.setVisibility(View.GONE);
                        pauseBtn.setVisibility(View.GONE);
                    }
                });

        dialogLayout = timeInfoDialogView.findViewById(R.id.custom_dialog_constraint_layout);
        titleTextView = timeInfoDialogView.findViewById(R.id.dialog_title_textview);
        messageTextView = timeInfoDialogView.findViewById(R.id.dialog_message_textview);
        clockButton = timeInfoDialogView.findViewById(R.id.clockImgBtn);
        alarmBellButton = timeInfoDialogView.findViewById(R.id.alarmBellImgBtn);
        timesTextView = timeInfoDialogView.findViewById(R.id.timesTextView);
        userTimesTextView = timeInfoDialogView.findViewById(R.id.userTimesTextView);
        userFrequencyTextView = timeInfoDialogView.findViewById(R.id.userFrequencyTextView);
        reminderMethodTextView = timeInfoDialogView.findViewById(R.id.alarmTypeTextView);
        userReminderMethodTextView = timeInfoDialogView.findViewById(R.id.userAlarmTypeTextView);
        userAlarmSoundTextView = timeInfoDialogView.findViewById(R.id.userAlarmSoundTextView);
        doneBtn = timeInfoDialogView.findViewById(R.id.btnYes);
        playBtn = timeInfoDialogView.findViewById(R.id.play_btn);
        pauseBtn = timeInfoDialogView.findViewById(R.id.pause_btn);

        if (sharedPrefs.getDarkDialogsPref()) {
            int aliceblue = ResourcesCompat.getColor(this.getResources(), R.color.alice_blue, null);
            dialogLayout.setBackground(
                    AppCompatResources.getDrawable(this, R.drawable.dialog_background_dark));
            titleTextView.setBackground(
                    AppCompatResources.getDrawable(this, R.drawable.dialog_title_background_dark));
            messageTextView.setTextColor(aliceblue);
            timesTextView.setTextColor(aliceblue);
            userTimesTextView.setTextColor(aliceblue);
            userFrequencyTextView.setTextColor(aliceblue);
            reminderMethodTextView.setTextColor(aliceblue);
            userReminderMethodTextView.setTextColor(aliceblue);
            userAlarmSoundTextView.setTextColor(aliceblue);
            doneBtn.setBackground(
                    AppCompatResources.getDrawable(this, R.drawable.dialog_bottom_btn_dark));
            playBtn.setBackground(AppCompatResources.getDrawable(this, R.drawable.play_btn_dark));
            pauseBtn.setBackground(AppCompatResources.getDrawable(this, R.drawable.pause_btn_dark));
        } else {
            doneBtn.setBackground(
                    AppCompatResources.getDrawable(this, R.drawable.dialog_bottom_btn_purple));
        }

        doneBtn.setText(this.getString(R.string.done));
        doneBtn.setAllCaps(false);
        doneBtn.setOnClickListener(v -> timeInfoDialog.dismiss());

        playBtn.setOnClickListener(v -> startAlarm());

        pauseBtn.setOnClickListener(v -> stopAlarm());

        clockButton.setOnClickListener(v -> dialogs.getFrequencyDialog().show());
        timesTextView.setOnClickListener(v -> dialogs.getFrequencyDialog().show());
        reminderMethodTextView.setOnClickListener(
                v -> dialogs.getAlarmOrNotificationDialog().show());
        alarmBellButton.setOnClickListener(v -> dialogs.getAlarmOrNotificationDialog().show());
        userTimesTextView.setOnClickListener(
                v -> {
                    if (userPill.getFrequency() < DatabaseHelper.DAILY) {
                        dialogs.getChooseReminderAmountDialog(2);
                    }
                });
    }

    private void stopAlarm() {

        if (alarmPlayer != null && alarmPlayer.isPlaying()) {
            alarmPlayer.stop();
            alarmPlayer.prepareAsync();
        }
        playBtn.setVisibility(View.VISIBLE);
        pauseBtn.setVisibility(View.GONE);
    }

    private void startAlarm() {
        alarmPlayer = new AudioHelper(this).getAlarmPlayer(userPill.getCustomAlarmUri());
        alarmPlayer.start();
        playBtn.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.VISIBLE);
    }

    private void findViewsByIds() {
        pillBottleImage = findViewById(R.id.pill_bottle_image);
        clockImage = findViewById(R.id.clock_image);
        calendarImage = findViewById(R.id.calendar_image);
        blisterPackImage = findViewById(R.id.blister_pack_image);
        pillNameAnimation = findViewById(R.id.pill_name_lottieview);
        pillTimeAnimation = findViewById(R.id.pill_time_lottieview);
        pillDateAnimation = findViewById(R.id.pill_date_lottieview);
        pillSupplyAnimation = findViewById(R.id.pill_supply_lottieview);
        pillNameTextView = findViewById(R.id.enterPillName);
        pillTimesTextView = findViewById(R.id.enter_pill_time_textview);
        pillTimeIntervalText = findViewById(R.id.pill_frequency_textview);
        pillTimeAlarmTypeText = findViewById(R.id.alarm_type_textview);
        pillStockupTextView = findViewById(R.id.pill_date_textview);
        pillStockupSubtext = findViewById(R.id.refill_date_optional_tag);
        pillSupplyTextView = findViewById(R.id.pill_supply_textview);
        pillSupplySubtext = findViewById(R.id.pill_supply_optional_tag);
        createNewPillButton = findViewById(R.id.create_new_pill);
        backButton = findViewById(R.id.back_button);
        clockImage = findViewById(R.id.clock);
    }

    private void initiateTexts() {
        interMedium = ResourcesCompat.getFont(this, R.font.inter_medium);
        pillNameTextView.setTypeface(interMedium);
        pillTimesTextView.setTypeface(interMedium);
        pillStockupTextView.setTypeface(interMedium);
        pillSupplyTextView.setTypeface(interMedium);
        createNewPillButton.setTypeface(interMedium);

        pillBottleImage.setOnClickListener(v -> chooseName());
        pillNameTextView.setOnClickListener(v -> chooseName());
        clockImage.setOnClickListener(v -> chooseTime());
        pillTimesTextView.setOnClickListener(v -> chooseTime());
        pillTimeAlarmTypeText.setOnClickListener(v -> chooseTime());
        pillTimeIntervalText.setOnClickListener(v -> chooseTime());
        calendarImage.setOnClickListener(v -> chooseDate());
        pillStockupTextView.setOnClickListener(v -> chooseDate());
        blisterPackImage.setOnClickListener(v -> chooseSupply());
        pillSupplyTextView.setOnClickListener(v -> chooseSupply());
    }

    private void chooseName() {
        dialogs.getChooseNameDialog(userPill.getName()).show();
    }

    private void chooseTime() {
        timeInfoDialog.show();
    }

    private void chooseDate() {
        dialogs.getStockupDateDialog(userPill).show();
    }

    private void chooseSupply() {
        dialogs.getChooseSupplyAmountDialog(userPill.getSupply()).show();
    }

    private void initiateButtons() {
        createNewPillButton.setOnClickListener(v -> createPill());
        backButton.setOnClickListener(v -> finish());
    }

    private void initiateCalendar() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
    }

    private void isPillReady() {
        if (areTextViewsNonEmpty()) {
            createNewPillButton.setAlpha(1f);
        }
    }

    private void openTimePickerDialog() {
        dialogs.getTimePickerDialog(cachedHour, cachedMin).show();
    }

    private Boolean areTextViewsNonEmpty() {
        if(pillNameTextView.getText().toString().trim().length() != 0 && pillTimesTextView.getText().toString().trim().length() != 0) {
            createNewPillButton.setAlpha(1f);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void applyPillName(String userPillName) {
        userPill.setName(userPillName);
        pillNameTextView.setText(userPillName);
        isPillReady();
        pillNameAnimation.playAnimation();
        areTextViewsNonEmpty();
    }

    @Override
    public void applyPillSupply(String userPillSupply) {
        userPill.setSupply(Integer.parseInt(userPillSupply));
        pillSupplyTextView.setText(userPillSupply);
        pillSupplyTextView.setTypeface(montserratSemiBold);
        pillSupplySubtext.setVisibility(View.GONE);
        pillSupplyTextView.setPadding(0, 0, 0, 0);
        pillSupplyAnimation.playAnimation();
    }

    @Override
    public void openTimePicker() {
        openTimePickerDialog();
    }

    @Override
    public void setInterval(int intervalInDays) {
        userPill.setFrequency(intervalInDays);

        if (intervalInDays <= 0) {
            pillTimeIntervalText.setVisibility(View.GONE);
        }
        String frequencyString =
                this.getString(R.string.frequency)
                        + this.getString(R.string.choose_frequency_dialog_daily);
        userFrequencyTextView.setText(frequencyString);
        userFrequencyTextView.setVisibility(View.VISIBLE);
        timesTextView.setVisibility(View.GONE);
    }

    @Override
    public void applyNumberOfReminders(int reminders) {
        userPill.setTimesArray(new String[reminders]);
    }

    @Override
    public void returnTimesStringArray(String[] times) {
        ArrayHelper arrayHelper = new ArrayHelper();
        userPill.setTimesArray(times);
        setTime(arrayHelper.convertArrayToString(times));
        pillTimeAnimation.playAnimation();
    }

    @Override
    public void applyStartDate(String startDate) {
        userPill.setStartDate(startDate);
        pillTimeIntervalText.setVisibility(View.VISIBLE);

        String frequencyString =
                this.getString(R.string.frequency)
                        + this.getString(
                                R.string.pill_time_interval_text,
                                userPill.getFrequency(),
                                userPill.getFormattedStartDate());
        userFrequencyTextView.setVisibility(View.VISIBLE);
        userFrequencyTextView.setText(frequencyString);
        pillTimeIntervalText.setText(frequencyString);

        openTimePickerDialog();
    }

    @Override
    public void applyReminderMethod(int alarmType) {
        userPill.setAlarmType(alarmType);
        userPill.setCustomAlarmUri(DEFAULT_ALARM_URI);

        if (alarmType == DatabaseHelper.ALARM) {
            alarmPlayer = new AudioHelper(this).getAlarmPlayer(userPill.getCustomAlarmUri());
            String alarmSound =
                    getString(R.string.reminder_sound).concat("Doomsday Alarm (Default)");
            userAlarmSoundTextView.setText(alarmSound);
        } else {
            String notificationSound = getString(R.string.reminder_sound) + "Default";
            userAlarmSoundTextView.setText(notificationSound);
        }

        reminderMethodTextView.setVisibility(View.GONE);
        userReminderMethodTextView.setVisibility(View.VISIBLE);
        userAlarmSoundTextView.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        String reminderType =
                getString(R.string.reminder_type)
                        + getString(
                                alarmType != DatabaseHelper.NOTIFICATION
                                        ? R.string.alarm
                                        : R.string.notification);
        userReminderMethodTextView.setText(reminderType);
    }

    @Override
    public void openFileSelect() {
        openDirectory();
    }

    public void openDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, 1);
    }

    @SuppressLint("WrongConstant")
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            Uri uri = intent.getData();
            this.grantUriPermission(
                    this.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            final int takeFlags = intent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.getContentResolver().takePersistableUriPermission(uri, takeFlags);
            userPill.setCustomAlarmUri(uri);
            String customAlarmString = getString(R.string.reminder_sound) + getFileName(uri);
            userAlarmSoundTextView.setText(customAlarmString);
        } catch (NullPointerException nullPointerException) {
            toasts.showCustomToast(getString(R.string.no_song_selected));
        }
    }

    @SuppressLint("Range")
    // Why the frick is it this convoluted to get a file name omg
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void hideIntervalSubText() {
        pillTimeIntervalText.setVisibility(View.GONE);
    }

    @Override
    public void openStartDatePicker() {
        dialogs.getStartDateDialog(userPill).show();
    }

    @Override
    public void setCachedTime(int cachedHour, int cachedMin) {
        this.cachedHour = cachedHour;
        this.cachedMin = cachedMin;
    }

    @Override
    public void applySelectedTime(String selectedTime) {
        System.out.println("Setting selected time = " + selectedTime);
        userPill.setTimesArray(new String[] {selectedTime});
        setTime(selectedTime);
        pillTimeAnimation.playAnimation();
    }

    public void setTime(String time) {
        pillTimesTextView.setVisibility(View.VISIBLE);
        pillTimesTextView.setText(time);
        timesTextView.setVisibility(View.GONE);
        userTimesTextView.setVisibility(View.VISIBLE);
        userTimesTextView.setText(this.getString(R.string.reminder_times).concat(time));
        areTextViewsNonEmpty();
    }

    @Override
    public void applyStockup(String stockupDate) {
        userPill.setStockupDate(stockupDate);
        pillStockupTextView.setText(
                new DateTimeManager().convertISODateStringToLocallyFormattedString(stockupDate));
        pillStockupSubtext.setVisibility(View.GONE);
        pillStockupTextView.setPadding(0, 0, 0, 0);
        pillDateAnimation.playAnimation();
    }

    public interface PillCreationListener {
        void notifyAddedPill(Pill pill);
    }
}
