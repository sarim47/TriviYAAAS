package com.example.triviyaaas.ui.dashboard;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviyaaas.R;
import com.example.triviyaaas.adapters.ResultAdapter;
import com.example.triviyaaas.database.DatabaseHelper;
import com.example.triviyaaas.model.Result;
import com.example.triviyaaas.ui.dashboard.bottomnav.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends BaseActivity {
    BottomNavigationView bottomNavigationView;
    private RecyclerView resultsRV;
    private ResultAdapter resultAdapter;
    private AppCompatImageView emptyStateImage;

    private List<Result> resultList = new ArrayList<>();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        initViews();
        setupBottomNavigation();
        setFloatingButtonAction();
        bottomNavigationView.setSelectedItemId(R.id.nav_results);

        dbHelper = new DatabaseHelper(this);

        resultsRV.setLayoutManager(new LinearLayoutManager(this));
        resultAdapter = new ResultAdapter(this, resultList);
        resultsRV.setAdapter(resultAdapter);

        loadResultsFromDatabase();
    }

    private void initViews() {
        resultsRV = findViewById(R.id.resultsRV);
        emptyStateImage = findViewById(R.id.emptyStateImage);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void loadResultsFromDatabase() {
        Cursor cursor = dbHelper.getAllScores();
        resultList.clear(); // Important to avoid duplication

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("quiz_name"));
                String difficulty = cursor.getString(cursor.getColumnIndexOrThrow("difficulty"));
                int total = cursor.getInt(cursor.getColumnIndexOrThrow("total_questions"));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));

                resultList.add(new Result(title, difficulty, score, total));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (resultList.isEmpty()) {
            emptyStateImage.setVisibility(View.VISIBLE);
            resultsRV.setVisibility(View.GONE);
        } else {
            emptyStateImage.setVisibility(View.GONE);
            resultsRV.setVisibility(View.VISIBLE);
        }

        resultAdapter.notifyDataSetChanged();
    }

}