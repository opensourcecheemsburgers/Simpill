package com.example.simpill;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {


    private Simpill simpill = new Simpill();
    private PillDBHelper myDatabase = new PillDBHelper(MainActivity.this);
    private Toasts toasts = new Toasts();

    public static int backPresses = 0;

    RecyclerView recyclerView;

    MainRecyclerViewAdapter myAdapter;
    Button settingsButton, aboutButton, fab;

    AlertDialog.Builder dialogBuilder;
    Dialog warningDialog;
    LayoutInflater inflater;
    View dialogView;
    Typeface truenoReg;
    TextView titleTextView, titleMessageView;
    Button yesBtn, cancelBtn;

    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSharedPrefs();

        setContentViewAndDesign();

        findViewsByIds();
        createRecyclerView();
        initiateButtons();
        isSqlDatabaseEmpty();
    }

    private void loadSharedPrefs() {
        SharedPreferences themePref = getSharedPreferences(Simpill.SELECTED_THEME, MODE_PRIVATE);
        int theme = themePref.getInt(Simpill.USER_THEME, 1);
        simpill.setCustomTheme(theme);
        SharedPreferences is24HrPref= getSharedPreferences(Simpill.IS_24HR_BOOLEAN, MODE_PRIVATE);
        Boolean is24Hr = is24HrPref.getBoolean(Simpill.USER_IS_24HR, true);
        simpill.setUserIs24Hr(is24Hr);
    }
    private void setContentViewAndDesign() {

        int theme = simpill.getCustomTheme();

        if (theme == simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if(theme == simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        }
        else {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }

        setContentView(R.layout.app_main);
    }

    private void findViewsByIds() {
        settingsButton = findViewById(R.id.settingsButton);
        aboutButton = findViewById(R.id.aboutButton);
        fab = findViewById(R.id.floating_action_button);
        recyclerView = findViewById(R.id.recyclerView);
    }
    private void initiateButtons() {
        settingsButton.setOnClickListener(v -> openSettingsActivity());
        aboutButton.setOnClickListener(v -> openAboutActivity());
        fab.setOnClickListener(v -> openCreatePillActivity());
    }

    private void createRecyclerView() {
        myAdapter = new MainRecyclerViewAdapter(MainActivity.this, getParent(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
    }

    public void notifyAdapter(int position){
        myAdapter.notifyItemRemoved(position);
    }
    public boolean onContextItemSelected(MenuItem item) {

        //This needs the pill name instead.

        String pillName = myDatabase.getPillNameFromCursor(item.getGroupId() - 1);

        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(this, UpdatePill.class);
                intent.putExtra(getString(R.string.pill_name), pillName);
                intent.putExtra(getString(R.string.pill_time), myDatabase.getPillTime(pillName));
                intent.putExtra(getString(R.string.pill_date), myDatabase.getPillDate(pillName));
                intent.putExtra(getString(R.string.pill_amount), myDatabase.getPillAmount(pillName));
                intent.putExtra(getString(R.string.is_pill_taken), myDatabase.getIsTaken(pillName));
                intent.putExtra(getString(R.string.time_taken), myDatabase.getTimeTaken(pillName));
                intent.putExtra(getString(R.string.bottle_color), myDatabase.getBottleColor(pillName));
                startActivity(intent);
                backPresses = 0;
                break;
            case 2:
                showPillResetWarningDialog(item.getGroupId() - 1, pillName);
                break;
            case 3:
                Intent intent1 = new Intent(this, ChooseColor.class);
                intent1.putExtra(getString(R.string.pill_name), pillName);
                startActivity(intent1);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    void isSqlDatabaseEmpty() {
        if (myDatabase.readSqlDatabase().getCount() == 0) {
            myDatabase.createTestingPills();
            new Dialogs().getWelcomeDialog(this).show();
        }
    }

    @Override
    public void onBackPressed() {
        backPresses++;

        switch (backPresses) {
            case 1:
                toasts.showCustomToast(this, "Press the back button again to exit.");
                break;
            case 2:
                closeApp();
                backPresses = 0;
                break;
        }
    }

    private void openCreatePillActivity() {
        Intent intent = new Intent(this, CreatePill.class);
        startActivity(intent);
    }
    private void openSettingsActivity() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        backPresses = 0;
    }
    private void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
        backPresses = 0;
    }


    private void showPillResetWarningDialog(int position, String pillName) {
        initDialog();
        createDialog();
        setDialogTexts();
        setOnClickListeners(position, pillName);
        warningDialog.show();
    }
    private void initDialog() {
        initDialogBuilder();
        initView();
        initTextViewsAndButtons();
    }
    private void initDialogBuilder() {
        dialogBuilder = new AlertDialog.Builder(this);
    }
    private void initView() {
        loadSharedPrefs();
        inflater = LayoutInflater.from(this);
        setViewBasedOnTheme();
    }
    private void setViewBasedOnTheme() {
        dialogView = inflater.inflate(R.layout.dialog_delete_pill, null);
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
        truenoReg = ResourcesCompat.getFont(this, R.font.truenoreg);

        titleTextView.setText(getString(R.string.pill_deletion_dialog_title));
        titleMessageView.setText(getString(R.string.pill_deletion_dialog_message));

        titleTextView.setTypeface(truenoReg);
        titleMessageView.setTypeface(truenoReg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleTextView.setLetterSpacing(0.025f);
            titleMessageView.setLetterSpacing(0.025f);
        }

        titleTextView.setTextSize(35.0f);
        titleMessageView.setTextSize(15.0f);
    }
    private void setOnClickListeners(int position, String pillName) {
        yesBtn.setOnClickListener(view -> {
            if(myDatabase.deletePill(pillName)) {
                toasts.showCustomToast(this,pillName + getString(R.string.append_pill_deleted_toast));
                notifyAdapter(position);
                warningDialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(view -> warningDialog.dismiss());
    }

    public void closeApp() {
        finishAffinity();
        System.exit(0);
    }
}