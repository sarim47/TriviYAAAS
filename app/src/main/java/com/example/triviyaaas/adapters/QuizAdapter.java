package com.example.triviyaaas.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviyaaas.R;
import com.example.triviyaaas.ui.QuizActivity;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private Cursor cursor;
    private Context context;

    public QuizAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categories_quiz_item_list, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            String quizName = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));

            holder.quizName.setText(quizName);

            holder.itemView.setOnClickListener(v -> showQuizInputDialog(categoryId, quizName));
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor == newCursor) return;

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    private void showQuizInputDialog(int categoryId, String quizName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Quiz Details");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_quiz_input, null);

        EditText numberInput = dialogView.findViewById(R.id.numberInput);
        RadioGroup difficultyGroup = dialogView.findViewById(R.id.difficultyGroup);
        RadioButton easyRadioButton = dialogView.findViewById(R.id.easyRadio);
        RadioButton mediumRadioButton = dialogView.findViewById(R.id.mediumRadio);
        RadioButton hardRadioButton = dialogView.findViewById(R.id.hardRadio);

        builder.setView(dialogView)
                .setPositiveButton("Start", (dialog, which) -> {
                    String number = numberInput.getText().toString();
                    String difficulty = "";

                    int selectedId = difficultyGroup.getCheckedRadioButtonId();
                    if (selectedId == easyRadioButton.getId()) {
                        difficulty = "Easy";
                    } else if (selectedId == mediumRadioButton.getId()) {
                        difficulty = "Medium";
                    } else if (selectedId == hardRadioButton.getId()) {
                        difficulty = "Hard";
                    }

                    if (!number.isEmpty() && Integer.parseInt(number) >= 1 && Integer.parseInt(number) <= 10) {
                        startQuiz(categoryId, quizName, Integer.parseInt(number), difficulty);
                    } else {
                        Toast.makeText(context, "Please enter a number between 1 and 10", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        difficultyGroup.check(easyRadioButton.getId());

        builder.show();
    }

    private void startQuiz(int categoryId, String quizName, int number, String difficulty) {
        Intent intent = new Intent(context, QuizActivity.class);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra("quizName", quizName);
        intent.putExtra("number", number);
        intent.putExtra("difficulty", difficulty.toLowerCase());
        context.startActivity(intent);
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizName;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizName = itemView.findViewById(R.id.quizName);
        }
    }
}
