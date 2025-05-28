package com.example.triviyaaas.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.triviyaaas.R;
import com.example.triviyaaas.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private AppCompatEditText emailInput, passwordInput, confirmPasswordInput, fullNameInput;
    private AppCompatButton registerButton;
    private AppCompatImageButton backButton;
    private AppCompatTextView loginTV;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        initViews();

        registerButton.setOnClickListener(v -> registerUser());

        backButton.setOnClickListener(view ->{
           goToLogin();
        });

        loginTV.setOnClickListener(view ->{
            goToLogin();
        });
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailAddressET);
        passwordInput = findViewById(R.id.setPasswordET);
        confirmPasswordInput = findViewById(R.id.retypePasswordET);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
        loginTV = findViewById(R.id.loginTV);
        fullNameInput = findViewById(R.id.fullNameET);
    }
    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String fullName = fullNameInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must have 1 uppercase, 1 digit, and be at least 6 characters", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.checkEmailExists(email)) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = DatabaseHelper.hashPassword(password);
        boolean inserted = dbHelper.insertUser(email, hashedPassword, fullName);

        if (inserted) {
            Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
            fullNameInput.setText("");
            emailInput.setText("");
            passwordInput.setText("");
            confirmPasswordInput.setText("");
            goToLogin();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d).{6,}$";
        return password.matches(regex);
    }

    private void goToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("fromRegister", true);
        startActivity(intent);
        finish();

    }
}
