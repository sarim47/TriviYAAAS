package com.example.triviyaaas.model;

public class Result {
    private String quizTitle;
    private String difficulty;
    private int score;
    private int totalQuestions;

    public Result(String quizTitle, String difficulty, int score, int totalQuestions) {
        this.quizTitle = quizTitle;
        this.difficulty = difficulty;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }

    public String getQuizTitle() { return quizTitle; }
    public String getDifficulty() { return difficulty; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
}
