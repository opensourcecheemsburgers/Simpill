/* (C) 2022 */
package com.example.simpill;

import static com.example.simpill.Pill.PRIMARY_KEY_INTENT_KEY_STRING;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class ChooseColor extends AppCompatActivity {

    final DatabaseHelper myDatabase = new DatabaseHelper(this);
    int selectedColor = 2;
    final ImageView[] bottles = new ImageView[12];
    Button backButton;
    Typeface truenoReg;

    Pill pill;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pill = myDatabase.getPill(getIntent().getIntExtra(PRIMARY_KEY_INTENT_KEY_STRING, -1));
        setContentViewBasedOnThemeSetting();
        findViewsByIds();
        createOnClickListeners();
    }

    private void findViewsByIds() {
        backButton = findViewById(R.id.back_button);

        truenoReg = ResourcesCompat.getFont(this, R.font.inter_reg);

        bottles[0] = findViewById(R.id.imageView1);
        bottles[1] = findViewById(R.id.imageView2);
        bottles[2] = findViewById(R.id.imageView3);
        bottles[3] = findViewById(R.id.imageView4);
        bottles[4] = findViewById(R.id.imageView5);
        bottles[5] = findViewById(R.id.imageView6);
        bottles[6] = findViewById(R.id.imageView7);
        bottles[7] = findViewById(R.id.imageView8);
        bottles[8] = findViewById(R.id.imageView9);
        bottles[9] = findViewById(R.id.imageView10);
        bottles[10] = findViewById(R.id.imageView11);
        bottles[11] = findViewById(R.id.imageView12);
    }

    private void createOnClickListeners() {
        backButton.setOnClickListener(view -> finish());

        for (int index = 0; index < bottles.length; index++) {
            int color = index + 1;
            bottles[index].setOnClickListener(
                    v -> {
                        pill.setBottleColor(color);
                        pill.updatePillInDatabase(this);
                        finish();
                    });
        }
    }

    private void setContentViewBasedOnThemeSetting() {
        int theme = new SharedPrefs(this).getThemesPref();

        if (theme == Simpill.BLUE_THEME) {
            setTheme(R.style.SimpillAppTheme_BlueBackground);
        } else if (theme == Simpill.GREY_THEME) {
            setTheme(R.style.SimpillAppTheme_GreyBackground);
        } else if (theme == Simpill.BLACK_THEME) {
            setTheme(R.style.SimpillAppTheme_BlackBackground);
        } else {
            setTheme(R.style.SimpillAppTheme_PurpleBackground);
        }

        setContentView(R.layout.app_choose_color);
    }
}
