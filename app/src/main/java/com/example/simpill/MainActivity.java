package com.example.simpill;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements Dialogs.PillDeleteDialogListener, Dialogs.PillResetDialogListener {

    private final SharedPrefs sharedPrefs = new SharedPrefs();
    private final DatabaseHelper myDatabase = new DatabaseHelper(MainActivity.this);
    private final Toasts toasts = new Toasts();

    public static int backPresses = 0;

    Dialogs dialogs = new Dialogs();

    RecyclerView recyclerView;

    MainRecyclerViewAdapter myAdapter;
    Button settingsButton, aboutButton, fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkOpenCount();

        setContentViewAndDesign();

        findViewsByIds();
        createRecyclerView();
        makeRecyclerViewItemsSwipeable();

        initiateButtons();
        isSqlDatabaseEmpty();
    }

    private void makeRecyclerViewItemsSwipeable() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();

                String pillName = myDatabase.getPillNameFromCursor(viewHolder.getLayoutPosition());

                switch (direction) {
                    case ItemTouchHelper.RIGHT:
                        myAdapter.notifyItemChanged(position);
                        dialogs.getPillDeletionDialog(getMainActivityContext(), pillName, position).show();
                        break;
                    case ItemTouchHelper.LEFT:
                        myAdapter.notifyItemChanged(position);
                        openUpdatePill(pillName);
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightActionIcon(R.drawable.ic_delete_svgrepo_com)
                        .addSwipeLeftActionIcon(R.drawable.ic_write_svgrepo_com)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void checkOpenCount() {
        int count = sharedPrefs.getOpenCountPref(this);
        count = count + 1;

        if(count == 100) {
            dialogs.getDonationDialog(this).show();
            count = 0;
        }
        sharedPrefs.setOpenCountPref(this, count);
    }
    private void setContentViewAndDesign() {
        int theme = sharedPrefs.getThemesPref(this);

        if (theme == Simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if (theme == Simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        } else if (theme == Simpill.BLACK_THEME) {
            setTheme(R.style.SimpillAppTheme_BlackBackground);
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

    public boolean onContextItemSelected(MenuItem item) {
        String pillName = myDatabase.getPillNameFromCursor(item.getGroupId() - 1);

        switch (item.getItemId()) {
            case 1:
                openUpdatePill(pillName);
                break;
            case 2:
                dialogs.getPillDeletionDialog(this, pillName, item.getGroupId() - 1).show();
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
            dialogs.getWelcomeDialog(this).show();
        }
    }

    @Override
    public void onBackPressed() {
        backPresses++;

        switch (backPresses) {
            case 1:
                toasts.showCustomToast(this, this.getString(R.string.press_back_again_toast));
                break;
            case 2:
                closeApp();
                backPresses = 0;
                break;
        }
    }

    private void openUpdatePill(String pillName) {
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
    }
    private void openCreatePillActivity() {
        Intent intent = new Intent(this, CreatePill.class);
        startActivity(intent);
    }
    private void openSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
        backPresses = 0;
    }
    private void openAboutActivity() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
        backPresses = 0;
    }

    public void closeApp() {
        ActivityCompat.finishAffinity(this);
        System.exit(0);
    }

    public Context getMainActivityContext() {
        return this;
    }


    @Override
    public void notifyAdapterOfDeletedPill(int position) {
        myAdapter.notifyItemRemoved(position);
    }


    @Override
    public void notifyAdapterOfResetPill(String pillName, MainRecyclerViewAdapter.MyViewHolder holder, int position, MediaPlayer resetSoundPlayer) {
        holder.reset_btn.setClickable(false);
        holder.reset_btn.setVisibility(View.INVISIBLE);
        holder.taken_btn.setClickable(true);
        holder.taken_btn.setVisibility(View.VISIBLE);

        myDatabase.setPillAmount(pillName, myDatabase.getPillAmount(pillName) + 1);
        myDatabase.setTimeTaken(pillName, getString(R.string.nullString));
        myDatabase.setIsTaken(pillName, 0);

        myAdapter.notifyItemChanged(position);

        toasts.showCustomToast(this, this.getString(R.string.pill_reset_toast, pillName));
        resetSoundPlayer.start();
    }
}