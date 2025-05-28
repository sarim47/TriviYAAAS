package com.example.triviyaaas.ui.dashboard.bottomnav;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.example.triviyaaas.R;
import com.example.triviyaaas.database.DatabaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddQuestionBottomSheet extends BottomSheetDialogFragment {

    private DatabaseHelper dbHelper;

    private AppCompatAutoCompleteTextView categoryDropdown, quizDropdown;
    private AppCompatEditText questionET, optionAET, optionBET, optionCET, optionDET, correctOptionET;
    private AppCompatButton saveBtn;
    private RadioGroup difficultyRadioGroup;
    private Long timestamp;


    private Map<String, Integer> categoryMap = new HashMap<>();
    private Map<String, Integer> quizMap = new HashMap<>();
    private int selectedQuizId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_question, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        timestamp = System.currentTimeMillis();

        categoryDropdown = view.findViewById(R.id.categoryDropdown);
        quizDropdown = view.findViewById(R.id.quizDropdown);
        questionET = view.findViewById(R.id.questionET);
        optionAET = view.findViewById(R.id.optionAET);
        optionBET = view.findViewById(R.id.optionBET);
        optionCET = view.findViewById(R.id.optionCET);
        optionDET = view.findViewById(R.id.optionDET);
        correctOptionET = view.findViewById(R.id.correctOption);
        difficultyRadioGroup = view.findViewById(R.id.difficultyRadioGroup);
        saveBtn = view.findViewById(R.id.saveQuestionBtn);

        loadCategories();

        saveBtn.setOnClickListener(v -> saveQuestion());

        return view;
    }

    private void loadCategories() {
        Cursor cursor = dbHelper.getCategories();
        List<String> categoryNames = new ArrayList<>();
        categoryMap.clear();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                categoryMap.put(name, id);
                categoryNames.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
        categoryDropdown.setAdapter(adapter);

        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = parent.getItemAtPosition(position).toString();
            int categoryId = categoryMap.get(selectedCategory);
            selectedQuizId = -1; // Reset in case quiz becomes stale
            loadQuizzesForCategory(categoryId);
        });

        categoryDropdown.setOnClickListener(v -> categoryDropdown.showDropDown());
    }

    private void loadQuizzesForCategory(int categoryId) {
        quizMap.clear();
        Cursor cursor = dbHelper.getQuizzesByCategory(categoryId);
        List<String> quizTitles = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                quizMap.put(title, id);
                quizTitles.add(title);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, quizTitles);
        quizDropdown.setAdapter(adapter);

        quizDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedQuiz = parent.getItemAtPosition(position).toString();
            selectedQuizId = quizMap.get(selectedQuiz);
        });

        quizDropdown.setOnClickListener(v -> quizDropdown.showDropDown());
    }

    private void saveQuestion() {
        if (selectedQuizId == -1) {
            Toast.makeText(getContext(), "Please select a quiz", Toast.LENGTH_SHORT).show();
            return;
        }

        String questionText = questionET.getText().toString().trim();
        String optionA = optionAET.getText().toString().trim();
        String optionB = optionBET.getText().toString().trim();
        String optionC = optionCET.getText().toString().trim();
        String optionD = optionDET.getText().toString().trim();
        String correctOption = correctOptionET.getText().toString().trim();
        String difficulty = "";
        int selectedId = difficultyRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radioEasy) {
            difficulty = "easy";
        } else if (selectedId == R.id.radioMedium) {
            difficulty = "medium";
        } else if (selectedId == R.id.radioHard) {
            difficulty = "hard";
        } else {
            Toast.makeText(getContext(), "Please select difficulty", Toast.LENGTH_SHORT).show();
            return;
        }


        if (questionText.isEmpty() || correctOption.isEmpty() || difficulty.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quiz_id", selectedQuizId);
        values.put("question_text", questionText);
        values.put("option_a", optionA);
        values.put("option_b", optionB);
        values.put("option_c", optionC);
        values.put("option_d", optionD);
        values.put("correct_option", correctOption);
        values.put("difficulty", difficulty);
        values.put("created_at", timestamp);

        long result = db.insert("questions", null, values);

        if (result != -1) {
            Toast.makeText(getContext(), "Question added successfully", Toast.LENGTH_SHORT).show();
            dismiss();
        } else {
            Toast.makeText(getContext(), "Failed to add question", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}

