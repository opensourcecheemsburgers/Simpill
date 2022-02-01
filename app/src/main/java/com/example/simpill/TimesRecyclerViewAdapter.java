package com.example.simpill;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimesRecyclerViewAdapter extends RecyclerView.Adapter<TimesRecyclerViewAdapter.MyViewHolder> {


    Context context;

    Simpill simpill;
    DatabaseHelper myDatabase;
    Dialogs dialogs;
    Toasts toasts;
    DateTimeManager dateTimeManager;

    String[] times;

    int itemCount;


    TimesRecyclerViewAdapter(Context myContext, int clocks) {
        this.context = myContext;
        this.itemCount = clocks;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageButton clockBtn, resetBtn;
        Button doneBtn;
        TextView timeTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            clockBtn = itemView.findViewById(R.id.clock_image);
            timeTextView = itemView.findViewById(R.id.time_textview);
            doneBtn = itemView.findViewById(R.id.btnDone);

        }
    }

    @NonNull
    @Override
    public TimesRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View clocksView = inflater.inflate(R.layout.clock_recycler_view_item, parent, false);
        MyViewHolder holder = new MyViewHolder(clocksView);
        holder.setIsRecyclable(false);

        times = new String[getItemCount()];

        return new TimesRecyclerViewAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.clock_recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        initClasses();
        setOnClickListeners(holder, position);

        holder.timeTextView.setText(context.getString(R.string.clocks_dialog_time_text) + (position+1));

        times[position] = holder.timeTextView.getText().toString();
    }

    public String[] returnTimeStringsArrayFromRecyclerViewClass() {
        return times;
    }


    private void setOnClickListeners(MyViewHolder holder, int position) {
        holder.clockBtn.setOnClickListener(view -> showTimePicker(holder, position));
    }


    private void showTimePicker(MyViewHolder holder, int position) {
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
                time = dateTimeManager.convert12HrTimeTo24HrTime(context, time);
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
            times[position] = time;
            holder.timeTextView.setText(time);
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.MyTimePickerDialogStyle, timeSetListener, 12, 0, simpill.getUserIs24Hr());
        timePickerDialog.show();
    }


    private void initClasses() {
        simpill = new Simpill();
        myDatabase = new DatabaseHelper(context);
        dialogs = new Dialogs();
        toasts = new Toasts();
        dateTimeManager = new DateTimeManager();
    }

    @Override
    public int getItemCount() {
        return this.itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public interface timesRecyclerViewListener {
        void returnTimesStringArray(String[] times);
    }
}
