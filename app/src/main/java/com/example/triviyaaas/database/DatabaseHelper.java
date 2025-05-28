package com.example.triviyaaas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "trivia_app.db";
    public static final int DATABASE_VERSION = 1;

    // User table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FULL_NAME = "full_name";

    // Quiz tables
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_QUIZZES = "quizzes";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_SCORES = "scores";
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables as before
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_FULL_NAME + " TEXT)";
        db.execSQL(createUserTable);

        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quiz_count INTEGER DEFAULT 0, " +
                "image_name TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_QUIZZES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_id INTEGER, " +
                "title TEXT NOT NULL, " +
                "FOREIGN KEY(category_id) REFERENCES " + TABLE_CATEGORIES + "(id))");

        db.execSQL("CREATE TABLE " + TABLE_QUESTIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "quiz_id INTEGER, " +
                "question_text TEXT NOT NULL, " +
                "option_a TEXT, " +
                "option_b TEXT, " +
                "option_c TEXT, " +
                "option_d TEXT, " +
                "correct_option TEXT, " +
                "difficulty TEXT, " +
                "created_at INTEGER, " +
                "FOREIGN KEY(quiz_id) REFERENCES " + TABLE_QUIZZES + "(id))");

        db.execSQL("CREATE TABLE " + TABLE_SCORES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "quiz_id INTEGER, " +
                "quiz_name TEXT, " +
                "difficulty TEXT, " +
                "total_questions INTEGER, " +
                "score INTEGER, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

        importQuizDataFromFile(db, context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZZES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
        if (oldVersion < 2) { // assuming you add this column in version 2
            db.execSQL("ALTER TABLE questions ADD COLUMN created_at INTEGER DEFAULT 0");
        }
    }

    // ----------------------
    // USER METHODS
    // ----------------------

    public boolean insertUser(String email, String hashedPassword, String fullName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, hashedPassword);
        values.put(COLUMN_FULL_NAME, fullName);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        String hashedPassword = hashPassword(password);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ? AND password = ?",
                new String[]{email, hashedPassword});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean isUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?",
                new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getFullNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_FULL_NAME},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String fullName = cursor.getString(0);
            cursor.close();
            return fullName;
        }
        return null;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    // ----------------------
    // QUIZ HELPERS (basic)
    // ----------------------

    public Cursor getCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
    }

    public Cursor getQuizzesByCategory(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM quizzes WHERE category_id = ?", new String[]{String.valueOf(categoryId)});
    }

    public Cursor getQuestionsForQuiz(int categoryId, String quizName, int number, String difficulty) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT q.* FROM " + TABLE_QUESTIONS + " q " +
                "JOIN " + TABLE_QUIZZES + " z ON q.quiz_id = z.id " +
                "WHERE z.category_id = ? AND z.title = ? AND q.difficulty = ? " +
                "ORDER BY q.created_at DESC, RANDOM() LIMIT ?";
        return db.rawQuery(query, new String[]{
                String.valueOf(categoryId), quizName, difficulty, String.valueOf(number)
        });
    }

    public void saveQuizScore(String quizName, String difficulty, int totalQuestions, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quiz_name", quizName);
        values.put("difficulty", difficulty);
        values.put("total_questions", totalQuestions);
        values.put("score", score);
        values.put("timestamp", System.currentTimeMillis());

        long result = db.insert(TABLE_SCORES, null, values);
        if (result == -1) {
            Log.d("Database", "Failed to insert score for quiz: " + quizName);
        } else {
            Log.d("Database", "Successfully inserted score for quiz: " + quizName);
        }
    }


    public Cursor getAllScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCORES + " ORDER BY timestamp DESC", null);
        Log.d("Database", "Cursor count: " + cursor.getCount()); // This will show if any rows are returned
        return cursor;
    }

    // ----------------------
    // SQL FILE IMPORT METHODS
    // ----------------------

    public void importQuizDataFromFile(SQLiteDatabase db, Context context) {
        try {
            // Open the SQL file from assets
            InputStream is = context.getAssets().open("data_set.sql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sqlScript = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlScript.append(line).append("\n");
            }
            reader.close();

            // Execute the SQL script
            String[] sqlStatements = sqlScript.toString().split(";");
            db.beginTransaction();
            for (String statement : sqlStatements) {
                if (!statement.trim().isEmpty()) {
                    db.execSQL(statement);
                }
            }
            db.setTransactionSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}