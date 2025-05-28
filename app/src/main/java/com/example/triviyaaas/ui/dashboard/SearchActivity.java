package com.example.triviyaaas.ui.dashboard;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviyaaas.R;
import com.example.triviyaaas.adapters.SearchAdapter;
import com.example.triviyaaas.database.DatabaseHelper;
import com.example.triviyaaas.model.SearchItem;
import com.example.triviyaaas.ui.dashboard.bottomnav.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {
    private RecyclerView searchResultsRV;
    BottomNavigationView bottomNavigationView;

    private SearchAdapter searchAdapter;
    private DatabaseHelper dbHelper;
    AppCompatEditText searchInput;
    private List<SearchItem> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupBottomNavigation();
        setFloatingButtonAction();

        dbHelper = new DatabaseHelper(this);
        searchResults = new ArrayList<>();
        searchResultsRV = findViewById(R.id.searchResultsRV);
        searchAdapter = new SearchAdapter(this, searchResults);

        searchResultsRV.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRV.setAdapter(searchAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 3) {
                    // Perform search
                    searchForCategoriesAndQuizzes(charSequence.toString());
                } else {
                    // Clear search results when less than 3 characters
                    searchResults.clear();
                    searchAdapter.updateData(searchResults);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }


    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        searchInput = findViewById(R.id.searchEditText);
    }

    private void searchForCategoriesAndQuizzes(String query) {
        searchResults.clear();

        // Search Categories
        Cursor categoryCursor = dbHelper.getCategories();
        while (categoryCursor.moveToNext()) {
            @SuppressLint("Range") String categoryName = categoryCursor.getString(categoryCursor.getColumnIndex("name"));
            @SuppressLint("Range") int categoryId = categoryCursor.getInt(categoryCursor.getColumnIndex("id"));

            if (categoryName.toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(new SearchItem(categoryId, categoryName));
            }
        }
        categoryCursor.close();

        searchAdapter.updateData(searchResults);
    }
}