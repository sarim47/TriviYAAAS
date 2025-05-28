package com.example.triviyaaas.ui.dashboard;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviyaaas.R;
import com.example.triviyaaas.adapters.CategoriesAdapter;
import com.example.triviyaaas.adapters.QuizAdapter;
import com.example.triviyaaas.database.DatabaseHelper;
import com.example.triviyaaas.ui.dashboard.bottomnav.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CategoriesActivity extends BaseActivity {
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    CategoriesAdapter categoryAdapter;
    QuizAdapter quizAdapter;
    AppCompatTextView categoriesLabel;
    Cursor cursor;
    boolean isGrid = true;
    boolean isQuizListVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        initViews();
        setupBottomNavigation();
        setFloatingButtonAction();
        bottomNavigationView.setSelectedItemId(R.id.nav_categories);


        DatabaseHelper dbHelper = new DatabaseHelper(this);
        cursor = dbHelper.getCategories();

        recyclerView = findViewById(R.id.categoriesRV);
        categoryAdapter = new CategoriesAdapter(this, cursor, isGrid);
        recyclerView.setAdapter(categoryAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        ImageView gridIcon = findViewById(R.id.gridIcon);
        ImageView listIcon = findViewById(R.id.listIcon);

        listIcon.setOnClickListener(v -> toggleLayoutManager(false, gridIcon, listIcon));
        gridIcon.setOnClickListener(v -> toggleLayoutManager(true, gridIcon, listIcon));

        RecyclerView quizListRV = findViewById(R.id.categoriesItemRV);
        quizListRV.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAdapter(this, null);
        quizListRV.setAdapter(quizAdapter);

        categoryAdapter.setOnCategoryClickListener(categoryId -> {
            Cursor quizCursor = dbHelper.getQuizzesByCategory(categoryId);
            quizAdapter.swapCursor(quizCursor);

            quizListRV.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            gridIcon.setVisibility(View.GONE);
            listIcon.setVisibility(View.GONE);
            categoriesLabel.setText(getString(R.string.available_quizzes));
            isQuizListVisible = true;
        });
    }

    private void toggleLayoutManager(boolean isGridLayout, ImageView gridIcon, ImageView listIcon) {
        this.isGrid = isGridLayout;

        recyclerView.setLayoutManager(isGridLayout ? new GridLayoutManager(this, 2) : new LinearLayoutManager(this));
        categoryAdapter.setIsGrid(isGridLayout);
        categoryAdapter.notifyDataSetChanged();

        listIcon.setVisibility(isGridLayout ? View.VISIBLE : View.GONE);
        gridIcon.setVisibility(isGridLayout ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isQuizListVisible) {
            // When coming back from quiz list, show the categories
            RecyclerView quizListRV = findViewById(R.id.categoriesItemRV);
            quizListRV.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            ImageView gridIcon = findViewById(R.id.gridIcon);
            ImageView listIcon = findViewById(R.id.listIcon);

            if (isGrid) {
                gridIcon.setVisibility(View.GONE);
                listIcon.setVisibility(View.VISIBLE);
            } else {
                gridIcon.setVisibility(View.VISIBLE);
                listIcon.setVisibility(View.GONE);
            }

            categoriesLabel.setText(getString(R.string.categories));

            // Fetch the categories again to restore state
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            cursor = dbHelper.getCategories();
            categoryAdapter.swapCursor(cursor);

            isQuizListVisible = false;
        } else {
            super.onBackPressed();
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        categoriesLabel = findViewById(R.id.categoriesLabel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (categoryAdapter != null) categoryAdapter.closeCursor();
        if (quizAdapter != null) quizAdapter.swapCursor(null);
    }
}
