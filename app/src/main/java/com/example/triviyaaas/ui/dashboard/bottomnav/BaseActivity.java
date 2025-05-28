package com.example.triviyaaas.ui.dashboard.bottomnav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.triviyaaas.R;
import com.example.triviyaaas.ui.dashboard.CategoriesActivity;
import com.example.triviyaaas.ui.dashboard.HomeScreenActivity;
import com.example.triviyaaas.ui.dashboard.ResultsActivity;
import com.example.triviyaaas.ui.registration.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public abstract class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setFloatingButtonAction() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AddQuestionBottomSheet bottomSheet = new AddQuestionBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

    }
    protected void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    if (!(this instanceof HomeScreenActivity)) {
                        startActivity(new Intent(this, HomeScreenActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                    return true;
                } else if (id == R.id.nav_categories) {
                    if (!(this instanceof CategoriesActivity)) {
                        startActivity(new Intent(this, CategoriesActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                    return true;
                } else if (id == R.id.nav_results) {
                    if (!(this instanceof ResultsActivity)) {
                        startActivity(new Intent(this, ResultsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                    return true;
                } else if (id == R.id.nav_logout) {
                    showLogoutConfirmation();
                    return true;
                }
                return false;
            });
        }
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, id) -> logoutUser())
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void logoutUser() {
        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
