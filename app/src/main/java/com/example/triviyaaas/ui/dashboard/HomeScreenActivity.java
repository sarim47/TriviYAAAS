package com.example.triviyaaas.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.triviyaaas.R;
import com.example.triviyaaas.ui.dashboard.bottomnav.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class HomeScreenActivity extends BaseActivity {

    BottomNavigationView bottomNavigationView;
    AppCompatTextView salutationTV, fullNameTV;
    ViewFlipper factsFlipper;
    LayoutInflater inflater;
    TextInputEditText searchEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        initViews();
        setupBottomNavigation();
        setFloatingButtonAction();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        setGreetings();
        setFullName();
        setFacts();
        setSearchFunctionality();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        salutationTV = findViewById(R.id.salutationTV);
        fullNameTV = findViewById(R.id.fullNameTV);
        factsFlipper = findViewById(R.id.factsFlipper);
        inflater = LayoutInflater.from(this);
        searchEditText = findViewById(R.id.searchEditText);

    }

    private void setGreetings() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Australia/Canberra"));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = getString(R.string.good_morning_label);
        } else if (hour >= 12 && hour < 17) {
            greeting = getString(R.string.good_afternoon_label);
        } else {
            greeting = getString(R.string.good_evening_label);
        }

        salutationTV.setText(greeting);
    }

    private void setFullName() {
        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fullName = sharedPrefs.getString("fullName", "User");

        fullNameTV.setText(fullName);
    }

    private void setFacts() {

        String[] amazingFacts = {
                "\uD83D\uDCA1 Bananas are berries, but strawberries aren't.",
                "\uD83E\uDDA0 Octopuses have three hearts.",
                "\uD83E\uDDF9 A group of flamingos is called a 'flamboyance'.",
                "\uD83C\uDF6F Honey never spoils – it lasts thousands of years.",
                "\uD83C\uDDEB\uD83C\uDDF7 The Eiffel Tower grows taller in summer.",
                "\uD83E\uDD88 Sharks existed before trees.",
                "\uD83C\uDF0C A day on Venus is longer than its year.",
                "\uD83D\uDC2E Cows have best friends and get stressed when separated.",
                "\u2728 There are more stars than grains of sand on Earth.",
                "\uD83D\uDCA9 Wombat poop is cube-shaped.",
                "\uD83D\uDC3F Sloths can hold their breath longer than dolphins.",
                "\uD83E\uDD9D An octopus has nine brains and blue blood."
        };

        List<String> factsList = Arrays.asList(amazingFacts);
        Collections.shuffle(factsList); // randomizes the order

        for (String fact : factsList) {
            View factView = inflater.inflate(R.layout.facts_item_layout, factsFlipper, false);
            TextView factText = factView.findViewById(R.id.factText);
            factText.setText(fact);
            factsFlipper.addView(factView);
        }
    }

    private void setSearchFunctionality() {
        searchEditText.setFocusable(false);
        searchEditText.setOnClickListener(v -> {
            startActivity(new Intent(HomeScreenActivity.this, SearchActivity.class));
        });
    }
}