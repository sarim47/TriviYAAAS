package com.example.triviyaaas.ui.registration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.triviyaaas.database.DatabaseHelper;
import com.example.triviyaaas.ui.dashboard.HomeScreenActivity;
import com.example.triviyaaas.R;

public class LoginActivity extends AppCompatActivity {

    private AppCompatEditText emailAddressET, passwordET;
    private AppCompatButton loginButton;
    private CheckBox rememberCheckbox;
    private AppCompatTextView signUpTV;

    private static final String PREFS_NAME = "LoginPrefs";
    private DatabaseHelper dbHelper;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        initViews();
        if (getIntent().getBooleanExtra("fromRegister", false)) {
            clearSavedCredentials();
        } else {
            loadSavedCredentials();
        }

        loadSavedCredentials();
        setupPasswordToggle();

        loginButton.setOnClickListener(view -> {
            String email = emailAddressET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if (validateInputs(email, password)) {
                loginUser(email, password);
            }
        });

        signUpTV.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

    }

    private void initViews() {
        emailAddressET = findViewById(R.id.emailAddressET);
        passwordET = findViewById(R.id.passwordET);
        loginButton = findViewById(R.id.loginButton);
        rememberCheckbox = findViewById(R.id.rememberCheckbox);
        signUpTV = findViewById(R.id.signUpTV);
    }

    private void clearSavedCredentials() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        emailAddressET.setText("");
        passwordET.setText("");
        rememberCheckbox.setChecked(false);
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailAddressET.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordET.setError("Password is required");
            return false;
        }

        return true;
    }

    private void loginUser(String email, String password) {
        if (dbHelper.isUserExists(email)) {
            if (dbHelper.checkUser(email, password)) {
                saveCredentialsIfNeeded(email, password);
                saveLoginStatusAndFullName(email, rememberCheckbox.isChecked());
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeScreenActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Incorrect credentials", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No such user found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCredentialsIfNeeded(String email, String password) {
        if (rememberCheckbox.isChecked()) {
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.putBoolean("remember", true);
            editor.apply();
        }
    }

    private void loadSavedCredentials() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (preferences.getBoolean("remember", false)) {
            String email = preferences.getString("email", "");
            String password = preferences.getString("password", "");

            emailAddressET.setText(email);
            passwordET.setText(password);
            rememberCheckbox.setChecked(true);
        }
    }

    private void saveLoginStatusAndFullName(String email, boolean rememberMe) {
        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if (rememberMe) {
            editor.putBoolean("isLoggedIn", true);
        } else {
            editor.putBoolean("isLoggedIn", false); // Ensure it's false
        }

        String fullName = dbHelper.getFullNameByEmail(email);
        editor.putString("fullName", fullName);
        editor.apply();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordToggle() {
        passwordET.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordET.getRight() - passwordET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (isPasswordVisible) {
                        passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        passwordET.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0);
                    } else {
                        passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        passwordET.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_close, 0);
                    }

                    passwordET.setSelection(passwordET.getText().length());
                    isPasswordVisible = !isPasswordVisible;
                    return true;
                }
            }
            return false;
        });
    }

}
