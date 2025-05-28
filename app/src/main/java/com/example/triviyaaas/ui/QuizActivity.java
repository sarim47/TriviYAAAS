package com.example.triviyaaas.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.triviyaaas.R;
import com.example.triviyaaas.database.DatabaseHelper;
import com.example.triviyaaas.ui.dashboard.ResultsActivity;

public class QuizActivity extends AppCompatActivity {

    private AppCompatTextView questionTextView, selectedQuizName, marksTV;
    private AppCompatButton optionA, optionB, optionC, optionD, nextButton, submitButton;

    private Cursor questionCursor;
    private int currentIndex = 0;
    private int totalQuestions;
    private String correctAnswer;
    private AppCompatButton selectedOption = null;
    private int correctAnswers = 0;

    int categoryId, number;
    String quizName, difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initViews();
        handleIntent();
        showQuestion(currentIndex);

        submitButton.setOnClickListener(v -> {
            if (selectedOption == null) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedText = selectedOption.getText().toString();

            boolean isCorrect = selectedText.equalsIgnoreCase(correctAnswer);

            if (isCorrect) {
                selectedOption.setBackgroundTintList(getColorStateList(R.color.colorSuccess));
                correctAnswers++;
                updateScoreDisplay();
            } else {
                selectedOption.setBackgroundTintList(getColorStateList(R.color.colorError));
            }

            highlightCorrectOption();
            disableAllOptions();
        });


        nextButton.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex < totalQuestions) {
                showQuestion(currentIndex);
                if (currentIndex == totalQuestions - 1) {
                    nextButton.setText(getString(R.string.end));
                }
            } else {
                Toast.makeText(this, "Quiz completed! Please check results.", Toast.LENGTH_SHORT).show();
                finishQuiz();
            }
        });


        View.OnClickListener optionClickListener = view -> {
            resetOptionStyles();
            selectedOption = (AppCompatButton) view;
            selectedOption.setBackgroundTintList(getColorStateList(R.color.colorOnBackground));
        };

        optionA.setOnClickListener(optionClickListener);
        optionB.setOnClickListener(optionClickListener);
        optionC.setOnClickListener(optionClickListener);
        optionD.setOnClickListener(optionClickListener);
    }

    private void initViews() {
        questionTextView = findViewById(R.id.questionTextView);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        nextButton = findViewById(R.id.nextButton);
        submitButton = findViewById(R.id.submitButton);
        selectedQuizName = findViewById(R.id.selectedQuizName);
        marksTV = findViewById(R.id.marksTV);
    }

    private void handleIntent() {
        categoryId = getIntent().getIntExtra("categoryId", -1);
        quizName = getIntent().getStringExtra("quizName");
        number = getIntent().getIntExtra("number", 1);
        difficulty = getIntent().getStringExtra("difficulty");

        totalQuestions = number;
        selectedQuizName.setText(quizName);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        questionCursor = dbHelper.getQuestionsForQuiz(categoryId, quizName, number, difficulty);

        if (questionCursor == null || questionCursor.getCount() == 0) {
            Toast.makeText(this, "No questions found", Toast.LENGTH_SHORT).show();
            finish();
        }
        updateScoreDisplay();
    }

    private void showQuestion(int index) {
        if (questionCursor.moveToPosition(index)) {
            String question = questionCursor.getString(questionCursor.getColumnIndexOrThrow("question_text"));
            String a = questionCursor.getString(questionCursor.getColumnIndexOrThrow("option_a"));
            String b = questionCursor.getString(questionCursor.getColumnIndexOrThrow("option_b"));
            String c = questionCursor.getString(questionCursor.getColumnIndexOrThrow("option_c"));
            String d = questionCursor.getString(questionCursor.getColumnIndexOrThrow("option_d"));
            correctAnswer = questionCursor.getString(questionCursor.getColumnIndexOrThrow("correct_option"));

            questionTextView.setText(question);
            optionA.setText(a);
            optionB.setText(b);
            optionC.setText(c);
            optionD.setText(d);

            selectedOption = null;
            enableAllOptions();
            resetOptionStyles();

            submitButton.setEnabled(true);
            nextButton.setEnabled(true);
        }
    }

    private void updateScoreDisplay() {
        marksTV.setText(correctAnswers + "/" + totalQuestions);
    }

    private void highlightCorrectOption() {
        if (optionA.getText().toString().equalsIgnoreCase(correctAnswer)) {
            optionA.setBackgroundTintList(getColorStateList(R.color.colorSuccess));
        } else if (optionB.getText().toString().equalsIgnoreCase(correctAnswer)) {
            optionB.setBackgroundTintList(getColorStateList(R.color.colorSuccess));
        } else if (optionC.getText().toString().equalsIgnoreCase(correctAnswer)) {
            optionC.setBackgroundTintList(getColorStateList(R.color.colorSuccess));
        } else if (optionD.getText().toString().equalsIgnoreCase(correctAnswer)) {
            optionD.setBackgroundTintList(getColorStateList(R.color.colorSuccess));
        }
    }

    private void resetOptionStyles() {
        optionA.setBackgroundTintList(getColorStateList(R.color.colorSecondary));
        optionB.setBackgroundTintList(getColorStateList(R.color.colorSecondary));
        optionC.setBackgroundTintList(getColorStateList(R.color.colorSecondary));
        optionD.setBackgroundTintList(getColorStateList(R.color.colorSecondary));
    }

    private void enableAllOptions() {
        optionA.setEnabled(true);
        optionB.setEnabled(true);
        optionC.setEnabled(true);
        optionD.setEnabled(true);
    }

    private void disableAllOptions() {
        optionA.setEnabled(false);
        optionB.setEnabled(false);
        optionC.setEnabled(false);
        optionD.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (questionCursor != null) {
            questionCursor.close();
        }
    }

    private void finishQuiz() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        dbHelper.saveQuizScore(quizName, difficulty, totalQuestions, correctAnswers);

        Intent intent = new Intent(QuizActivity.this, ResultsActivity.class);
        startActivity(intent);
        finish();
    }
}
