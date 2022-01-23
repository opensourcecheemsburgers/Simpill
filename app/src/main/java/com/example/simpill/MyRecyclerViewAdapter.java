package com.example.simpill;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.TimeZone;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    Simpill simpill;
    PillDBHelper myDatabase;
    DateTimeManager dateTimeManager;
    AlarmSetter alarmSetter;
    TimeZone userTimezone;

    String pillName;
    int alarmCodeForAllAlarms = 0;
    private final Context myContext;
    MainActivity mainActivity;
    Activity myActivity;
    Typeface truenoLight, truenoReg;

    AlertDialog.Builder dialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    TextView titleTextView, titleMessageView;
    Button yesBtn, cancelBtn;
    Dialog warningDialog;

    MediaPlayer takenMediaPlayer, resetMediaPlayer;

    MyRecyclerViewAdapter(MainActivity mainActivity, Activity myActivity, Context myContext) {
        this.myActivity = myActivity;
        this.mainActivity = mainActivity;
        this.myContext = myContext;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView pill_time_textview, pill_name_textview;
        ImageButton taken_btn, reset_btn;
        Button big_button;
        ImageView pill_bottle_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pill_name_textview = itemView.findViewById(R.id.pillName);
            pill_time_textview = itemView.findViewById(R.id.pillTime);
            taken_btn = itemView.findViewById(R.id.tickButton);
            reset_btn = itemView.findViewById(R.id.resetButton);
            pill_bottle_image = itemView.findViewById(R.id.pillBottleImage);
            big_button = itemView.findViewById(R.id.bigButton);
            pill_bottle_image.setOnCreateContextMenuListener(this);
            big_button.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.add(this.getAbsoluteAdapterPosition() + 1, 1, 0, "Update Pill");
            menu.add(this.getAbsoluteAdapterPosition() + 1, 2, 0, "Delete Pill");
            menu.add(this.getAbsoluteAdapterPosition() + 1, 3, 0, "Change Bottle Color");
        }
    }

    @Override
    public int getItemCount() {
        PillDBHelper myDatabase = new PillDBHelper(myContext);
        return myDatabase.getRowCount();
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;


        simpill = (Simpill) myContext.getApplicationContext();

        loadSharedPrefs();

        if (simpill.getCustomTheme()){
            view = layoutInflater.inflate(R.layout.example_pill, parent, false);
        }
        else {
            view = layoutInflater.inflate(R.layout.example_pill_light, parent, false);
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        initAll(holder, position);

        alarmSetter.setAlarms(alarmCodeForAllAlarms);
    }



    private void initAll(MyViewHolder holder, int position) {
        initClasses(position);
        initTextViews(holder, pillName);
        initBottleImage(holder, pillName);
        initButtons(holder, pillName, position);
    }
    private void initClasses(int position) {
        myActivity = new Activity();
        myDatabase = new PillDBHelper(myContext);
        dateTimeManager = new DateTimeManager();
        pillName = myDatabase.getPillNameFromCursor(position);
        userTimezone = dateTimeManager.getUserTimezone();
        alarmSetter = new AlarmSetter(myContext, pillName, myDatabase.getPrimaryKeyId(pillName));
    }
    private void loadSharedPrefs() {
        SharedPreferences themePref = myContext.getSharedPreferences(Simpill.THEME_PREF_BOOLEAN, Context.MODE_PRIVATE);
        Boolean theme = themePref.getBoolean(Simpill.USER_THEME, true);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= myContext.getSharedPreferences(Simpill.IS_24HR_BOOLEAN, Context.MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
    }
    private void initTextViews(MyViewHolder holder, String pillName) {
        truenoLight = ResourcesCompat.getFont(myContext, R.font.truenolight);
        truenoReg = ResourcesCompat.getFont(myContext, R.font.truenoreg);

        holder.pill_name_textview.setTypeface(truenoLight);
        holder.pill_time_textview.setTypeface(truenoLight);
        holder.pill_name_textview.setTextSize(35.0f);
        holder.pill_time_textview.setTextSize(15.0f);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.pill_name_textview.setLetterSpacing(0.025f);
            holder.pill_time_textview.setLetterSpacing(0.025f);
        }

        holder.pill_name_textview.setText(myDatabase.getPillName(pillName));

        if (!myDatabase.getTimeTaken(pillName).equals(myContext.getString(R.string.nullString))) {
            String takenTime = myContext.getString(R.string.Taken_at) + " " + myDatabase.getTimeTaken(pillName);
            holder.pill_time_textview.setText(takenTime);
            holder.taken_btn.setVisibility(View.INVISIBLE);
            holder.taken_btn.setClickable(false);
            holder.reset_btn.setVisibility(View.VISIBLE);
            holder.reset_btn.setClickable(true);
        }
        else {
            String takeTime = myContext.getString(R.string.Take_at) + " " + myDatabase.getPillTime(pillName);
            if (!simpill.getUserIs24Hr()){
                takeTime = myContext.getString(R.string.Take_at) + " " + dateTimeManager.convert24HrTimeTo12HrTime(myContext, myDatabase.getPillTime(pillName));
            }
            holder.pill_time_textview.setText(takeTime);
            holder.reset_btn.setVisibility(View.INVISIBLE);
            holder.reset_btn.setClickable(false);
            holder.taken_btn.setVisibility(View.VISIBLE);
            holder.taken_btn.setClickable(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.pill_name_textview.setContextClickable(true);
            holder.pill_time_textview.setContextClickable(true);
        }
    }
    private void initBottleImage(MyViewHolder holder, String pillName) {
        MediaPlayer shakeMediaPlayer = MediaPlayer.create(myContext, R.raw.shake);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.pill_bottle_image.setContextClickable(true);
        }

        int bottleColor = myDatabase.getBottleColor(pillName);
        switch (bottleColor) {
            case 1:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_1));
                break;
            case 2: default:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_2));
                break;
            case 3:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_3));
                break;
            case 4:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_4));
                break;
            case 5:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_5));
                break;
            case 6:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_6));
                break;
            case 7:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_7));
                break;
            case 8:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_8));
                break;
            case 9:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_9));
                break;
            case 10:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_10));
                break;
            case 11:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_11));
                break;
            case 12:
                holder.pill_bottle_image.setImageDrawable(AppCompatResources.getDrawable(myContext, R.drawable.pill_bottle_color_12));
                break;
        }


        holder.pill_bottle_image.setOnClickListener(v -> {
            showCustomToast(1, holder, pillName);
            shakeMediaPlayer.start();
        });
    }
    private void initButtons(MyViewHolder holder, String pillName, int position) {
        takenMediaPlayer = MediaPlayer.create(myContext, R.raw.correct);
        resetMediaPlayer = MediaPlayer.create(myContext, R.raw.wrong);
        takenMediaPlayer.setVolume(0.5f, 0.5f);


        int thisPillAmount = myDatabase.getPillAmount(pillName);

        holder.taken_btn.setOnClickListener(v -> {
            int newPillAmount = thisPillAmount - 1;

            String currentTime = dateTimeManager.getCurrentTime(myContext, userTimezone);
            if (!simpill.getUserIs24Hr()){
                currentTime = dateTimeManager.convert24HrTimeTo12HrTime(myContext, currentTime);
            }
            String takenTime = myContext.getString(R.string.Taken_at) + " " + currentTime;

            myDatabase.setPillAmount(pillName, newPillAmount);
            myDatabase.setIsTaken(pillName, 1);
            myDatabase.setTimeTaken(pillName, currentTime);

            if (myDatabase.getPillAmount(pillName) != newPillAmount ||
                    myDatabase.getIsTaken(pillName) != 1 ||
                    myDatabase.getPillTime(pillName).equals(myContext.getString(R.string.nullString))) {
                throw new SQLiteException();
            }

            holder.pill_time_textview.setText(takenTime);
            holder.taken_btn.setVisibility(View.INVISIBLE);
            holder.taken_btn.setClickable(false);
            holder.reset_btn.setVisibility(View.VISIBLE);
            holder.reset_btn.setClickable(true);
            takenMediaPlayer.start();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(myContext);
            notificationManagerCompat.cancel(pillName, myDatabase.getPrimaryKeyId(pillName));

            showCustomToast(2, holder, pillName);
        });

        holder.reset_btn.setOnClickListener(v -> showPillResetWarningDialog(holder, position, pillName));

        holder.big_button.setOnClickListener(view -> {
            Intent intent = new Intent(myContext, UpdatePill.class);
            intent.putExtra(myContext.getString(R.string.primary_key_id), myDatabase.getPrimaryKeyId(pillName));
            intent.putExtra(myContext.getString(R.string.pill_name), myDatabase.getPillName(pillName));
            intent.putExtra(myContext.getString(R.string.pill_time), myDatabase.getPillTime(pillName));
            intent.putExtra(myContext.getString(R.string.pill_date), myDatabase.getPillDate(pillName));
            intent.putExtra(myContext.getString(R.string.pill_amount), myDatabase.getPillAmount(pillName));
            intent.putExtra(myContext.getString(R.string.is_pill_taken), myDatabase.getIsTaken(pillName));
            intent.putExtra(myContext.getString(R.string.time_taken), myDatabase.getTimeTaken(pillName));
            intent.putExtra(myContext.getString(R.string.bottle_color), myDatabase.getBottleColor(pillName));
            myContext.startActivity(intent);
            MainActivity.backPresses = 0;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.big_button.setContextClickable(true);
        }
    }

    private void showPillResetWarningDialog(MyViewHolder holder, int position, String pillName){
        initDialog();
        createDialog();
        setDialogTexts();
        setOnClickListeners(holder, position, pillName);
        warningDialog.show();
    }
    private void initDialog() {
        initDialogBuilder();
        initView();
        initTextViewsAndButtons();
    }
    private void initDialogBuilder() {
        dialogBuilder = new AlertDialog.Builder(myContext);
    }
    private void initView() {
        loadSharedPrefs();
        inflater = LayoutInflater.from(myContext);
        setViewBasedOnTheme();
    }
    private void setViewBasedOnTheme() {
        if (simpill.getCustomTheme())
        {
            dialogView = inflater.inflate(R.layout.warning_dialog_layout, null);
        }
        else {
            dialogView = inflater.inflate(R.layout.warning_dialog_layout_light, null);
        }
        dialogBuilder.setView(dialogView);
    }
    private void initTextViewsAndButtons() {
        titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        titleMessageView = dialogView.findViewById(R.id.dialogMessageTextView);
        yesBtn = dialogView.findViewById(R.id.btnYes);
        cancelBtn = dialogView.findViewById(R.id.btnNo);
    }
    private void createDialog() {
        warningDialog = dialogBuilder.create();
        warningDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }
    private void setDialogTexts() {
        titleTextView.setText(myContext.getString(R.string.reset_warning_title));
        titleMessageView.setText(myContext.getString(R.string.reset_warning_message));

        titleTextView.setTypeface(truenoReg);
        titleMessageView.setTypeface(truenoReg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleTextView.setLetterSpacing(0.025f);
            titleMessageView.setLetterSpacing(0.025f);
        }

        titleTextView.setTextSize(35.0f);
        titleMessageView.setTextSize(15.0f);
    }
    private void setOnClickListeners(MyViewHolder holder, int position, String pillName) {
        yesBtn.setOnClickListener(view -> {
            holder.reset_btn.setClickable(false);
            holder.reset_btn.setVisibility(View.INVISIBLE);
            holder.taken_btn.setClickable(true);
            holder.taken_btn.setVisibility(View.VISIBLE);

            myDatabase.setPillAmount(pillName, myDatabase.getPillAmount(pillName) + 1);
            myDatabase.setTimeTaken(pillName, myContext.getString(R.string.nullString));
            myDatabase.setIsTaken(pillName, 0);

            notifyItemChanged(position);

            showCustomToast(3, holder, pillName);
            warningDialog.dismiss();
            resetMediaPlayer.start();
        });
        cancelBtn.setOnClickListener(view -> warningDialog.dismiss());
    }

    private void showCustomToast(int toastNumber, MyViewHolder holder, String pillName) {
        LayoutInflater layoutInflater = LayoutInflater.from(myContext);

        View toastLayout;
        if (simpill.getCustomTheme()) {
            toastLayout = layoutInflater.inflate(R.layout.custom_toast, holder.itemView.findViewById(R.id.custom_toast_layout));
        } else {
            toastLayout = layoutInflater.inflate(R.layout.custom_toast_light, holder.itemView.findViewById(R.id.custom_toast_layout_light));
        }

        Toast toast = new Toast(myContext);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 250);
        toast.setView(toastLayout);

        TextView toastTextView = toastLayout.findViewById(R.id.custom_toast_message);

        switch (toastNumber) {
            case 1:
                toastTextView.setText("You have " + myDatabase.getPillAmount(pillName) + " " + pillName + " pills left.");
                break;
            case 2:
                toastTextView.setText(pillName + " taken.");
                break;
            case 3:
                toastTextView.setText(pillName + " reset.");
                break;
        }

        toast.show();
    }

}
