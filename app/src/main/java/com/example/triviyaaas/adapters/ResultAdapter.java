package com.example.triviyaaas.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviyaaas.R;
import com.example.triviyaaas.model.Result;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private List<Result> resultList;
    private Context context;

    public ResultAdapter(Context context, List<Result> resultList) {
        this.context = context;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_item_list, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Result result = resultList.get(position);

        holder.quizTitle.setText(result.getQuizTitle());
        holder.quizNumberTV.setText(result.getScore() + "/" + result.getTotalQuestions());
        holder.difficultyTV.setText(result.getDifficulty());

        // Optionally change color based on score
        int green = ContextCompat.getColor(context, R.color.colorSuccess);
        int red = ContextCompat.getColor(context, R.color.colorError);

        int percentage = (result.getScore() * 100) / result.getTotalQuestions();
        if (percentage >= 70) {
            holder.quizNumberTV.setTextColor(green);
        } else {
            holder.quizNumberTV.setTextColor(red);
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView quizTitle, quizNumberTV, difficultyTV;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.quizTitle);
            quizNumberTV = itemView.findViewById(R.id.quizNumberTV);
            difficultyTV = itemView.findViewById(R.id.difficultyTV);
        }
    }
}

