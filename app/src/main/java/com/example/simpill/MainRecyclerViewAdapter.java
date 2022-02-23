package com.example.simpill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.TimeZone;


public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.MyViewHolder> {

    SharedPrefs sharedPrefs = new SharedPrefs();
    Dialogs dialogs = new Dialogs();
    DatabaseHelper myDatabase;
    Toasts toasts;
    DateTimeManager dateTimeManager;
    AlarmSetter alarmSetter;
    TimeZone userTimezone;

    String pillName;
    int alarmCodeForAllAlarms = 0;
    private final Context myContext;
    MainActivity mainActivity;
    Activity myActivity;
    Typeface truenoLight, truenoReg;

    MediaPlayer takenMediaPlayer, resetMediaPlayer;

    MainRecyclerViewAdapter(MainActivity mainActivity, Activity myActivity, Context myContext) {
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
            menu.add(this.getAbsoluteAdapterPosition() + 1, 1, 0, R.string.context_menu_update);
            menu.add(this.getAbsoluteAdapterPosition() + 1, 2, 0, R.string.context_menu_delete);
            menu.add(this.getAbsoluteAdapterPosition() + 1, 3, 0, R.string.context_menu_change_color);
        }
    }

    @Override
    public int getItemCount() {
        DatabaseHelper myDatabase = new DatabaseHelper(myContext);
        return myDatabase.getRowCount();
    }

    @NonNull
    @Override
    public MainRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.example_pill_new, parent, false));
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
        myDatabase = new DatabaseHelper(myContext);
        toasts = new Toasts();
        dateTimeManager = new DateTimeManager();
        pillName = myDatabase.getPillNameFromCursor(position);
        userTimezone = dateTimeManager.getUserTimezone();
        alarmSetter = new AlarmSetter(myContext, pillName);
    }

    private void initTextViews(MyViewHolder holder, String pillName) {
        truenoLight = ResourcesCompat.getFont(myContext, R.font.truenolight);
        truenoReg = ResourcesCompat.getFont(myContext, R.font.truenoreg);

        holder.pill_name_textview.setTypeface(truenoReg);
        holder.pill_time_textview.setTypeface(truenoReg);
        holder.pill_name_textview.setTextSize(27.0f);
        holder.pill_time_textview.setTextSize(15.0f);

        holder.pill_name_textview.setLetterSpacing(0.025f);
        holder.pill_time_textview.setLetterSpacing(0.025f);

        holder.pill_name_textview.setText(myDatabase.getPillName(pillName));

        if (!myDatabase.getTimeTaken(pillName).equals(myContext.getString(R.string.nullString))) {
            String takenTime = myContext.getString(R.string.taken_at, myDatabase.getTimeTaken(pillName));
            holder.pill_time_textview.setText(takenTime);
            holder.taken_btn.setVisibility(View.INVISIBLE);
            holder.taken_btn.setClickable(false);
            holder.reset_btn.setVisibility(View.VISIBLE);
            holder.reset_btn.setClickable(true);
        }
        else {
            String times;
            String frequencyString;
            
            if (sharedPrefs.get24HourFormatPref(myContext)) {
                times = myDatabase.convertArrayToString(myDatabase.getPillTime(pillName));
            } else {
                times = myDatabase.convertArrayToString(myDatabase.convert24HrArrayTo12HrArray(myContext, myDatabase.getPillTime(pillName)));
            }
            
            int pillFrequency = myDatabase.getFrequency(pillName);
            
            switch (pillFrequency) {
                case 0: case 1:
                    times = myContext.getString(R.string.take_at, times);
                    break;
                case 2:
                    frequencyString = myContext.getString(R.string.choose_frequency_dialog_every_other_day);
                    times = myContext.getString(R.string.take_at_custom_interval, times, frequencyString);
                    break;
                case 7:
                    frequencyString = myContext.getString(R.string.choose_frequency_dialog_weekly);
                    times = myContext.getString(R.string.take_at_custom_interval, times, frequencyString);
                    break;
                default:
                    frequencyString = myContext.getString(R.string.append_days_to_custom_interval, pillFrequency);
                    times = myContext.getString(R.string.take_at_custom_interval, times, frequencyString);
                    break;
            }

            holder.pill_time_textview.setText(times);
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
            if ((myDatabase.getPillAmount(pillName) > 0)) {
                toasts.showCustomToast(myContext, myContext.getString(R.string.pill_bottle_amount_toast, myDatabase.getPillAmount(pillName), pillName));
                shakeMediaPlayer.start();
            }
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
            if (!sharedPrefs.get24HourFormatPref(myContext)){
                currentTime = dateTimeManager.convert24HrTimeTo12HrTime(myContext, currentTime);
            }
            String takenTime = myContext.getString(R.string.taken_at, currentTime);

            myDatabase.setPillAmount(pillName, newPillAmount);
            myDatabase.setIsTaken(pillName, 1);
            myDatabase.setTimeTaken(pillName, currentTime);

            if (myDatabase.getPillAmount(pillName) != newPillAmount ||
                    myDatabase.getIsTaken(pillName) != 1 ||
                    myDatabase.convertArrayToString(myDatabase.getPillTime(pillName)).equals(myContext.getString(R.string.nullString))) {
                throw new SQLiteException();
            }

            holder.pill_time_textview.setText(takenTime);
            holder.taken_btn.setVisibility(View.INVISIBLE);
            holder.taken_btn.setClickable(false);
            holder.reset_btn.setVisibility(View.VISIBLE);
            holder.reset_btn.setClickable(true);
            takenMediaPlayer.start();

            deleteActiveNotifications();
            toasts.showCustomToast(myContext, myContext.getString(R.string.pill_taken_toast, pillName));
        });

        holder.reset_btn.setOnClickListener(v -> dialogs.getPillResetDialog(myContext, pillName, holder, position, resetMediaPlayer).show());

        holder.big_button.setOnClickListener(view -> {
            Intent intent = new Intent(myContext, UpdatePill.class);
            intent.putExtra(myContext.getString(R.string.primary_key_id), myDatabase.getPrimaryKeyId(pillName));
            intent.putExtra(myContext.getString(R.string.pill_name), myDatabase.getPillName(pillName));
            intent.putExtra(myContext.getString(R.string.pill_time), myDatabase.getPillTime(pillName));
            intent.putExtra(myContext.getString(R.string.pill_date), myDatabase.getPillDate(pillName));
            intent.putExtra(myContext.getString(R.string.pill_frequency), myDatabase.getFrequency(pillName));
            intent.putExtra(myContext.getString(R.string.pill_start_date), myDatabase.getStartDate(pillName));
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

    private void deleteActiveNotifications() {
        for (int currentNumber = 0; currentNumber < myDatabase.getPillTime(pillName).length; currentNumber++) {
            NotificationManagerCompat.from(myContext).cancel(pillName, myDatabase.getPrimaryKeyId(pillName) * 10 * 10 * 10 + currentNumber);
        }
    }

}
